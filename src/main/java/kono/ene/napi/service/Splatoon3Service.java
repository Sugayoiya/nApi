package kono.ene.napi.service;

import cn.hutool.json.JSONObject;
import kono.ene.napi.response.splat3.BattleHistoryResponse;

public interface Splatoon3Service {

    String bulletToken(Integer qid);

    JSONObject get_tw_history_list(Integer qid);

    BattleHistoryResponse get_battle_history_list(Integer qid);

    JSONObject get_battle_history_detail(Integer qid, String id);

    JSONObject get_stage_schedule(Integer qid);

    JSONObject get_player_stats_simple(Integer qid);

    JSONObject get_player_stats_full(Integer qid);

    JSONObject get_salmon_run_stats(Integer qid);

    JSONObject get_current_splatfest(Integer qid);

    JSONObject get_splatfest_list(Integer qid);

    JSONObject get_weapon_stats(Integer qid);

    JSONObject get_fits(Integer qid);

    JSONObject get_maps_stats(Integer qid);

    JSONObject do_store_order(Integer qid, String id, Boolean confirm);

    JSONObject get_store_items(Integer qid);

    JSONObject get_single_player_stats(Integer qid);

    JSONObject get_species_cur_weapon(Integer qid);

    JSONObject get_sr_history_list(Integer qid);

    JSONObject get_sr_history_detail(Integer qid, String id);

    JSONObject get_outfits_common_data(Integer qid);

    JSONObject get_replay_list(Integer qid);
}
