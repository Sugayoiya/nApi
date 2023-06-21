package kono.ene.napi.service;

import kono.ene.napi.dao.entity.SwitchUserDo;
import kono.ene.napi.dao.entity.UserDo;
import kono.ene.napi.request.AccountAccessTokenRequest;
import kono.ene.napi.request.SessionRequest;
import kono.ene.napi.request.UserInfoRequest;
import kono.ene.napi.request.WebServiceRequest;
import kono.ene.napi.response.ns.WebServiceAccessTokenResponse;

import java.security.NoSuchAlgorithmException;

public interface NintendoService {

    String loginChallenge(Integer qid) throws NoSuchAlgorithmException;

    String sessionToken(SessionRequest sessionRequest);

    String refreshAccessToken(AccountAccessTokenRequest accountAccessTokenRequest);

    UserDo userInfo(UserInfoRequest userInfo);

    SwitchUserDo nintendo_switch_account(UserInfoRequest userInfo);

    WebServiceAccessTokenResponse web_service_token(WebServiceRequest webServiceRequest);
}
