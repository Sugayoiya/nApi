package kono.ene.napi.service.nintendo;

import jakarta.annotation.Resource;
import kono.ene.napi.dao.entity.GlobalConfigDo;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static kono.ene.napi.constant.MongoField.*;

@Service
public class MiscImpl implements Misc {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public void updateNintendoGlobalConfig() {
        try (var ex = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<String> nsoAppVersionFuture = CompletableFuture.supplyAsync(kono.ene.napi.util.Misc::getNSOAppVersion, ex);
            CompletableFuture<String> nsoMainJsFuture = CompletableFuture.supplyAsync(kono.ene.napi.util.Misc::getMainJsUrl, ex);
            CompletableFuture<String> nsoWebVersionFuture = nsoMainJsFuture.thenApplyAsync(kono.ene.napi.util.Misc::getWebViewVersion);

            String nsoAppVersion = nsoAppVersionFuture.get();
            String nsoWebViewVersion = nsoWebVersionFuture.get();

            GlobalConfigDo configDo = mongoTemplate.findAndModify(
                    Query.query(Criteria.where(ID).is("nintendo_global_config")),
                    Update.update(APP_VERSION, nsoAppVersion)
                            .set(WEB_VIEW_VERSION, nsoWebViewVersion)
                            .set(UPDATE_TIME, new Date())
                            .setOnInsert(CREATE_TIME, new Date()),
                    FindAndModifyOptions.options().upsert(true).returnNew(true),
                    GlobalConfigDo.class);

            Assert.notNull(configDo, "update nintendo_global_config error");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GlobalConfigDo getGlobalConfig() {
        return mongoTemplate.findById("nintendo_global_config", GlobalConfigDo.class);
    }
}
