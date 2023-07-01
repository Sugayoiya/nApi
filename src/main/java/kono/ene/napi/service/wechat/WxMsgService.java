package kono.ene.napi.service.wechat;

import kono.ene.napi.service.wechat.adapter.TextBuilder;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WxMsgService {
    /**
     * 用户的openId和前端登录场景code的映射关系
     */
    private static final ConcurrentHashMap<String, Integer> OPENID_EVENT_CODE_MAP = new ConcurrentHashMap<>();
    private static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
    @Value("${wx.mp.callback}")
    private String callback;


    public WxMpXmlOutMessage scan(WxMpService wxMpService, WxMpXmlMessage wxMpXmlMessage) {
        // TODO
        return new TextBuilder().build("请点击链接授权：<a href=\"" + "skipUrl" + "\">登录</a>", wxMpXmlMessage, wxMpService);
    }

    private String getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        // TODO
        // 扫码关注的渠道事件有前缀，需要去除
        return wxMpXmlMessage.getEventKey().replace("qrscene_", "");
    }

    /**
     * 用户授权
     *
     * @param userInfo
     */
    public void authorize(WxOAuth2UserInfo userInfo) {
        // TODO
    }

    private void fillUserInfo(Long uid, WxOAuth2UserInfo userInfo) {
        // TODO
    }

    private void login(Long uid, Integer eventKey) {
        // TODO
    }
}
