package kono.ene.napi.response.ns;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FriendCodeUrlResponse {
    @JsonProperty("qid")
    private Long qid;
    @JsonProperty("url")
    private String url;
    @JsonProperty("friendCode")
    private String friendCode;
}
