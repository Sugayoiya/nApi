package kono.ene.napi.mongo;

import kono.ene.napi.dao.base.BaseRepoImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        repositoryBaseClass = BaseRepoImpl.class,
        basePackages = "kono.ene.napi.dao.repository",
        mongoTemplateRef = "mongoTemplate")
public class MongoRepoConfig {
}
