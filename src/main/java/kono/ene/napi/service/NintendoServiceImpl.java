package kono.ene.napi.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import kono.ene.napi.config.GlobalConfigurage;
import kono.ene.napi.constant.MongoField;
import kono.ene.napi.dao.entity.*;
import kono.ene.napi.dao.repository.*;
import kono.ene.napi.exception.BaseRuntimeException;
import kono.ene.napi.request.AccountAccessTokenRequest;
import kono.ene.napi.request.SessionRequest;
import kono.ene.napi.request.UserInfoRequest;
import kono.ene.napi.request.WebServiceRequest;
import kono.ene.napi.response.ns.WebServiceAccessTokenResponse;
import kono.ene.napi.util.Misc;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static kono.ene.napi.constant.MongoField.*;
import static kono.ene.napi.util.Misc.doGet;
import static kono.ene.napi.util.Misc.doPost;

@Slf4j
@Service
public class NintendoServiceImpl implements NintendoService {
    @Resource
    private NintendoCodeChallengeDao nintendoCodeChallengeDao;
    @Resource
    private NintendoAuthDao nintendoAuthDao;
    @Resource
    private NintendoUserDao nintendoUserDao;
    @Resource
    private NintendoSwitchUserDao nintendoSwitchUserDao;
    @Resource
    private NintendoGlobalConfigDao nintendoGlobalConfigDao;
    @Resource
    private NintendoSwitchWebAccessTokenDao nintendoSwitchWebAccessTokenDao;
    @Resource
    @Qualifier("globalConfigDTO")
    private GlobalConfigurage.GlobalConfigDTO globalConfig;

    @Resource
    private MongoTemplate mongoTemplate;


    @SneakyThrows
    @Override
    public String loginChallenge(Integer qid) {
        // Generate a random state value
        SecureRandom secureRandom = new SecureRandom();
        byte[] authStateBytes = new byte[36];
        secureRandom.nextBytes(authStateBytes);
        String authState = Base64.getUrlEncoder().withoutPadding().encodeToString(authStateBytes);

        // Generate a random code verifier
        byte[] authCodeVerifierBytes = new byte[32];
        secureRandom.nextBytes(authCodeVerifierBytes);
        String authCodeVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(authCodeVerifierBytes);

        // Generate the code challenge using the S256 method
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        sha256.update(authCodeVerifier.getBytes());
        byte[] authCodeChallengeBytes = sha256.digest();
        String authCodeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(authCodeChallengeBytes);

        // Build the login challenge URL
        Map<String, Object> params = new HashMap<>();
        params.put("client_id", "71b963c1b7b6d119");
        params.put("redirect_uri", "npf" + "71b963c1b7b6d119" + "://auth");
        params.put("response_type", "session_token_code");
        params.put("scope", "openid user user.birthday user.mii user.screenName");
        params.put("session_token_code_challenge", authCodeChallenge.replace("=", ""));
        params.put("session_token_code_challenge_method", "S256");
        params.put("state", authState);
        params.put("theme", "login_form");

        String url = "https://accounts.nintendo.com/connect/1.0.0/authorize?" + URLUtil.buildQuery(params, Charset.defaultCharset());
        Date now = new Date();
        mongoTemplate.findAndModify(
                Query.query(Criteria.where(MongoField.QID).is(qid)),
                Update.update("verify", authCodeVerifier).set("url", url).set(MongoField.UPDATE_TIME, now).setOnInsert(MongoField.CREATE_TIME, now),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                CodeChallengeDo.class);

        return url;
    }


    private String decodeUrl(String url) {
        Pattern pattern = Pattern.compile("(eyJhbGciOiJIUzI1NiJ9\\.[a-zA-Z0-9_-]*\\.[a-zA-Z0-9_-]*)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new RuntimeException("url malformed");
    }

    @Override
    public String sessionToken(SessionRequest sessionRequest) {
        CodeChallengeDo nintendoCodeChallenge = nintendoCodeChallengeDao.findByQid(sessionRequest.getQid());
        if (nintendoCodeChallenge == null) {
            return null;
        } else if (!StringUtils.hasLength(nintendoCodeChallenge.getSessionToken())) {
            String code = decodeUrl(sessionRequest.getRedirect_url());

            Map<String, String> headers = new HashMap<>();
            headers.put("Accept-Encoding", "gzip");
            headers.put("User-Agent", "OnlineLounge/" + globalConfig.getAppVersion() + " NASDKAPI Android");
            headers.put("Accept-Language", "en-US");
            headers.put("Accept", "application/json");
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            headers.put("Content-Length", "540");
            headers.put("Host", "accounts.nintendo.com");
            headers.put("Connection", "Keep-Alive");

            Map<String, Object> params = new HashMap<>();
            params.put("client_id", "71b963c1b7b6d119");
            params.put("session_token_code", code);
            params.put("session_token_code_verifier", nintendoCodeChallenge.getVerify().replace("=", ""));

            String url = "https://accounts.nintendo.com/connect/1.0.0/api/session_token";
            String response = doPost(url, params, headers);
            String sessionToken = JSONUtil.parseObj(response).getStr("session_token");
            nintendoCodeChallenge.setSessionToken(sessionToken);

            mongoTemplate.updateFirst(
                    Query.query(Criteria.where(MongoField.QID).is(sessionRequest.getQid())),
                    Update.update(MongoField.SESSION_TOKEN, sessionToken).set(MongoField.UPDATE_TIME, new Date()),
                    CodeChallengeDo.class);

            return sessionToken;
        } else {
            return nintendoCodeChallenge.getSessionToken();
        }
    }

    @Override
    public String refreshAccessToken(AccountAccessTokenRequest accessTokenRequest) {
        AuthDo nintendoAuth = nintendoAuthDao.findByQid(accessTokenRequest.getQid());
        if (!isAccessTokenExpired(nintendoAuth)) {
            return nintendoAuth.getAccessToken();
        }

        CodeChallengeDo codeChallengeDo = nintendoCodeChallengeDao.findByQid(accessTokenRequest.getQid());
        if (codeChallengeDo == null || !StringUtils.hasLength(codeChallengeDo.getSessionToken())) {
            throw new BaseRuntimeException(40000, "session token not found");
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Coral/" + globalConfig.getAppVersion() + " (com.nintendo.znca; build:1999; iOS 15.5.0) Alamofire/5.4.4");
        headers.put("Accept", "application/json");
        headers.put("Accept-Language", "en-US");
        headers.put("Accept-Encoding", "gzip, deflate");

        Map<String, Object> params = new HashMap<>();
        params.put("client_id", "71b963c1b7b6d119");
        params.put("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer-session-token");
        params.put("session_token", codeChallengeDo.getSessionToken());

        String url = "https://accounts.nintendo.com/connect/1.0.0/api/token";
        String response = doPost(url, params, headers);
        JSONObject responseJson = JSONUtil.parseObj(response);
        String accessToken = responseJson.getStr("access_token");
        String idToken = responseJson.getStr("id_token");
        Integer expiresIn = responseJson.getInt("expires_in");
        Date refreshTime = new Date();

        AuthDo authDo = mongoTemplate.findAndModify(
                Query.query(Criteria.where(MongoField.QID).is(accessTokenRequest.getQid())),
                Update.update(MongoField.ACCESS_TOKEN, accessToken)
                        .set(MongoField.ID_TOKEN, idToken)
                        .set(MongoField.EXPIRES_IN, expiresIn)
                        .set(MongoField.REFRESH_TIME, refreshTime)
                        .set(MongoField.UPDATE_TIME, new Date())
                        .setOnInsert(MongoField.CREATE_TIME, new Date()),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                AuthDo.class);

        assert authDo != null;
        return authDo.getAccessToken();
    }

    private boolean isAccessTokenExpired(AuthDo authDo) {
        if (authDo == null || authDo.getAccessToken() == null || authDo.getRefreshTime() == null || authDo.getExpiresIn() == null) {
            return true;
        }
        return authDo.getRefreshTime().getTime() + authDo.getExpiresIn() * 1000
                < System.currentTimeMillis();
    }

    @Override
    public UserDo userInfo(UserInfoRequest userInfo) {
        Integer qid = userInfo.getQid();
        AuthDo authDo = mongoTemplate.findOne(Query.query(Criteria.where(MongoField.QID).is(qid)), AuthDo.class);
        String accessToken;
        if (isAccessTokenExpired(authDo)) {
            accessToken = refreshAccessToken(new AccountAccessTokenRequest(qid));
        } else {
            accessToken = authDo.getAccessToken();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Coral/2.5.2 (com.nintendo.znca; build:1999; iOS 15.5.0) Alamofire/5.4.4");
        headers.put("Accept", "application/json");
        headers.put("Accept-Language", "zh-CN");
        headers.put("Authorization", "Bearer " + accessToken);
        headers.put("Host", "api.accounts.nintendo.com");
        headers.put("Connection", "Keep-Alive");
        headers.put("Accept-Encoding", "gzip");

        String url = "https://api.accounts.nintendo.com/2.0.0/users/me";
        String response = doGet(url, headers);

        return mongoTemplate.findAndModify(
                Query.query(Criteria.where(MongoField.QID).is(qid)),
                Update.fromDocument(Document.parse(response))
                        .set(MongoField.UPDATE_TIME, new Date())
                        .setOnInsert(MongoField.CREATE_TIME, new Date()),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                UserDo.class
        );
    }

    @Override
    public SwitchUserDo nintendo_switch_account(UserInfoRequest userInfo) {
        Integer qid = userInfo.getQid();
        AuthDo authDo = nintendoAuthDao.findByQid(qid);
        UserDo userDo = nintendoUserDao.findByQid(qid);

        String uuid = UUID.randomUUID().toString().replace("-", "");
        String accessToken = authDo.getAccessToken();
        String language = userDo.getLanguage();

        int f_step = 1;
        Misc.FApiResult fResult = Misc.callFApi(accessToken, f_step, uuid, userDo.getId());

        String f = fResult.getF();
        String f_timestamp = fResult.getTimestamp();
        String f_request_id = fResult.getRequest_id();

        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "api-lp1.znc.srv.nintendo.net");
        headers.put("Accept-Language", language);
        headers.put("User-Agent", "com.nintendo.znca/" + globalConfig.getAppVersion() + " (Android/7.1.2)");
        headers.put("Accept", "application/json");
        headers.put("X-ProductVersion", globalConfig.getAppVersion());
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Connection", "Keep-Alive");
        headers.put("Authorization", "Bearer");
        headers.put("X-Platform", "Android");
        headers.put("Accept-Encoding", "gzip");

        String bodyJsonStr = JSONUtil.toJsonStr(MapUtil.builder()
                .put("parameter", MapUtil.builder()
                        .put("f", f)
                        .put("naIdToken", accessToken)
                        .put("timestamp", Long.parseLong(f_timestamp))
                        .put("requestId", f_request_id)
                        .put("naCountry", userDo.getCountry())
                        .put("naBirthday", userDo.getBirthday())
                        .put("language", language)
                        .build())
                .build());

        String url = "https://api-lp1.znc.srv.nintendo.net/v3/Account/Login";
        String response = doPost(url, null, headers, bodyJsonStr);

        JSONObject responseJson = JSONUtil.parseObj(response);
        Integer status = responseJson.getInt("status");
        if (status == 9404) {
            log.warn("nintendo accessToken expired, response: {}", response);
            throw new RuntimeException("nintendo_switch_account token expired");
        } else if (status != 0) {
            log.error("nintendo_switch_account error, response: {}", response);
            throw new RuntimeException("nintendo_switch_account error");
        }

        JSONObject result = responseJson.getJSONObject("result");
        JSONObject profile = result.getJSONObject("user").putOnce(QID, qid);

        Set<String> webApiCredential = result.getJSONObject(WEB_API_SERVER_CREDENTIAL).keySet();
        Set<String> fc_keys = result.getJSONObject(FIREBASE_CREDENTIAL).keySet();

        Update update = Update.fromDocument(Document.parse(profile.toString()))
                .set(UPDATE_TIME, new Date())
                .set(F_RESULT + "." + F_STEP, f_step)
                .set(F_RESULT + "." + REQUEST_ID, f_request_id)
                .set(F_RESULT + "." + TIMESTAMP, f_timestamp)
                .set(F_RESULT + "." + F, f)
                .setOnInsert(CREATE_TIME, new Date());

        for (String key : webApiCredential) {
            update.set(WEB_API_SERVER_CREDENTIAL + "." + key, result.getJSONObject(WEB_API_SERVER_CREDENTIAL).get(key));
        }
        for (String key : fc_keys) {
            update.set(FIREBASE_CREDENTIAL + "." + key, result.getJSONObject(FIREBASE_CREDENTIAL).get(key));
        }

        update.set(WEB_API_SERVER_CREDENTIAL + "." + UPDATE_TIME, new Date())
                .set(FIREBASE_CREDENTIAL + "." + UPDATE_TIME, new Date())
                .setOnInsert(WEB_API_SERVER_CREDENTIAL + "." + CREATE_TIME, new Date())
                .setOnInsert(FIREBASE_CREDENTIAL + "." + CREATE_TIME, new Date());

        SwitchUserDo switchUser = mongoTemplate.findAndModify(
                Query.query(Criteria.where(MongoField.QID).is(qid)),
                update,
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                SwitchUserDo.class);

        assert switchUser != null;
        return switchUser;
    }

    @Override
    public WebServiceAccessTokenResponse web_service_token(WebServiceRequest webServiceRequest) {
        Integer qid = webServiceRequest.getQid();
        Optional<GlobalConfigDo> nintendoGlobalConfig = nintendoGlobalConfigDao.findById("nintendo_global_config");
        if (nintendoGlobalConfig.isPresent()) {
            GlobalConfigDo configDo = nintendoGlobalConfig.get();
            final AtomicReference<GlobalConfigDo.WebService> webService = new AtomicReference<>();
            configDo.getWebServices().stream()
                    .filter(ws -> ws.getName().equals(webServiceRequest.getGameStr()))
                    .findFirst().ifPresent(webService::set);
            GlobalConfigDo.WebService webServiceGet = webService.get();
            if (webServiceGet == null) {
                throw new RuntimeException("web_service_token gameStr error");
            }
            var webAccessToken = nintendoSwitchWebAccessTokenDao.findByQidAndGameId(qid, webServiceGet.getId());
            if (webAccessToken != null) {
                // check expire
                if (webAccessToken.getRefreshTime().getTime() + webAccessToken.getExpiresIn() * 1000 > System.currentTimeMillis()) {
                    return WebServiceAccessTokenResponse.builder()
                            .qid(qid).gameId(webServiceGet.getId()).gameName(webServiceGet.getName())
                            .gameUri(webServiceGet.getUri()).accessToken(webAccessToken.getAccessToken())
                            .imageUri(webServiceGet.getImageUri()).build();
                }
            }
            UserDo userDo = nintendoUserDao.findByQid(qid);
            SwitchUserDo switchUserDo = nintendoSwitchUserDao.findByQid(qid);
            SwitchUserDo.FResult fResult = switchUserDo.getFResult();
            String accessToken = switchUserDo.getWebApiServerCredential().getAccessToken();

            int f_step = 2;
            Misc.FApiResult fApiResult = Misc.callFApi(accessToken, f_step, fResult.getRequestId(), userDo.getId());

            Map<String, String> headers = new HashMap<>();
            headers.put("X-Platform", "Android");
            headers.put("X-ProductVersion", configDo.getAppVersion());
            headers.put("Authorization", "Bearer " + accessToken);
            headers.put("Content-Type", "application/json; charset=utf-8");
            headers.put("Content-Length", "391");
            headers.put("Accept-Encoding", "gzip");
            headers.put("User-Agent", "com.nintendo.znca/" + configDo.getAppVersion() + "(Android/7.1.2)");

            Map<String, Object> parameter = new HashMap<>();
            parameter.put("f", fApiResult.getF());
            parameter.put("id", webServiceGet.getId());
            parameter.put("registrationToken", accessToken);
            parameter.put("requestId", fApiResult.getRequest_id());
            parameter.put("timestamp", fApiResult.getTimestamp());

            Map<String, Object> body = new HashMap<>();
            body.put("parameter", parameter);

            String url = "https://api-lp1.znc.srv.nintendo.net/v2/Game/GetWebServiceToken";

            String response = doPost(url, headers, JSONUtil.toJsonStr(body));
            JSONObject result = JSONUtil.parseObj(response);
            if (result.getInt("status") != 0) {
                log.error("web_service_token error, response: {}", result);
                throw new BaseRuntimeException(40002, "web_service_token error");
            }
            JSONObject webServiceTokenJson = result.getJSONObject("result");

            Date now = new Date();
            Query query = Query.query(Criteria.where(QID).is(qid).and(GAME_ID).is(webServiceGet.getId()));
            Update update = Update.update(ACCESS_TOKEN, webServiceTokenJson.getStr("accessToken"))
                    .set(EXPIRES_IN, webServiceTokenJson.getInt("expiresIn"))
                    .set(UPDATE_TIME, now)
                    .set(REFRESH_TIME, now)
                    .set(LANGUAGE, userDo.getLanguage())
                    .set(COUNTRY, userDo.getCountry())
                    .setOnInsert(GAME_ID, webServiceGet.getId())
                    .setOnInsert(GAME_URI, webServiceGet.getUri())
                    .setOnInsert(GAME_NAME, webServiceGet.getName())
                    .setOnInsert(IMAGE_URI, webServiceGet.getImageUri())
                    .setOnInsert(QID, qid)
                    .setOnInsert(CREATE_TIME, now);
            var switchWebAccessTokenDo = mongoTemplate.findAndModify(query, update,
                    FindAndModifyOptions.options().upsert(true).returnNew(true),
                    WebAccessTokenDo.class);

            log.info("web_service_token result:{}", switchWebAccessTokenDo);

            Assert.notNull(switchWebAccessTokenDo, "web_service_token error");
            return WebServiceAccessTokenResponse.builder()
                    .qid(qid).gameId(switchWebAccessTokenDo.getGameId()).gameName(switchWebAccessTokenDo.getGameName())
                    .gameUri(switchWebAccessTokenDo.getGameUri()).accessToken(switchWebAccessTokenDo.getAccessToken())
                    .imageUri(switchWebAccessTokenDo.getImageUri()).build();
        }
        throw new BaseRuntimeException(40002, "web_service_token error");
    }
}
