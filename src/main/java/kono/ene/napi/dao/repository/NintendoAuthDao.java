package kono.ene.napi.dao.repository;


import kono.ene.napi.dao.base.BaseRepo;
import kono.ene.napi.dao.entity.AuthDo;

public interface NintendoAuthDao extends BaseRepo<AuthDo, String> {
    AuthDo findByQid(Integer qid);
}
