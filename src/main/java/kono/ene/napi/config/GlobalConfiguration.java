package kono.ene.napi.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import kono.ene.napi.dao.entity.GlobalConfigDo;
import kono.ene.napi.service.nintendo.NConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class GlobalConfiguration {

    @Resource
    private NConfig NConfig;

    @PostConstruct
    public void init() {
        log.info("init GlobalConfig post construct");
        NConfig.updateGlobalConfig();
    }

    @Bean(name = "globalConfigDTO")
    public GlobalConfigDTO getNintendoGlobalConfig() {
        GlobalConfigDo globalConfig = NConfig.getGlobalConfig();
        GlobalConfigDTO globalConfigDTO = new GlobalConfigDTO();
        globalConfigDTO.setAppVersion(globalConfig.getAppVersion());
        globalConfigDTO.setWebViewVersion(globalConfig.getWebViewVersion());
        var webServices = new ArrayList<GlobalConfigDTO.ServiceConfig>();
        for (var webService : globalConfig.getWebServices()) {
            GlobalConfigDTO.ServiceConfig serviceConfig = new GlobalConfigDTO.ServiceConfig();
            serviceConfig.setId(webService.getId());
            serviceConfig.setName(webService.getName());
            serviceConfig.setUrl(webService.getUri());
            serviceConfig.setImageUrl(webService.getImageUri());
            webServices.add(serviceConfig);
        }
        globalConfigDTO.setWebServices(webServices);
        return globalConfigDTO;
    }

    @Data
    public static class GlobalConfigDTO {
        private String appVersion;
        private String webViewVersion;
        private List<ServiceConfig> webServices;

        @Data
        public static class ServiceConfig {
            private Long id;
            private String name;
            private String url;
            private String imageUrl;
        }
    }
}
