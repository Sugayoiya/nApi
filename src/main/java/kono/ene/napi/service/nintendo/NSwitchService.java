package kono.ene.napi.service.nintendo;

import kono.ene.napi.response.ns.*;

public interface NSwitchService {

    WebServicesResponse listWebServices(Long qid);

    void announcements(Long qid);

    FriendsResponse friendsList(Long qid);

    FriendCodeUrlResponse createFriendCodeUrl(Long qid);

    SwitchUserSelfResponse userSelf(Long qid);

    FriendResponse userByFriendCode(Long qid, String friendCode);

    void sendFriendRequest(Long qid, String friendCode);

}
