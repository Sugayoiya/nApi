package kono.ene.napi.dao.entity;

import kono.ene.napi.constant.MongoField;
import kono.ene.napi.dao.base.BaseDo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@EqualsAndHashCode(callSuper = true)
@Document("nintendo_code_challenge")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeChallengeDo extends BaseDo {
    @Id
    private String id;

    @Field(MongoField.QID)
    private Long qid;

    private String verify;

    private String url;

    @Field(MongoField.SESSION_TOKEN)
    private String sessionToken;
}


