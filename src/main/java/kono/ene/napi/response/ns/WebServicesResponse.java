package kono.ene.napi.response.ns;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WebServicesResponse {
    @JsonProperty("qid")
    private Integer qid;
    @JsonProperty("web_services")
    private List<WebService> webServices;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WebService {
        @JsonProperty("id")
        private Long id;
        @JsonProperty("uri")
        private String uri;
        @JsonProperty("customAttributes")
        private List<CustomAttributesDTO> customAttributes;
        @JsonProperty("whiteList")
        private List<String> whiteList;
        @JsonProperty("name")
        private String name;
        @JsonProperty("imageUri")
        private String imageUri;

        @NoArgsConstructor
        @Data
        public static class CustomAttributesDTO {
            @JsonProperty("attrKey")
            private String attrKey;
            @JsonProperty("attrValue")
            private String attrValue;
        }
    }

}
