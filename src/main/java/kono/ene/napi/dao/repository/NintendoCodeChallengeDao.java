package kono.ene.napi.dao.repository;


import kono.ene.napi.dao.base.BaseRepo;
import kono.ene.napi.dao.entity.CodeChallengeDo;


public interface NintendoCodeChallengeDao extends BaseRepo<CodeChallengeDo, String> {
    CodeChallengeDo findByQid(Long qid);
}
