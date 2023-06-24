package kono.ene.napi.dao.repository;


import kono.ene.napi.dao.base.BaseRepo;
import kono.ene.napi.dao.entity.UserDo;

public interface NintendoUserDao extends BaseRepo<UserDo, String> {
    UserDo findByQid(Long qid);
}
