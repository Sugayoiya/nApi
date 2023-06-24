package kono.ene.napi.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebServiceRequest {
    private Long qid;
    @JsonProperty("game_str")
    private String gameStr;
}
