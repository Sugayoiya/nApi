package kono.ene.napi.dao.base;

import kono.ene.napi.constant.MongoField;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
public class BaseDo {
    @Field(MongoField.CREATE_TIME)
    private Date createTime;
    @Field(MongoField.UPDATE_TIME)
    private Date updateTime;
}
