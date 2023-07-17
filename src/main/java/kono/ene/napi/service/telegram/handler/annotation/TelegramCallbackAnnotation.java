package kono.ene.napi.service.telegram.handler.annotation;


import java.lang.annotation.*;

/**
 * 策略处理器标志属性
 *
 * @author chen
 */
@Documented
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TelegramCallbackAnnotation {

    /**
     * 策略处理器标志属性
     *
     * @return event string
     */
    String callback() default "";

    /**
     * 策略处理器优先级, 值越小优先级越高, 越前置处理, 默认值为 {@code 0}
     *
     * @return priority value
     */
    int priority() default 0;

}
