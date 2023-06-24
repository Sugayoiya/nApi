package kono.ene.napi.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SessionRequest {
    private Long qid;
    private String redirect_url;
}
