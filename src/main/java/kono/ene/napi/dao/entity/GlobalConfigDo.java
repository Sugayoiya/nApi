package kono.ene.napi.dao.entity;

import kono.ene.napi.constant.MongoField;
import kono.ene.napi.dao.base.BaseDo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Document("nintendo_global_config")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalConfigDo extends BaseDo {
    @Id
    private String id;

    // client_id
    @Field(MongoField.CLIENT_ID)
    private String clientId;
    // app_version
    @Field(MongoField.APP_VERSION)
    private String appVersion;
    // web_view_version
    @Field(MongoField.WEB_VIEW_VERSION)
    private String webViewVersion;
    // web_services
    @Field(MongoField.WEB_SERVICES)
    private List<WebService> webServices;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WebService {
        @Field("id")
        private Long id;
        @Field("uri")
        private String uri;
        @Field("customAttributes")
        private List<CustomAttributesDTO> customAttributes;
        @Field("whiteList")
        private List<String> whiteList;
        @Field("name")
        private String name;
        @Field("imageUri")
        private String imageUri;

        @NoArgsConstructor
        @AllArgsConstructor
        @Data
        public static class CustomAttributesDTO {
            @Field("attrKey")
            private String attrKey;
            @Field("attrValue")
            private String attrValue;
        }
    }
}


