package kono.ene.napi.service.nintendo;

import kono.ene.napi.dao.entity.SwitchUserDo;
import kono.ene.napi.dao.entity.UserDo;
import kono.ene.napi.request.AccountAccessTokenRequest;
import kono.ene.napi.request.SessionRequest;
import kono.ene.napi.request.UserInfoRequest;
import kono.ene.napi.request.WebServiceRequest;
import kono.ene.napi.response.ns.WebServiceAccessTokenResponse;

import java.security.NoSuchAlgorithmException;

public interface NintendoService {

    String loginChallenge(Long qid) throws NoSuchAlgorithmException;

    void bind(Long qid, String redirectUrl);

    String sessionToken(Long qid, String redirectUrl);

    String sessionToken(SessionRequest sessionRequest);

    String refreshAccessToken(Long qid);

    String refreshAccessToken(AccountAccessTokenRequest accountAccessTokenRequest);

    void userInfo(Long qid);

    UserDo userInfo(UserInfoRequest userInfo);

    void nintendo_switch_account(Long qid);

    SwitchUserDo nintendo_switch_account(UserInfoRequest userInfo);

    WebServiceAccessTokenResponse web_service_token(Long qid, String gameStr);

    WebServiceAccessTokenResponse web_service_token(WebServiceRequest webServiceRequest);
}
