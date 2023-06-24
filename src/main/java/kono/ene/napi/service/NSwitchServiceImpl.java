package kono.ene.napi.service;

import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.Resource;
import kono.ene.napi.config.GlobalConfigurage;
import kono.ene.napi.dao.entity.GlobalConfigDo;
import kono.ene.napi.dao.entity.SwitchUserDo;
import kono.ene.napi.dao.repository.NintendoGlobalConfigDao;
import kono.ene.napi.dao.repository.NintendoSwitchUserDao;
import kono.ene.napi.response.ns.*;
import kono.ene.napi.util.Misc;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static kono.ene.napi.constant.MongoField.*;

@Slf4j
@Service
public class NSwitchServiceImpl implements NSwitchService {
    @Resource
    private NintendoSwitchUserDao nintendoSwitchUserDao;
    @Resource
    private NintendoGlobalConfigDao nintendoGlobalConfigDao;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    @Qualifier("globalConfigDTO")
    private GlobalConfigurage.GlobalConfigDTO globalConfig;

    private HttpResponse do_znc_call(Long qid, String path, Map<String, Object> params) {
        SwitchUserDo switchUserDo = nintendoSwitchUserDao.findByQid(qid);
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        String url = "https://api-lp1.znc.srv.nintendo.net" + path;
        String guid = UUID.randomUUID().toString();
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "com.nintendo.znca/" + globalConfig.getAppVersion() + " (Android/7.1.2)");
        headers.put("Accept-Encoding", "gzip");
        headers.put("Accept", "application/json");
        headers.put("Connection", "Keep-Alive");
        headers.put("Host", "api-lp1.znc.srv.nintendo.net");
        headers.put("X-ProductVersion", globalConfig.getAppVersion());
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", "Bearer " + switchUserDo.getWebApiServerCredential().getAccessToken());

        Map<String, Object> body = new HashMap<>();
        body.put("parameter", params);
        body.put("requestId", guid);

        return Misc.doPost(url, JSONUtil.toJsonStr(body), headers);
    }

    @Override
    public WebServicesResponse listWebServices(Long qid) {
        WebServicesResponse.WebServicesResponseBuilder response = WebServicesResponse.builder().qid(qid);
        try (HttpResponse httpResponse = do_znc_call(qid, "/v1/Game/ListWebServices", null)) {
            String body = httpResponse.body();
            JSONObject jsonObject = JSONUtil.parseObj(body);
            if (jsonObject.getInt(STATUS) == 0) {
                JSONArray webServices = jsonObject.getJSONArray(RESULT);
                List<GlobalConfigDo.WebService> webServiceList = JSONUtil.toList(webServices, GlobalConfigDo.WebService.class);
                List<WebServicesResponse.WebService> webServicesResponseList = JSONUtil.toList(webServices, WebServicesResponse.WebService.class);
                // TODO 全网络服务配置
//                NintendoGlobalConfigDo nintendoGlobalConfigDo = mongoTemplate.findAndModify(
//                        Query.query(Criteria.where("_id").is("nintendo_global_config")),
//                        Update.update("webServices", webServiceList)
//                                .set(UPDATE_TIME, new Date())
//                                .setOnInsert(CREATE_TIME, new Date()),
//                        FindAndModifyOptions.options().returnNew(true).upsert(true),
//                        NintendoGlobalConfigDo.class);
                response.webServices(webServicesResponseList);
                log.info("listWebServices success, webServices: {}", webServices);
            } else {
                log.warn("listWebServices failed");
            }
        }
        return response.build();
    }

    @Override
    public void announcements(Long qid) {
        try (HttpResponse httpResponse = do_znc_call(qid, "/v1/Announcement/List", null)) {
            String body = httpResponse.body();
            log.info("announcements success, body: {}", body);
        }
    }

    @Override
    public FriendsResponse friendsList(Long qid) {
        FriendsResponse.FriendsResponseBuilder response = FriendsResponse.builder().qid(qid);
        try (HttpResponse httpResponse = do_znc_call(qid, "/v3/Friend/List", null)) {
            String body = httpResponse.body();
            log.info("friendsList success, body text: {}", body);
            JSONObject resJsonObj = JSONUtil.parseObj(body);
            if (resJsonObj.getInt(STATUS) == 0) {
                JSONArray friends = resJsonObj.getByPath(RESULT + "." + FRIENDS, JSONArray.class);
                List<FriendsResponse.FriendsDTO> friendList = JSONUtil.toList(friends, FriendsResponse.FriendsDTO.class);
                List<SwitchUserDo.FriendsDTO> friendsDTOList = JSONUtil.toList(friends, SwitchUserDo.FriendsDTO.class);

                UpdateResult updateResult = nintendoSwitchUserDao.updateFirst(
                        new Document(QID, qid),
                        new Document("$set", new Document(FRIENDS, friendsDTOList))
                                .append("$setOnInsert", new Document(CREATE_TIME, new Date()))
                                .append("$currentDate", new Document(UPDATE_TIME, true)));
                if (updateResult.getMatchedCount() == 0) {
                    log.warn("friendsList failed, qid: {}", qid);
                } else {
                    log.info("friendsList success, qid: {}", qid);
                    response.friends(friendList);
                }
            }
        }
        return response.build();
    }

    @Override
    public FriendCodeUrlResponse createFriendCodeUrl(Long qid) {
        // TODO expire time figure out
        FriendCodeUrlResponse.FriendCodeUrlResponseBuilder response = FriendCodeUrlResponse.builder().qid(qid);
        try (HttpResponse httpResponse = do_znc_call(qid, "/v3/Friend/CreateFriendCodeUrl", null)) {
            String body = httpResponse.body();
            JSONObject resJsonObj = JSONUtil.parseObj(body);
            log.info("create friend code url success, body json: {}", resJsonObj);
            if (resJsonObj.getInt(STATUS) == 0) {
//                FriendCodeUrlResponse json = resJsonObj.getJSONObject(RESULT).toBean(FriendCodeUrlResponse.class);
                response.friendCode(resJsonObj.getByPath(RESULT + "." + FRIEND_CODE, String.class));
                response.url(resJsonObj.getByPath(RESULT + "." + URL, String.class));
                log.info("create friend code url success, res: {}", response);
            }
        }
        return response.build();
    }

    @Override
    public SwitchUserSelfResponse userSelf(Long qid) {
        SwitchUserSelfResponse.SwitchUserSelfResponseBuilder response = SwitchUserSelfResponse.builder().qid(qid);
        try (HttpResponse httpResponse = do_znc_call(qid, "/v3/User/ShowSelf", null)) {
            String body = httpResponse.body();
            JSONObject resJsonObj = JSONUtil.parseObj(body);
            log.info("userSelf success, body json: {}", resJsonObj);
            if (resJsonObj.getInt(STATUS) == 0) {
                JSONObject result = resJsonObj.getJSONObject(RESULT);
                SwitchUserDo switchUserDo = result.toBean(SwitchUserDo.class);
                switchUserDo.setQid(qid);
                UpdateResult updateResult = nintendoSwitchUserDao.updateFirst(
                        new Document(QID, qid),
                        new Document("$set", switchUserDo)
                                .append("$setOnInsert", new Document(CREATE_TIME, new Date()))
                                .append("$currentDate", new Document(UPDATE_TIME, true)));

                if (updateResult.getMatchedCount() == 0) {
                    log.warn("userSelf failed, qid: {}", qid);
                } else {
                    SwitchUserSelfResponse switchUserSelfResponse = result.toBean(SwitchUserSelfResponse.class);
                    switchUserSelfResponse.setQid(qid);
                    log.info("userSelf success, res: {}", switchUserSelfResponse);
                    return switchUserSelfResponse;
                }
            }
        }
        return response.build();
    }


    private static String formatFriendCode(String friendCode) {
        Pattern friendCodePattern = Pattern.compile("^(?:SW-)?([0-9]{4})[- ]?([0-9]{4})[- ]?([0-9]{4})$");
        Matcher matcher = friendCodePattern.matcher(friendCode);
        if (matcher.find()) {
            return String.format("%s-%s-%s", matcher.group(1), matcher.group(2), matcher.group(3));
        }
        throw new RuntimeException("malformed friend code");
    }

    @Override
    public FriendResponse userByFriendCode(Long qid, String friendCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("friendCode", formatFriendCode(friendCode));
        try (HttpResponse httpResponse = do_znc_call(qid, "/v3/Friend/GetUserByFriendCode", params)) {
            String body = httpResponse.body();
            JSONObject resJsonObj = JSONUtil.parseObj(body);
            log.info("userByFriendCode success, body json: {}", resJsonObj);
            if (resJsonObj.getInt(STATUS) == 0) {
                JSONObject result = resJsonObj.getJSONObject(RESULT);
                log.info("userByFriendCode success, res: {}", result);
                return result.toBean(FriendResponse.class);
            }
        }
        return new FriendResponse();
    }

    @Override
    public void sendFriendRequest(Long qid, String friendCode) {
        FriendResponse friendResponse = userByFriendCode(qid, friendCode);
        if (friendResponse.getNsaId() != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("nsaId", friendResponse.getNsaId());
            try (HttpResponse httpResponse = do_znc_call(qid, "/v3/FriendRequest/Create", params)) {
                String body = httpResponse.body();
                JSONObject resJsonObj = JSONUtil.parseObj(body);
                log.info("sendFriendRequest success, body json: {}", resJsonObj);
                if (resJsonObj.getInt(STATUS) == 0) {
                    log.info("sendFriendRequest success, res: {}", resJsonObj);
                } else {
                    // if blocked, the request will be blocked until unblocked
                    // {"status": 9467, "errorMessage": "Already friend error."}
                    // {"status": 9464, "errorMessage": "Duplicate friend request error."}
                    log.warn("sendFriendRequest failed, res: {}", resJsonObj);
                }
            }
        } else {
            log.warn("sendFriendRequest failed, friend not found, friendCode: {}", friendCode);
        }
    }
}
