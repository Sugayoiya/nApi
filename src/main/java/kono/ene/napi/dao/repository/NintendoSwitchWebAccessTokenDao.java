package kono.ene.napi.dao.repository;


import kono.ene.napi.dao.base.BaseRepo;
import kono.ene.napi.dao.entity.WebAccessTokenDo;

public interface NintendoSwitchWebAccessTokenDao extends BaseRepo<WebAccessTokenDo, String> {
    WebAccessTokenDo findByQidAndGameId(Integer qid, Long gameId);
}
