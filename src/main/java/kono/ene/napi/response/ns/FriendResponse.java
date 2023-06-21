package kono.ene.napi.response.ns;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FriendResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("nsaId")
    private String nsaId;
    @JsonProperty("imageUri")
    private String imageUri;
    @JsonProperty("name")
    private String name;
    @JsonProperty("extras")
    private ExtrasDTO extras;

    @NoArgsConstructor
    @Data
    public static class ExtrasDTO {
        // fake data
        @JsonProperty("friendCode")
        private String friendCode;
    }
}
