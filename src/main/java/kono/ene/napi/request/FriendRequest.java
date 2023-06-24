package kono.ene.napi.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequest {
    @JsonProperty("qid")
    private Long qid;
    @JsonProperty("friendCode")
    private String friendCode;
}
