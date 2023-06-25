package kono.ene.napi.controller;

import jakarta.annotation.Resource;
import kono.ene.napi.dao.entity.SwitchUserDo;
import kono.ene.napi.dao.entity.UserDo;
import kono.ene.napi.request.AccountAccessTokenRequest;
import kono.ene.napi.request.SessionRequest;
import kono.ene.napi.request.UserInfoRequest;
import kono.ene.napi.request.WebServiceRequest;
import kono.ene.napi.response.ResponseDto;
import kono.ene.napi.response.ns.AccountAccessTokenResponse;
import kono.ene.napi.response.ns.LoginChallengeResponse;
import kono.ene.napi.response.ns.SessionTokenResponse;
import kono.ene.napi.response.ns.WebServiceAccessTokenResponse;
import kono.ene.napi.service.nintendo.NintendoService;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/nintendo")
public class NintendoController extends AbstractBaseController {
    @Resource
    private NintendoService nintendoService;

    @SneakyThrows
    @GetMapping("/login")
    public ResponseDto<LoginChallengeResponse> login(@RequestParam Long qid) {
        return ResponseDto.success(LoginChallengeResponse.builder()
                .login_url(nintendoService.loginChallenge(qid))
                .build());
    }

    @PostMapping("/session_token")
    public ResponseDto<SessionTokenResponse> session_token(@RequestBody SessionRequest sr) {
        return ResponseDto.success(SessionTokenResponse.builder()
                .session_token(nintendoService.sessionToken(sr))
                .build());
    }

    @PostMapping("/account_access_token")
    public ResponseDto<AccountAccessTokenResponse> account_access_token(@RequestBody AccountAccessTokenRequest aatr) {
        return ResponseDto.success(AccountAccessTokenResponse.builder()
                .account_access_token(nintendoService.refreshAccessToken(aatr))
                .build());
    }

    @PostMapping("/user_info")
    public ResponseDto<UserDo> user_info(@RequestBody UserInfoRequest userInfo) {
        return ResponseDto.success(nintendoService.userInfo(userInfo));
    }

    @PostMapping("/login_nintendo_switch_account")
    public ResponseDto<SwitchUserDo> nintendo_switch_account(@RequestBody UserInfoRequest userInfo) {
        return ResponseDto.success(nintendoService.nintendo_switch_account(userInfo));
    }

    @PostMapping("/web_service_token")
    public ResponseDto<WebServiceAccessTokenResponse> web_service_token(@RequestBody WebServiceRequest webServiceRequest) {
        return ResponseDto.success(nintendoService.web_service_token(webServiceRequest));
    }
}
