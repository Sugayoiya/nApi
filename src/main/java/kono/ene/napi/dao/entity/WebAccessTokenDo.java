package kono.ene.napi.dao.entity;

import kono.ene.napi.dao.base.BaseDo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

import static kono.ene.napi.constant.MongoField.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Document("nintendo_switch_web_access_token")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebAccessTokenDo extends BaseDo {
    @Id
    private String id;
    @Field("qid")
    private Long qid;
    @Field("game_id")
    private Long gameId;
    @Field("game_uri")
    private String gameUri;
    @Field("game_name")
    private String gameName;
    @Field("image_uri")
    private String imageUri;

    @Field("country")
    private String country;
    @Field("language")
    private String language;

    @Field(ACCESS_TOKEN)
    private String accessToken;
    @Field(EXPIRES_IN)
    private Integer expiresIn;
    @Field(REFRESH_TIME)
    private Date refreshTime;
    @Field(BULLET_TOKEN)
    private String bulletToken;
}


