package kono.ene.napi.response.ns;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SwitchUserSelfResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("qid")
    private Long qid;
    @JsonProperty("nsaId")
    private String nsaId;
    @JsonProperty("imageUri")
    private String imageUri;
    @JsonProperty("name")
    private String name;
    @JsonProperty("supportId")
    private String supportId;
    @JsonProperty("isChildRestricted")
    private Boolean isChildRestricted;
    @JsonProperty("etag")
    private String etag;
    @JsonProperty("links")
    private LinksDTO links;
    @JsonProperty("permissions")
    private PermissionsDTO permissions;
    @JsonProperty("presence")
    private PresenceDTO presence;

    @NoArgsConstructor
    @Data
    public static class LinksDTO {
        @JsonProperty("nintendoAccount")
        private NintendoAccountDTO nintendoAccount;
        @JsonProperty("friendCode")
        private FriendCodeDTO friendCode;

        @NoArgsConstructor
        @Data
        public static class NintendoAccountDTO {
            @JsonProperty("membership")
            private MembershipDTO membership;

            @NoArgsConstructor
            @Data
            public static class MembershipDTO {
                @JsonProperty("active")
                private ActiveDTO active;

                @NoArgsConstructor
                @Data
                public static class ActiveDTO {
                    @JsonProperty("active")
                    private Boolean active;
                }
            }
        }

        @NoArgsConstructor
        @Data
        public static class FriendCodeDTO {
            @JsonProperty("regenerable")
            private Boolean regenerable;
            @JsonProperty("regenerableAt")
            private Integer regenerableAt;
            @JsonProperty("id")
            private String id;
        }
    }

    @NoArgsConstructor
    @Data
    public static class PermissionsDTO {
        @JsonProperty("presence")
        private String presence;
    }

    @NoArgsConstructor
    @Data
    public static class PresenceDTO {
        @JsonProperty("state")
        private String state;
        @JsonProperty("updatedAt")
        private Integer updatedAt;
        @JsonProperty("logoutAt")
        private Integer logoutAt;
        @JsonProperty("game")
        private GameDTO game;

        @NoArgsConstructor
        @Data
        public static class GameDTO {
            @JsonProperty("name")
            private String name;
            @JsonProperty("imageUri")
            private String imageUri;
            @JsonProperty("shopUri")
            private String shopUri;
            @JsonProperty("totalPlayTime")
            private Integer totalPlayTime;
            @JsonProperty("firstPlayedAt")
            private Integer firstPlayedAt;
            @JsonProperty("sysDescription")
            private String sysDescription;
        }
    }
}
