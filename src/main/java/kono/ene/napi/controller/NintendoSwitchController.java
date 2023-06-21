package kono.ene.napi.controller;

import jakarta.annotation.Resource;
import kono.ene.napi.request.FriendRequest;
import kono.ene.napi.response.ns.*;
import kono.ene.napi.service.NintendoSwitchService;
import org.springframework.web.bind.annotation.*;

@RestController
public class NintendoSwitchController extends AbstractBaseController {
    @Resource
    private NintendoSwitchService switchService;

    @GetMapping("/nintendo_switch/list_web_services")
    public WebServicesResponse listWebServices(@RequestParam Integer qid) {
        return switchService.listWebServices(qid);
    }

    @GetMapping("/nintendo_switch/announcements")
    public void announcements(@RequestParam Integer qid) {
        switchService.announcements(qid);
    }

    @GetMapping("/nintendo_switch/friends_list")
    public FriendsResponse friendsList(@RequestParam Integer qid) {
        return switchService.friendsList(qid);
    }

    @GetMapping("/nintendo_switch/create_friend_code")
    public FriendCodeUrlResponse createFriendCodeUrl(@RequestParam Integer qid) {
        return switchService.createFriendCodeUrl(qid);
    }

    // userSelf
    @GetMapping("/nintendo_switch/user_self")
    public SwitchUserSelfResponse userSelf(@RequestParam Integer qid) {
        return switchService.userSelf(qid);
    }

    // userByFriendCode
    @GetMapping("/nintendo_switch/user_by_friend_code")
    public FriendResponse userByFriendCode(@RequestParam Integer qid, @RequestParam String friendCode) {
        return switchService.userByFriendCode(qid, friendCode);
    }

    // sendFriendRequest
    @PostMapping("/nintendo_switch/send_friend_request")
    public void sendFriendRequest(@RequestBody FriendRequest friendRequest) {
        switchService.sendFriendRequest(friendRequest.getQid(), friendRequest.getFriendCode());
    }

}
