package kono.ene.napi.service;

import kono.ene.napi.response.ns.*;

public interface NintendoSwitchService {

    WebServicesResponse listWebServices(Integer qid);

    void announcements(Integer qid);

    FriendsResponse friendsList(Integer qid);

    FriendCodeUrlResponse createFriendCodeUrl(Integer qid);

    SwitchUserSelfResponse userSelf(Integer qid);

    FriendResponse userByFriendCode(Integer qid, String friendCode);

    void sendFriendRequest(Integer qid, String friendCode);

}
