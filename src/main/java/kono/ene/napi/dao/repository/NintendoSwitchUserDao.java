package kono.ene.napi.dao.repository;


import kono.ene.napi.dao.base.BaseRepo;
import kono.ene.napi.dao.entity.SwitchUserDo;

public interface NintendoSwitchUserDao extends BaseRepo<SwitchUserDo, String> {
    SwitchUserDo findByQid(Long qid);
}
