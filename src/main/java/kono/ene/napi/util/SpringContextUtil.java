package kono.ene.napi.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNullApi;


public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static final String DEFAULT_APP_NAME_KEY = "spring.application.name";

    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
            return null;
        }

        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        if (applicationContext == null) {
            return null;
        }

        return applicationContext.getBean(beanName, clazz);
    }

    /**
     * 获取当前环境
     */
    public static String getEnv() {
        return applicationContext.getEnvironment().getActiveProfiles()[0];
    }

    public static Environment getEnvironment() {
        return applicationContext.getEnvironment();
    }

    /**
     * 获取当前部署的应用服务名
     *
     * @return application name
     */
    public static String getApplicationName() {
        return applicationContext.getEnvironment().getProperty(DEFAULT_APP_NAME_KEY);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }
}
