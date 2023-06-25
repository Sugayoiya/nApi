package kono.ene.napi.service.nintendo;

import kono.ene.napi.dao.entity.GlobalConfigDo;

public interface Misc {
    void updateNintendoGlobalConfig();

    GlobalConfigDo getGlobalConfig();

}
