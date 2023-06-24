package kono.ene.napi.service;

import cn.hutool.json.JSONObject;
import kono.ene.napi.response.splat3.BattleHistoryResponse;

public interface Splatoon3Service {

    String bulletToken(Long qid);

    JSONObject get_tw_history_list(Long qid);

    BattleHistoryResponse get_battle_history_list(Long qid);

    JSONObject get_battle_history_detail(Long qid, String id);

    JSONObject get_stage_schedule(Long qid);

    JSONObject get_player_stats_simple(Long qid);

    JSONObject get_player_stats_full(Long qid);

    JSONObject get_salmon_run_stats(Long qid);

    JSONObject get_current_splatfest(Long qid);

    JSONObject get_splatfest_list(Long qid);

    JSONObject get_weapon_stats(Long qid);

    JSONObject get_fits(Long qid);

    JSONObject get_maps_stats(Long qid);

    JSONObject do_store_order(Long qid, String id, Boolean confirm);

    JSONObject get_store_items(Long qid);

    JSONObject get_single_player_stats(Long qid);

    JSONObject get_species_cur_weapon(Long qid);

    JSONObject get_sr_history_list(Long qid);

    JSONObject get_sr_history_detail(Long qid, String id);

    JSONObject get_outfits_common_data(Long qid);

    JSONObject get_replay_list(Long qid);
}
