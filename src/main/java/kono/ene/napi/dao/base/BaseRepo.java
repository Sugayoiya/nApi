package kono.ene.napi.dao.base;

import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Map;

@NoRepositoryBean
public interface BaseRepo<T, ID> extends MongoRepository<T, ID> {
    UpdateResult updateFirst(Document queryDoc, Document updateDoc);

    UpdateResult upsert(Map<String, ?> queryMap, Map<String, ?> updateMap, Class<?> entityClass);

    T findAndModify(Document queryDoc, Document updateDoc);

    T findAndModify(Document queryDoc, Document updateDoc, FindAndModifyOptions findAndModifyOptions);
}
