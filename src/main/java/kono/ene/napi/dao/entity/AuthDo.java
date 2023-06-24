package kono.ene.napi.dao.entity;

import kono.ene.napi.constant.MongoField;
import kono.ene.napi.dao.base.BaseDo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Document("nintendo_auth")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthDo extends BaseDo {
    @Id
    private String id;
    @Field(MongoField.QID)
    private Long qid;
    // ACCESS_TOKEN
    @Field(MongoField.ACCESS_TOKEN)
    private String accessToken;
    // ID_TOKEN
    @Field(MongoField.ID_TOKEN)
    private String idToken;
    // EXPIRES_IN
    @Field(MongoField.EXPIRES_IN)
    private Integer expiresIn;
    // REFRESH_TIME
    @Field(MongoField.REFRESH_TIME)
    private Date refreshTime;
}


