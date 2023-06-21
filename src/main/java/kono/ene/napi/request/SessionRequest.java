package kono.ene.napi.request;

import lombok.Data;

@Data
public class SessionRequest {
    private String redirect_url;
    private Integer qid;
}
