package kono.ene.napi.response.ns;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FriendsResponse {
    @JsonProperty("qid")
    private Integer qid;
    @JsonProperty("friends")
    private List<FriendsDTO> friends;

    @NoArgsConstructor
    @Data
    public static class FriendsDTO {
        @JsonProperty("id")
        private Long id;
        @JsonProperty("nsaId")
        private String nsaId;
        @JsonProperty("imageUri")
        private String imageUri;
        @JsonProperty("name")
        private String name;
        @JsonProperty("isFriend")
        private Boolean isFriend;
        @JsonProperty("isFavoriteFriend")
        private Boolean isFavoriteFriend;
        @JsonProperty("isServiceUser")
        private Boolean isServiceUser;
        @JsonProperty("friendCreatedAt")
        private Integer friendCreatedAt;
        @JsonProperty("presence")
        private PresenceDTO presence;

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
}
