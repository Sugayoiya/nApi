package kono.ene.napi.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebServiceRequest {
    private Integer qid;
    @JsonProperty("game_str")
    private String gameStr;
}
