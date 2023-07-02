package kono.ene.napi.util;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import kono.ene.napi.exception.BusinessException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Misc {
    public static String NSO_APP_VERSION = "2.5.2";
    public static String WEB_VIEW_VERSION = "4.0.0-d5178440";
    private static final String SPLATNET3_URL = "https://api.lp1.av5ja.srv.nintendo.net";
    private static final Pattern jsPattern = Pattern.compile("\\b(?<revision>[0-9a-f]{40})\\b[\\S]*?void 0[\\S]*?\"revision_info_not_set\"}`,.*?=`(?<version>\\d+\\.\\d+\\.\\d+)-");


    public static String doGet(String url, Map<String, String> headers) {
        // try-with-resources
        try (HttpResponse httpResponse = HttpRequest.get(url).headerMap(headers, true).execute()) {
            if (httpResponse.isOk())
                return httpResponse.body();
            else
                throw new BusinessException(40001, "doGet error: " + httpResponse.getStatus() + " " + httpResponse.body());
        }
    }

    public static String doPost(String url, Map<String, Object> params, Map<String, String> headers, String body) {
        // try-with-resources
        try (HttpResponse httpResponse = HttpRequest.post(url)
                .headerMap(headers, true)
                .form(params)
                .body(body, ContentType.JSON.toString(Charset.defaultCharset()))
                .execute()) {
            if (httpResponse.isOk())
                return httpResponse.body();
            else
                throw new BusinessException(40001, "doPost error: " + httpResponse.getStatus() + " " + httpResponse.body());
        }
    }

    public static String doPost(String url, Map<String, String> headers, String body) {
        // try-with-resources
        try (HttpResponse httpResponse = HttpRequest.post(url)
                .headerMap(headers, true)
                .body(body, ContentType.JSON.toString(Charset.defaultCharset()))
                .execute()) {
            if (httpResponse.isOk())
                return httpResponse.body();
            else
                throw new BusinessException(40001, "doPost error: " + httpResponse.getStatus() + " " + httpResponse.body());
        }
    }

    public static String doPost(String url, Map<String, Object> params, Map<String, String> headers) {
        try (HttpResponse httpResponse = HttpRequest.post(url).headerMap(headers, true).form(params).execute()) {
            if (httpResponse.isOk())
                return httpResponse.body();
            else
                throw new BusinessException(40001, "doPost error: " + httpResponse.getStatus() + " " + httpResponse.body());
        }
    }

    public static String doPost(String url, Map<String, Object> params, String body, Map<String, String> headers) {
        try (HttpResponse httpResponse = HttpRequest.post(url).headerMap(headers, true).form(params).body(body, ContentType.JSON.toString(Charset.defaultCharset())).execute()) {

            return httpResponse.body();
        }
    }

    public static HttpResponse doPost(String url, String body, Map<String, String> headers) {
        try (HttpResponse httpResponse = HttpRequest.post(url).headerMap(headers, true).body(body, ContentType.JSON.toString(Charset.defaultCharset())).execute()) {
            return httpResponse;
        }
    }

    public static String getNSOAppVersion() {
        try {
            Document doc = Jsoup.connect("https://apps.apple.com/us/app/nintendo-switch-online/id1234806557").get();
            Elements versionElt = doc.select("p.whats-new__latest__version");
            NSO_APP_VERSION = versionElt.text().replace("Version ", "").trim();
        } catch (IOException e) {
            log.warn("getNSOAppVersion error", e);
        }
        return NSO_APP_VERSION;
    }

    public static String getMainJsUrl() {
        Map<String, String> appHead = new HashMap<>();
        appHead.put("Upgrade-Insecure-Requests", "1");
        appHead.put("Accept", "*/*");
        appHead.put("DNT", "1");
        appHead.put("X-AppColorScheme", "DARK");
        appHead.put("X-Requested-With", "com.nintendo.znca");
        appHead.put("Sec-Fetch-Site", "none");
        appHead.put("Sec-Fetch-Mode", "navigate");
        appHead.put("Sec-Fetch-User", "?1");
        appHead.put("Sec-Fetch-Dest", "document");

        Map<String, String> appCookies = new HashMap<>();
        appCookies.put("_dnt", "1");

        try {
            Response home = Jsoup.connect(SPLATNET3_URL).headers(appHead).cookies(appCookies).execute();
            if (home.statusCode() == 200) {
                Document doc = Jsoup.parse(home.body());
                Elements mainJS = doc.select("script[src*=static]");
                if (!CollectionUtils.isEmpty(mainJS)) {
                    return SPLATNET3_URL + mainJS.get(0).attr("src");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static String getWebViewVersion(String jsUrl) {
        Map<String, String> jsHead = new HashMap<>();
        jsHead.put("Accept", "*/*");
        jsHead.put("X-Requested-With", "com.nintendo.znca");
        jsHead.put("Sec-Fetch-Site", "same-origin");
        jsHead.put("Sec-Fetch-Mode", "no-cors");
        jsHead.put("Sec-Fetch-Dest", "script");
        jsHead.put("Referer", SPLATNET3_URL);

        Map<String, String> jsCookies = new HashMap<>();
        jsCookies.put("_dnt", "1");

        try {
            Response mainJsBody = Jsoup.connect(jsUrl).headers(jsHead).cookies(jsCookies).ignoreContentType(true).execute();
            if (mainJsBody.statusCode() == 200) {
                Matcher m = jsPattern.matcher(mainJsBody.body());
                if (m.find()) {
                    String version = m.group("version");
                    String revision = m.group("revision").substring(0, 8);
                    WEB_VIEW_VERSION = version + "-" + revision;
                    return WEB_VIEW_VERSION;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.warn("getWebViewVersion error, return fallback version: {}", WEB_VIEW_VERSION);
        return WEB_VIEW_VERSION;
    }

    @Data
    public static class FApiResult {
        private String f;
        private String request_id;
        private String timestamp;
    }

    public static FApiResult callFApi(String idToken, Integer step, String requestId, String nsaid) {
        String url = "https://api.imink.app/f";

        Map<String, Object> apiBody = new HashMap<>();
        apiBody.put("hash_method", step);
        apiBody.put("request_id", requestId);
        apiBody.put("token", idToken);
        apiBody.put("na_id", nsaid);
        String bodyJsonString = JSONUtil.toJsonStr(apiBody);

        Map<String, String> apiHead = new HashMap<>();
        apiHead.put("User-Agent", "moko/" + getNSOAppVersion());
        apiHead.put("Content-Type", "application/json; charset=utf-8");

        String apiResponse = doPost(url, null, apiHead, bodyJsonString);
        return JSONUtil.toBean(apiResponse, FApiResult.class);
    }


//    public static Map<String, String> graphqlQuery;
//
//    static {
//        Map<String, String> aMap = new HashMap<>();
//        aMap.put("BankaraBattleHistoriesQuery", "0438ea6978ae8bd77c5d1250f4f84803");
//        aMap.put("BankaraBattleHistoriesRefetchQuery", "92b56403c0d9b1e63566ec98fef52eb3");
//        aMap.put("BattleHistoryCurrentPlayerQuery", "49dd00428fb8e9b4dde62f585c8de1e0");
//        aMap.put("CatalogQuery", "ff12098bad4989a813201b00ff22ac4e");
//        aMap.put("CatalogRefetchQuery", "60a6592c6ee8e47245020ae0d314d378");
//        aMap.put("ChallengeQuery", "8a079214500148bf88a8fce1d7209b90");
//        aMap.put("ChallengeRefetchQuery", "34aedc79f96b8613501bba465295f779");
//        aMap.put("CheckinQuery", "5d0d1b45ebf4e324d0dae017d9df06d2");
//        aMap.put("CheckinWithQRCodeMutation", "daffd9621680664dbf19d27e87484ac7");
//        aMap.put("ConfigureAnalyticsQuery", "f8ae00773cc412a50dd41a6d9a159ddd");
//        aMap.put("CoopHistoryDetailQuery", "379f0d9b78b531be53044bcac031b34b");
//        aMap.put("CoopHistoryDetailRefetchQuery", "d3188df2fd4436870936b109675e2849");
//        aMap.put("CoopHistoryQuery", "91b917becd2fa415890f5b47e15ffb15");
//        aMap.put("CoopPagerLatestCoopQuery", "eb947416660e0a7520549f6b9a8ffcd7");
//        aMap.put("CoopRecordBigRunRecordContainerPaginationQuery", "2b83817b6e88b202d25939fe04658d33");
//        aMap.put("CoopRecordQuery", "b2f05c682ed2aeb669a86a3265ceb713");
//        aMap.put("CoopRecordRefetchQuery", "15035e6c4308b32d1a77e87398be5cd4");
//        aMap.put("CreateMyOutfitMutation", "31ff008ea218ffbe11d958a52c6f959f");
//        aMap.put("DetailFestRecordDetailQuery", "96c3a7fd484b8d3be08e0a3c99eb2a3d");
//        aMap.put("DetailFestRefethQuery", "18c7c465b18de5829347b7a7f1e571a1");
//        aMap.put("DetailFestVotingStatusRefethQuery", "92f51ed1ab462bbf1ab64cad49d36f79");
//        aMap.put("DetailRankingQuery", "cc38f388c51f9930bd7cca966893f1b4");
//        aMap.put("DetailTabViewWeaponTopsArRefetchQuery", "a6782a0c692e8076656f9b4ab613fd82");
//        aMap.put("DetailTabViewWeaponTopsClRefetchQuery", "8d3c5bb2e82d6eb32a37eefb0e1f8f69");
//        aMap.put("DetailTabViewWeaponTopsGlRefetchQuery", "b23468857c049c2f0684797e45fabac1");
//        aMap.put("DetailTabViewWeaponTopsLfRefetchQuery", "d46f88c2ea5c4daeb5fe9d5813d07a99");
//        aMap.put("DetailTabViewXRankingArRefetchQuery", "6de3895bd90b5fa5220b5e9355981e16");
//        aMap.put("DetailTabViewXRankingClRefetchQuery", "3ab25d7f475cb3d5daf16f835a23411b");
//        aMap.put("DetailTabViewXRankingGlRefetchQuery", "d62ec65b297968b659103d8dc95d014d");
//        aMap.put("DetailTabViewXRankingLfRefetchQuery", "d96057b8f46e5f7f213a35c8ea2b8fdc");
//        aMap.put("DetailVotingStatusQuery", "53ee6b6e2acc3859bf42454266d671fc");
//        aMap.put("DownloadSearchReplayQuery", "d1841381ec4972f1bfc4742d162de0b3");
//        aMap.put("EventBattleHistoriesQuery", "9744fcf676441873c7c8a51285b6aa4d");
//        aMap.put("EventBattleHistoriesRefetchQuery", "8083b0c7f34a4bd0ef4a06ff86fc3e18");
//        aMap.put("EventMatchRankingPeriodQuery", "cdf4ffe56864817014e59c569ec8630f");
//        aMap.put("EventMatchRankingQuery", "2acc36b477328ebb281fa91a07110e2d");
//        aMap.put("EventMatchRankingRefetchQuery", "3cfc123fe1add3c924518c1550b2936c");
//        aMap.put("EventMatchRankingSeasonRefetchQuery", "e39d7ce9875a9d6940b4b449ed5b358b");
//        aMap.put("FestRecordQuery", "44c76790b68ca0f3da87f2a3452de986");
//        aMap.put("FestRecordRefetchQuery", "73b9837d0e4dd29bfa2f1a7d7ee0814a");
//        aMap.put("FriendListQuery", "f0a8ebc384cf5fbac01e8085fbd7c898");
//        aMap.put("FriendListRefetchQuery", "aa2c979ad21a1100170ddf6afea3e2db");
//        aMap.put("GesotownQuery", "a43dd44899a09013bcfd29b4b13314ff");
//        aMap.put("GesotownRefetchQuery", "951cab295eafdbeccfc2e718d7a98646");
//        aMap.put("HeroHistoryQuery", "fbee1a882371d4e3becec345636d7d1c");
//        aMap.put("HeroHistoryRefetchQuery", "4f9ae2b8f1d209a5f20302111b28f975");
//        aMap.put("HistoryRecordQuery", "d9246baf077b2a29b5f7aac321810a77");
//        aMap.put("HistoryRecordRefetchQuery", "67921048c4af8e2b201a12f13ad0ddae");
//        aMap.put("HomeQuery", "7dcc64ea27a08e70919893a0d3f70871");
//        aMap.put("JourneyChallengeDetailQuery", "5a199948d059985bd758cc0175131f4a");
//        aMap.put("JourneyChallengeDetailRefetchQuery", "e7414c7a64bf80bb50ce21d5ccfde772");
//        aMap.put("JourneyQuery", "bc71fc0264f3f72256724b069f7a4097");
//        aMap.put("JourneyRefetchQuery", "09eee118fa16415d6bc3846bc6e5d8e5");
//        aMap.put("LatestBattleHistoriesQuery", "0d90c7576f1916469b2ae69f64292c02");
//        aMap.put("LatestBattleHistoriesRefetchQuery", "6b74405ca9b43ee77eb8c327c3c1a317");
//        aMap.put("MyOutfitDetailQuery", "d935d9e9ba7a5b6b5d6ece7f253304fc");
//        aMap.put("MyOutfitsQuery", "81d9a6849467d2aa6b1603ebcedbddbe");
//        aMap.put("MyOutfitsRefetchQuery", "10db4e349f3123c56df14e3adec2ee6f");
//        aMap.put("PagerLatestVsDetailQuery", "0329c535a32f914fd44251be1f489e24");
//        aMap.put("PagerUpdateBattleHistoriesByVsModeQuery", "eef75ef7ce1964dfe9006bf5facde61e");
//        aMap.put("PhotoAlbumQuery", "7e950e4f69a5f50013bba8a8fb6a3807");
//        aMap.put("PhotoAlbumRefetchQuery", "53fb0ad32c13dd9a6e617b1158cc2d41");
//        aMap.put("PrivateBattleHistoriesQuery", "8e5ae78b194264a6c230e262d069bd28");
//        aMap.put("PrivateBattleHistoriesRefetchQuery", "89bc61012dcf170d9253f406ebebee67");
//        aMap.put("RankingHoldersFestTeamRankingHoldersPaginationQuery", "f488fccdad37b9e19aed50a8d6e83a24");
//        aMap.put("RegularBattleHistoriesQuery", "3baef04b095ad8975ea679d722bc17de");
//        aMap.put("RegularBattleHistoriesRefetchQuery", "4c95233c8d55e7c8cc23aae06109a2e8");
//        aMap.put("ReplayModalReserveReplayDownloadMutation", "87bff2b854168b496c2da8c0e7f3e5bc");
//        aMap.put("ReplayQuery", "c8d9828642f6eac6894876026d3db450");
//        aMap.put("ReplayUploadedReplayListRefetchQuery", "4e83edd3d0964716c6ab27b9d6acf17f");
//        aMap.put("SaleGearDetailOrderGesotownGearMutation", "b79b7a101a243912754f72437e2ad7e5");
//        aMap.put("SaleGearDetailQuery", "6eb1b255b2cf04c08041567148c883ad");
//        aMap.put("SettingQuery", "73bd677ed986ad2cb7004ceabfff4d38");
//        aMap.put("StageRecordQuery", "f08a932d533845dde86e674e03bbb7d3");
//        aMap.put("StageRecordsRefetchQuery", "2fb1b3fa2d40c9b5953ea1ae263e54c1");
//        aMap.put("StageScheduleQuery", "d1f062c14f74f758658b2703a5799002");
//        aMap.put("SupportButton_SupportChallengeMutation", "991bace9e8c52d63084cd1570a97a5b4");
//        aMap.put("UpdateMyOutfitMutation", "bb809066282e7d659d3b9e9d4e46b43b");
//        aMap.put("VotesUpdateFestVoteMutation", "a2c742c840718f37488e0394cd6e1e08");
//        aMap.put("VsHistoryDetailPagerRefetchQuery", "994cf141e55213e6923426caf37a1934");
//        aMap.put("VsHistoryDetailQuery", "9ee0099fbe3d8db2a838a75cf42856dd");
//        aMap.put("WeaponRecordQuery", "5f279779e7081f2d14ae1ddca0db2b6e");
//        aMap.put("WeaponRecordsRefetchQuery", "6961f618fcef440c81509b205465eeec");
//        aMap.put("XBattleHistoriesQuery", "6796e3cd5dc3ebd51864dc709d899fc5");
//        aMap.put("XBattleHistoriesRefetchQuery", "94711fc9f95dd78fc640909f02d09215");
//        aMap.put("XRankingDetailQuery", "d5e4924c05891208466fcba260d682e7");
//        aMap.put("XRankingDetailRefetchQuery", "fb960404299958248b3c0a2fbb444c35");
//        aMap.put("XRankingQuery", "d771444f2584d938db8d10055599011d");
//        aMap.put("XRankingRefetchQuery", "5149402597bd2531b4eea04692d8bfd5");
//        aMap.put("myOutfitCommonDataEquipmentsQuery", "d29cd0c2b5e6bac90dd5b817914832f8");
//        aMap.put("myOutfitCommonDataFilteringConditionQuery", "d02ab22c9dccc440076055c8baa0fa7a");
//        aMap.put("refetchableCoopHistory_coopResultQuery", "50be9b694c7c6b99b7a383e494ec5258");
//        aMap.put("useCurrentFestQuery", "c0429fd738d829445e994d3370999764");
//        aMap.put("useShareMyOutfitQuery", "3ba5572efce5bebbd859fc2d269d223c");
//
//        graphqlQuery = Collections.unmodifiableMap(aMap);
//    }
}
