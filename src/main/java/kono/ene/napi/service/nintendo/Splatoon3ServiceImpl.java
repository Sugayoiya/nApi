package kono.ene.napi.service.nintendo;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import kono.ene.napi.config.GlobalConfiguration;
import kono.ene.napi.dao.entity.WebAccessTokenDo;
import kono.ene.napi.dao.repository.NintendoGlobalConfigDao;
import kono.ene.napi.dao.repository.NintendoSwitchUserDao;
import kono.ene.napi.dao.repository.NintendoSwitchWebAccessTokenDao;
import kono.ene.napi.dao.repository.NintendoUserDao;
import kono.ene.napi.exception.BusinessException;
import kono.ene.napi.response.splat3.BattleHistoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static kono.ene.napi.constant.MongoField.*;
import static kono.ene.napi.constant.SplatoonGraphQL.*;

@Slf4j
@Service
public class Splatoon3ServiceImpl implements Splatoon3Service {
    @Resource
    private NintendoSwitchUserDao nintendoSwitchUserDao;
    @Resource
    private NintendoSwitchWebAccessTokenDao switchWebAccessTokenDao;
    @Resource
    private NintendoGlobalConfigDao nintendoGlobalConfigDao;
    @Resource
    private NintendoUserDao nintendoUserDao;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    @Qualifier("globalConfigDTO")
    private GlobalConfiguration.GlobalConfigDTO globalConfig;
    private static final long SPLATOON3_ID = 4834290508791808L;

    private GlobalConfiguration.GlobalConfigDTO.ServiceConfig getSplatoon3ServiceConfig() {
        List<GlobalConfiguration.GlobalConfigDTO.ServiceConfig> webServices = globalConfig.getWebServices();
        for (GlobalConfiguration.GlobalConfigDTO.ServiceConfig webService : webServices) {
            if (webService.getId() == SPLATOON3_ID) {
                return webService;
            }
        }
        throw new BusinessException("splatoon3 service config not found");
    }

    @Override
    public String bulletToken(Long qid) {

        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
        Assert.notNull(webAccessTokenDo, "webAccessTokenDo is null");
        var nintendoUserDo = nintendoUserDao.findByQid(qid);
        Assert.notNull(nintendoUserDo, "nintendoUserDo is null");

        var splatoon3ServiceConfig = getSplatoon3ServiceConfig();
        var url = splatoon3ServiceConfig.getUrl();
        var host = URLUtil.url(url).getHost();
        var fullUrl = URLUtil.completeUrl(url, "/api/bullet_tokens");

        Map<String, String> appHead = new HashMap<>();
        appHead.put("Host", host);
        appHead.put("User-Agent", "com.nintendo.znca/" + globalConfig.getAppVersion() + " (Android/7.1.2)");
        appHead.put("Content-Type", "application/json; charset=utf-8");
        appHead.put("X-Platform", "Android");
        appHead.put("X-Web-View-Ver", globalConfig.getWebViewVersion());
        appHead.put("X-NACOUNTRY", nintendoUserDo.getCountry());
        appHead.put("Accept-Language", "en-US");
        appHead.put("X-GameWebToken", webAccessTokenDo.getAccessToken());
        appHead.put("Connection", "keep-alive");

        try (HttpResponse httpResponse = HttpRequest.post(fullUrl).headerMap(appHead, true).body("").execute()) {
            if (httpResponse.getStatus() >= 200 && httpResponse.getStatus() < 300) {
                String response = httpResponse.body();
                JSONObject jsonObject = JSONUtil.parseObj(response);
                log.info("bulletToken response: {}", jsonObject);
                String bulletToken = jsonObject.getStr("bulletToken");
                WebAccessTokenDo switchWebAccessTokenDo = mongoTemplate.findAndModify(
                        new Query(Criteria.where(QID).is(qid).and(GAME_ID).is(SPLATOON3_ID)),
                        new Update().set(BULLET_TOKEN, bulletToken).currentDate(UPDATE_TIME),
                        FindAndModifyOptions.options().returnNew(true).upsert(true),
                        WebAccessTokenDo.class
                );
                return Objects.requireNonNull(switchWebAccessTokenDo).getBulletToken();
            } else {
                throw new BusinessException("bulletToken error: " + httpResponse.getStatus() + " " + httpResponse.body());
            }
        }
    }

    @Override
    public JSONObject get_tw_history_list(Long qid) {
        WebAccessTokenDo webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(REGULAR_BATTLE_HISTORIES_QUERY);
        return createGraphqlRequest(webAccessTokenDo, RegularBattleHistoriesQuery, new HashMap<>());
    }

    @Override
    public BattleHistoryResponse get_battle_history_list(Long qid) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(LATEST_BATTLE_HISTORIES_QUERY);
        JSONObject jsonObject = createGraphqlRequest(webAccessTokenDo, LatestBattleHistoriesQuery, new HashMap<>());
        return JSONUtil.toBean(jsonObject, BattleHistoryResponse.class);
    }

    @Override
    public JSONObject get_battle_history_detail(Long qid, String id) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(VS_HISTORY_DETAIL_QUERY);
        return createGraphqlRequest(webAccessTokenDo, VsHistoryDetailQuery, new HashMap<>(Map.of("vsResultId", id)));
    }

    @Override
    public JSONObject get_stage_schedule(Long qid) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(STAGE_SCHEDULE_QUERY);
        return createGraphqlRequest(webAccessTokenDo, StageScheduleQuery, new HashMap<>());
    }

    @Override
    public JSONObject get_player_stats_simple(Long qid) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(CONFIGURE_ANALYTICS_QUERY);
        return createGraphqlRequest(webAccessTokenDo, ConfigureAnalyticsQuery, new HashMap<>());
    }

    @Override
    public JSONObject get_player_stats_full(Long qid) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(HISTORY_RECORD_QUERY);
        return createGraphqlRequest(webAccessTokenDo, HistoryRecordQuery, new HashMap<>());
    }

    @Override
    public JSONObject get_salmon_run_stats(Long qid) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(COOP_HISTORY_QUERY);
        return createGraphqlRequest(webAccessTokenDo, CoopHistoryQuery, new HashMap<>());
    }


    @Override
    public JSONObject get_current_splatfest(Long qid) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(USE_CURRENT_FEST_QUERY);
        return createGraphqlRequest(webAccessTokenDo, CurrentFestQuery, new HashMap<>());
    }

    @Override
    public JSONObject get_splatfest_list(Long qid) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(FEST_RECORD_QUERY);
        return createGraphqlRequest(webAccessTokenDo, FestRecordQuery, new HashMap<>());
    }

    @Override
    public JSONObject get_weapon_stats(Long qid) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(WEAPON_RECORD_QUERY);
        return createGraphqlRequest(webAccessTokenDo, WeaponRecordQuery, new HashMap<>());
    }

    @Override
    public JSONObject get_fits(Long qid) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(MY_OUTFITS_QUERY);
        return createGraphqlRequest(webAccessTokenDo, MyOutfitsQuery, new HashMap<>());
    }

    @Override
    public JSONObject get_maps_stats(Long qid) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(STAGE_RECORD_QUERY);
        return createGraphqlRequest(webAccessTokenDo, StageRecordQuery, new HashMap<>());
    }

    @Override
    public JSONObject do_store_order(Long qid, String id, Boolean confirm) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(SALE_GEAR_DETAIL_ORDER_GESOTOWN_GEAR_MUTATION);
        return createGraphqlRequest(webAccessTokenDo, SaleGearDetailOrderGesotownGearMutation, new HashMap<>(Map
                .of("input", new HashMap<>(Map.of("id", id, "isForceOrder", confirm)))));
    }

    @Override
    public JSONObject get_store_items(Long qid) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(GESOTOWN_QUERY);
        return createGraphqlRequest(webAccessTokenDo, GesotownQuery, new HashMap<>());
    }

    @Override
    public JSONObject get_single_player_stats(Long qid) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(HERO_HISTORY_QUERY);
        return createGraphqlRequest(webAccessTokenDo, HeroHistoryQuery, new HashMap<>());
    }

    @Override
    public JSONObject get_species_cur_weapon(Long qid) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(BATTLE_HISTORY_CURRENT_PLAYER_QUERY);
        return createGraphqlRequest(webAccessTokenDo, BattleHistoryCurrentPlayerQuery, new HashMap<>());
    }

    @Override
    public JSONObject get_sr_history_list(Long qid) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(REFETCHABLE_COOP_HISTORY_COOP_RESULT_QUERY);
        return createGraphqlRequest(webAccessTokenDo, RefetchableCoopHistory_CoopResultQuery, new HashMap<>());
    }

    @Override
    public JSONObject get_sr_history_detail(Long qid, String id) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(COOP_HISTORY_DETAIL_QUERY);
        return createGraphqlRequest(webAccessTokenDo, CoopHistoryDetailQuery, new HashMap<>(Map.of("coopHistoryDetailId", id)));
    }

    @Override
    public JSONObject get_outfits_common_data(Long qid) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(MY_OUTFIT_COMMON_DATA_EQUIPMENTS_QUERY);
        return createGraphqlRequest(webAccessTokenDo, MyOutfitCommonDataEquipmentsQuery, new HashMap<>());
    }

    @Override
    public JSONObject get_replay_list(Long qid) {
        var webAccessTokenDo = switchWebAccessTokenDao.findByQidAndGameId(qid, SPLATOON3_ID);
//        String queryHash = Misc.graphqlQuery.get(REPLAY_QUERY);
        return createGraphqlRequest(webAccessTokenDo, ReplayQuery, new HashMap<>());
    }

    private JSONObject createGraphqlRequest(WebAccessTokenDo webAccessTokenDo, String queryHash, Map<String, Object> variables) {
        var splatoon3ServiceConfig = getSplatoon3ServiceConfig();
        String url = splatoon3ServiceConfig.getUrl();
        String host = URLUtil.url(url).getHost();

        Map<String, String> headers = new HashMap<>();
        headers.put("Host", host);
        headers.put("Origin", url);
        headers.put("User-Agent", String.format("com.nintendo.znca/%s (Android/7.1.2)", globalConfig.getAppVersion()));
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("X-Web-View-Ver", globalConfig.getWebViewVersion());
        headers.put("Accept-Language", webAccessTokenDo.getLanguage());
        headers.put("Authorization", "Bearer " + webAccessTokenDo.getBulletToken());

        Map<String, Object> graphqlBody = new HashMap<>();
        graphqlBody.put("variables", variables);
        Map<String, Object> extensions = new HashMap<>();
        Map<String, Object> persistedQuery = new HashMap<>();
        persistedQuery.put("version", 1);
        persistedQuery.put("sha256Hash", queryHash);
        extensions.put("persistedQuery", persistedQuery);
        graphqlBody.put("extensions", extensions);

        String graphqlBodyStr = JSONUtil.toJsonStr(graphqlBody);
        String fullUrl = URLUtil.completeUrl(url, "/api/graphql");
        try (HttpResponse httpResponse = HttpRequest.post(fullUrl).headerMap(headers, true).body(graphqlBodyStr).execute()) {
            if (httpResponse.getStatus() >= 200 && httpResponse.getStatus() < 300) {
                String body = httpResponse.body();
                return JSONUtil.parseObj(body, true).getJSONObject("data");
            }
            throw new BusinessException("createGraphqlRequest error: " + httpResponse.getStatus() + " " + httpResponse.body());
        }
    }
}
