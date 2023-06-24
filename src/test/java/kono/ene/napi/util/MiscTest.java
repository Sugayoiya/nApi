package kono.ene.napi.util;

import jakarta.annotation.Resource;
import kono.ene.napi.dao.entity.UserDo;
import kono.ene.napi.dao.repository.NintendoAuthDao;
import kono.ene.napi.dao.repository.NintendoUserDao;
import kono.ene.napi.request.AccountAccessTokenRequest;
import kono.ene.napi.request.UserInfoRequest;
import kono.ene.napi.response.ResponseDto;
import kono.ene.napi.service.NintendoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
@Slf4j
class MiscTest {
    @Resource
    private NintendoUserDao nintendoUserDao;
    @Resource
    private NintendoAuthDao nintendoAuthDao;

    @Resource
    private NintendoService nintendoService;

    @Test
    public void nintendoAppVersion() {
        String nsoAppVersion = Misc.getNSOAppVersion();
        log.info("nsoAppVersion: {}", nsoAppVersion);
    }

    @Test
    void callFApi() {
        long qid = 21778445;
        String accessToken = nintendoService.refreshAccessToken(new AccountAccessTokenRequest(qid));
        UserDo userDo = nintendoService.userInfo(new UserInfoRequest(qid));
        String uuid = UUID.randomUUID().toString().replace("-", "");

        Misc.FApiResult fApiResult = Misc.callFApi(accessToken, 1, uuid, userDo.getId());
        log.info("fApiResult: {}", fApiResult);
    }

    @Test
    void testResponse() {
        ResponseDto<String> responseDto = ResponseDto.success("test");
        log.info("responseDto: {}", responseDto);
    }
}