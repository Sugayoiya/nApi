package kono.ene.napi.response.ns;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

import static kono.ene.napi.constant.MongoField.ACCESS_TOKEN;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WebServiceAccessTokenResponse {
    @JsonProperty("qid")
    private Integer qid;
    @JsonProperty("game_id")
    private Long gameId;
    @JsonProperty("game_uri")
    private String gameUri;
    @JsonProperty("game_name")
    private String gameName;
    @JsonProperty("image_uri")
    private String imageUri;
    @JsonProperty(ACCESS_TOKEN)
    private String accessToken;
}
