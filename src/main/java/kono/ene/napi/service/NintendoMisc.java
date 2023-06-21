package kono.ene.napi.service;

import kono.ene.napi.dao.entity.GlobalConfigDo;

public interface NintendoMisc {
    void updateNintendoGlobalConfig();

    GlobalConfigDo getGlobalConfig();

}
