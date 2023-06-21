package kono.ene.napi.controller;

import cn.hutool.json.JSONObject;
import jakarta.annotation.Resource;
import kono.ene.napi.response.splat3.*;
import kono.ene.napi.service.Splatoon3Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/splatoon3")
public class NintendoSplatoonController extends AbstractBaseController {
    @Resource
    private Splatoon3Service splatoonService;


    // bulletToken
    @GetMapping("/bullet_token")
    public String bulletToken(@RequestParam Integer qid) {
        return splatoonService.bulletToken(qid);
    }

    // get_tw_history_list
    @GetMapping("/tw_history_list")
    public JSONObject regularBattleHistoriesQuery(@RequestParam Integer qid) {
        return splatoonService.get_tw_history_list(qid);
    }

    // get_battle_history_list
    @GetMapping("/battle_history_list")
    public BattleHistoryResponse latestBattleHistoriesQuery(@RequestParam Integer qid) {
        return splatoonService.get_battle_history_list(qid);
    }

    // get_battle_history_detail
    @PostMapping("/battle_history_detail")
    public JSONObject get_battle_history_detail(@RequestParam Integer qid, @RequestParam String id) {
        return splatoonService.get_battle_history_detail(qid, id);
    }

    // get_stage_schedule
    @GetMapping("/stage_schedule")
    public StageScheduleResponse get_stage_schedule(@RequestParam Integer qid) {
        JSONObject stageSchedule = splatoonService.get_stage_schedule(qid);
        return null;
    }

    // get_player_stats_simple
    @GetMapping("/player_stats_simple")
    public PlayerStatsSimpleResponse get_player_stats_simple(@RequestParam Integer qid) {
        JSONObject playerStatsSimple = splatoonService.get_player_stats_simple(qid);
        return null;
    }

    // get_player_stats_full
    @GetMapping("/player_stats_full")
    public PlayerStatsFullResponse get_player_stats_full(@RequestParam Integer qid) {
        JSONObject playerStatsFull = splatoonService.get_player_stats_full(qid);
        return null;
    }

    // get_salmon_run_stats
    @GetMapping("/salmon_run_stats")
    public SalmonRunStatsResponse get_salmon_run_stats(@RequestParam Integer qid) {
        JSONObject salmonRunStats = splatoonService.get_salmon_run_stats(qid);
        return null;
    }

    // get_current_splatfest
    @GetMapping("/current_splatfest")
    public CurrentSplatfestResponse get_current_splatfest(@RequestParam Integer qid) {
        JSONObject currentSplatfest = splatoonService.get_current_splatfest(qid);
        return null;
    }

    // get_splatfest_list
    @GetMapping("/splatfest_list")
    public SplatFestListResponse get_splatfest_list(@RequestParam Integer qid) {
        JSONObject splatfestList = splatoonService.get_splatfest_list(qid);
        return null;
    }

    // get_weapon_stats
    @GetMapping("/weapon_stats")
    public WeaponStatsResponse get_weapon_stats(@RequestParam Integer qid) {
        JSONObject weaponStats = splatoonService.get_weapon_stats(qid);
        return null;
    }

    // get_fits
    @GetMapping("/fits")
    public FitsResponse get_fits(@RequestParam Integer qid) {
        JSONObject fits = splatoonService.get_fits(qid);
        return null;
    }

    // get_maps_stats
    @GetMapping("/maps_stats")
    public MapsStatsResponse get_maps_stats(@RequestParam Integer qid) {
        JSONObject mapsStats = splatoonService.get_maps_stats(qid);
        return null;
    }

    // get_store_items
    @GetMapping("/store_items")
    public StoreItemsResponse get_store_items(@RequestParam Integer qid) {
        JSONObject storeItems = splatoonService.get_store_items(qid);
        return null;
    }

    // do_store_order
    @PostMapping("/store_order")
    public StoreOrderResponse do_store_order(@RequestParam Integer qid, @RequestParam String id, @RequestParam Boolean confirm) {
        JSONObject storeOrder = splatoonService.do_store_order(qid, id, confirm);
        return null;
    }

    // get_single_player_stats
    @GetMapping("/single_player_stats")
    public SinglePlayerStatsResponse get_single_player_stats(@RequestParam Integer qid) {
        JSONObject singlePlayerStats = splatoonService.get_single_player_stats(qid);
        return null;
    }

    // get_species_cur_weapon
    @GetMapping("/species_cur_weapon")
    public SpeciesCurWeaponResponse get_species_cur_weapon(@RequestParam Integer qid) {
        JSONObject speciesCurWeapon = splatoonService.get_species_cur_weapon(qid);
        return null;
    }

    // get_sr_history_list
    @GetMapping("/sr_history_list")
    public SrHistoryListResponse get_sr_history_list(@RequestParam Integer qid) {
        JSONObject srHistoryList = splatoonService.get_sr_history_list(qid);
        return null;
    }

    // get_sr_history_detail
    @PostMapping("/sr_history_detail")
    public SrHistoryDetailResponse get_sr_history_detail(@RequestParam Integer qid, @RequestParam String id) {
        JSONObject srHistoryDetail = splatoonService.get_sr_history_detail(qid, id);
        return null;
    }

    // get_outfits_common_data
    @GetMapping("/outfits_common_data")
    public OutfitsCommonDataResponse get_outfits_common_data(@RequestParam Integer qid) {
        JSONObject outfitsCommonData = splatoonService.get_outfits_common_data(qid);
        return null;
    }

    // get_replay_list
    @GetMapping("/replay_list")
    public ReplayListResponse get_replay_list(@RequestParam Integer qid) {
        JSONObject replayList = splatoonService.get_replay_list(qid);
        return null;
    }

}
