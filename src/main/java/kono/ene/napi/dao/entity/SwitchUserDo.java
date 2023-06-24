package kono.ene.napi.dao.entity;

import kono.ene.napi.dao.base.BaseDo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

import static kono.ene.napi.constant.MongoField.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Document("nintendo_switch_user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SwitchUserDo extends BaseDo {
    @Id
    private Long id;
    @Field("qid")
    private Long qid;
    @Field("nsaId")
    private String nsaId;
    @Field("name")
    private String name;
    @Field("etag")
    private String etag;
    @Field("imageUri")
    private String imageUri;
    @Field("isChildRestricted")
    private Boolean isChildRestricted;
    @Field("supportId")
    private String supportId;

    @Field("firebaseCredential")
    private FirebaseCredentialDTO firebaseCredential;

    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class FirebaseCredentialDTO extends BaseDo {
        @Field("accessToken")
        private String accessToken;
        @Field("expiresIn")
        private Integer expiresIn;
    }

    @Field("links")
    private LinksDTO links;

    @NoArgsConstructor
    @Data
    public static class LinksDTO {
        @Field("nintendoAccount")
        private NintendoAccountDTO nintendoAccount;
        @Field("friendCode")
        private FriendCodeDTO friendCode;

        @NoArgsConstructor
        @Data
        public static class NintendoAccountDTO {
            @Field("membership")
            private MembershipDTO membership;

            @NoArgsConstructor
            @Data
            public static class MembershipDTO {
                @Field("active")
                private Boolean active;
            }
        }

        @NoArgsConstructor
        @Data
        public static class FriendCodeDTO {
            @Field("regenerable")
            private Boolean regenerable;
            @Field("regenerableAt")
            private Integer regenerableAt;
            @Field("id")
            private String id;
        }
    }

    @Field("permissions")
    private PermissionsDTO permissions;

    @NoArgsConstructor
    @Data
    public static class PermissionsDTO {
        @Field("presence")
        private String presence;
    }

    @Field("presence")
    private PresenceDTO presence;

    @NoArgsConstructor
    @Data
    public static class PresenceDTO {
        @Field("state")
        private String state;
        @Field("updatedAt")
        private Integer updatedAt;
        @Field("logoutAt")
        private Integer logoutAt;
        @Field("game")
        private GameDTO game;

        @NoArgsConstructor
        @Data
        public static class GameDTO {
            // TODO not usable yet
            @Field("name")
            private String name;
            @Field("imageUri")
            private String imageUri;
            @Field("shopUri")
            private String shopUri;
            @Field("totalPlayTime")
            private Integer totalPlayTime;
            @Field("firstPlayedAt")
            private Integer firstPlayedAt;
            @Field("sysDescription")
            private String sysDescription;
        }
    }

    @Field("webApiServerCredential")
    private WebApiServerCredentialDTO webApiServerCredential;

    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class WebApiServerCredentialDTO extends BaseDo {
        @Field("accessToken")
        private String accessToken;
        @Field("expiresIn")
        private Integer expiresIn;
    }

    @Field("friends")
    private List<FriendsDTO> friends;

    @NoArgsConstructor
    @Data
    public static class FriendsDTO {
        @Field("id")
        private Long id;
        @Field("nsaId")
        private String nsaId;
        @Field("imageUri")
        private String imageUri;
        @Field("name")
        private String name;
        @Field("isFriend")
        private Boolean isFriend;
        @Field("isFavoriteFriend")
        private Boolean isFavoriteFriend;
        @Field("isServiceUser")
        private Boolean isServiceUser;
        @Field("friendCreatedAt")
        private Integer friendCreatedAt;
        @Field("presence")
        private PresenceDTO presence;

        @NoArgsConstructor
        @Data
        public static class PresenceDTO {
            @Field("state")
            private String state;
            @Field("updatedAt")
            private Integer updatedAt;
            @Field("logoutAt")
            private Integer logoutAt;
            @Field("game")
            private GameDTO game;

            @NoArgsConstructor
            @Data
            public static class GameDTO {
                @Field("name")
                private String name;
                @Field("imageUri")
                private String imageUri;
                @Field("shopUri")
                private String shopUri;
                @Field("totalPlayTime")
                private Integer totalPlayTime;
                @Field("firstPlayedAt")
                private Integer firstPlayedAt;
                @Field("sysDescription")
                private String sysDescription;
            }
        }
    }

    @Field(F_RESULT)
    private FResult fResult;

    @NoArgsConstructor
    @Data
    public static class FResult {
        @Field(F)
        private String f;
        @Field(REQUEST_ID)
        private String requestId;
        @Field(TIMESTAMP)
        private String timestamp;
        @Field(F_STEP)
        private Integer fStep;
    }

    @Field("webServices")
    private List<WebServicesDTO> webServices;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class WebServicesDTO {
        @Field("id")
        private Long id;
        @Field("uri")
        private String uri;
        @Field("name")
        private String name;
        @Field("imageUri")
        private String imageUri;
        @Field(ACCESS_TOKEN)
        private String accessToken;
        @Field(EXPIRES_IN)
        private Integer expiresIn;
        @Field(CREATE_TIME)
        private Date createTime;
        @Field(UPDATE_TIME)
        private Date updateTime;
        @Field(REFRESH_TIME)
        private Date refreshTime;
    }
}


