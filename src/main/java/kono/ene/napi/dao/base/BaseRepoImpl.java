package kono.ene.napi.dao.base;

import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.Assert;

import java.util.Map;


/**
 * How to use: @EnableMongoRepositories(repositoryBaseClass = BaseRepoImpl.class)
 *
 * @param <T>
 * @param <ID>
 */
public class BaseRepoImpl<T, ID> extends SimpleMongoRepository<T, ID> implements BaseRepo<T, ID> {
    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, ID> entityInformation;

    /**
     * Creates a new {@link SimpleMongoRepository} for the given {@link MongoEntityInformation} and {@link MongoTemplate}.
     *
     * @param metadata        must not be {@literal null}.
     * @param mongoOperations must not be {@literal null}.
     */
    public BaseRepoImpl(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
        this.entityInformation = metadata;
    }


    @Override
    public UpdateResult updateFirst(Document queryDoc, Document updateDoc) {
        Query query = new BasicQuery(new Document(queryDoc));
        Update update = new BasicUpdate(new Document(updateDoc));
        return mongoOperations.updateFirst(query, update, this.entityInformation.getJavaType());
    }

    @Override
    public UpdateResult upsert(Map<String, ?> queryMap, Map<String, ?> updateMap, Class<?> entityClass) {
        Query query = new BasicQuery(new Document(queryMap));
        Update update = new BasicUpdate(new Document(updateMap));
        return mongoOperations.upsert(query, update, this.entityInformation.getJavaType());
    }

    @Override
    public T findAndModify(Document queryDoc, Document updateDoc, FindAndModifyOptions findAndModifyOptions) {
        Query query = new BasicQuery(queryDoc);
        Update update = new BasicUpdate(updateDoc);
        return mongoOperations.findAndModify(query, update, findAndModifyOptions, this.entityInformation.getJavaType());
    }

    @Override
    public T findAndModify(Document queryDoc, Document updateDoc) {
        return findAndModify(queryDoc, updateDoc, FindAndModifyOptions.options());
    }
}
