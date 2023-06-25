package kono.ene.napi.service.nintendo;

import kono.ene.napi.dao.entity.GlobalConfigDo;

public interface NConfig {
    void updateGlobalConfig();

    GlobalConfigDo getGlobalConfig();

}
