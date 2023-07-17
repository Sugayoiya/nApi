package kono.ene.napi.service.telegram.handler.processor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import kono.ene.napi.service.telegram.handler.TelegramBaseHandler;
import kono.ene.napi.service.telegram.handler.TelegramContext;
import kono.ene.napi.service.telegram.handler.UpdateEventEnum;
import kono.ene.napi.service.telegram.handler.annotation.TelegramHandlerAnnotation;
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
public class TelegramHandlerProcessor implements ApplicationContextAware, SmartInitializingSingleton {
    private final Multimap<UpdateEventEnum, TelegramBaseHandler> STRATEGY_MULTIMAP = ArrayListMultimap.create();
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> handlerBeans = applicationContext.getBeansWithAnnotation(TelegramHandlerAnnotation.class);
        List<InternalProcessUnit> sortedInternalUnits = handlerBeans.values().stream()
                .filter(bean -> TelegramBaseHandler.class.isAssignableFrom(bean.getClass()))
                .map(bean -> {
                    InternalProcessUnit unit = new InternalProcessUnit();
                    unit.setBean((TelegramBaseHandler) bean);
                    Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
                    TelegramHandlerAnnotation annotation = clazz.getAnnotation(TelegramHandlerAnnotation.class);
                    unit.setEvents(annotation.events());
                    unit.setPriority(annotation.priority());
                    return unit;
                })
                .sorted(Comparator.comparing(InternalProcessUnit::getPriority))
                .toList();
        sortedInternalUnits.forEach(this::registerHandler);

    }

    public void processContext(TelegramContext context) {
        UpdateEventEnum event = context.getUpdateEventEnum();
        Collection<TelegramBaseHandler> handlers = STRATEGY_MULTIMAP.get(event);
        if (handlers.isEmpty()) {
            throw new RuntimeException("unsupported telegram event: " + event.getName());
        } else {
            for (TelegramBaseHandler eventHandler : handlers) {
                eventHandler.handle(context.getUpdate());
            }
        }
    }


    private void registerHandler(InternalProcessUnit unit) {
        UpdateEventEnum[] events = unit.getEvents();
        for (UpdateEventEnum event : events) {
            STRATEGY_MULTIMAP.put(event, unit.getBean());
        }
    }


    @Data
    private static class InternalProcessUnit {

        private TelegramBaseHandler bean;

        private UpdateEventEnum[] events;

        private int priority;
    }

}
