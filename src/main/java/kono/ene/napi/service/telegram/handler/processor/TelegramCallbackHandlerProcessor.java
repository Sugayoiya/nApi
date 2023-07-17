package kono.ene.napi.service.telegram.handler.processor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import kono.ene.napi.service.telegram.handler.TelegramContext;
import kono.ene.napi.service.telegram.handler.annotation.TelegramCallbackAnnotation;
import kono.ene.napi.service.telegram.service.impl.CallbackInnerService;
import lombok.Data;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class TelegramCallbackHandlerProcessor implements ApplicationContextAware, SmartInitializingSingleton {
    private final Multimap<String, CallbackInnerService> STRATEGY_MULTIMAP = ArrayListMultimap.create();
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> handlerBeans = applicationContext.getBeansWithAnnotation(TelegramCallbackAnnotation.class);
        List<InternalProcessUnit> sortedInternalUnits = handlerBeans.values().stream()
                .filter(bean -> CallbackInnerService.class.isAssignableFrom(bean.getClass()))
                .map(bean -> {
                    InternalProcessUnit unit = new InternalProcessUnit();
                    unit.setBean((CallbackInnerService) bean);
                    Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
                    TelegramCallbackAnnotation annotation = clazz.getAnnotation(TelegramCallbackAnnotation.class);
                    unit.setCallback(annotation.callback());
                    unit.setPriority(annotation.priority());
                    return unit;
                })
                .sorted(Comparator.comparing(InternalProcessUnit::getPriority))
                .toList();
        sortedInternalUnits.forEach(this::registerHandler);

    }

    public void processContext(TelegramContext context) {
        String callbackQueryId = context.getCallbackQuery().getData();
        Collection<CallbackInnerService> callbackInnerServices = STRATEGY_MULTIMAP.get(callbackQueryId.split(":")[0]);
        if (callbackInnerServices.isEmpty()) {
            throw new RuntimeException("unsupported telegram event: " + callbackQueryId);
        } else {
            for (CallbackInnerService callback : callbackInnerServices) {
                callback.handle(callbackQueryId, context.getUpdate());
            }
        }
    }


    private void registerHandler(InternalProcessUnit unit) {
        String event = unit.getCallback();
        STRATEGY_MULTIMAP.put(event, unit.getBean());
    }


    @Data
    private static class InternalProcessUnit {

        private CallbackInnerService bean;

        private String callback;

        private int priority;
    }

}
