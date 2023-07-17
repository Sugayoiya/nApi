package kono.ene.napi.controller.wechat;

import kono.ene.napi.service.wechat.WxMsgService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/wechat/msg")
public class WxMessageController {
    private final WxMpService wxService;
    private final WxMsgService wxMsgService;

    @PostMapping()
    public String authGet() {

        WxMpKefuMessage wxMpKefuMessage = new WxMpKefuMessage();
        wxMpKefuMessage.setMsgType("text");
        wxMpKefuMessage.setToUser("oCTt96I6dBSBCT63AHGka_XPQ5ZY");
        wxMpKefuMessage.setContent("测试");
        try {
            wxService.getKefuService().sendKefuMessage(wxMpKefuMessage);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }

        return "success";
    }
}
