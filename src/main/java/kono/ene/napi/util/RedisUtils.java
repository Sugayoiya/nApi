package kono.ene.napi.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RedisUtils {

    /**
     * redis key通常使用分号作为分隔符
     */
    public static final char SEPARATOR = ':';

    /**
     * 生成不附带通用信息的redis key, 等于:
     * <blockquote><pre>
     *     generateKey(false, false, service, feature, variables)
     * </pre></blockquote>
     *
     * @param service   业务参数, must not be {@literal null}
     * @param feature   功能参数, must not be {@literal null}
     * @param variables 额外变量
     * @return redis key name
     * @see RedisUtils#generateKey(boolean, boolean, String, String, String...)
     * @since 1.0.7
     */
    public static String genKeyWithNone(String service, String feature, String... variables) {
        return generateKey(false, false, service, feature, variables);
    }

    /**
     * 生成附带[环境配置]的redis key, 等于:
     * <blockquote><pre>
     *     generateKey(true, false, service, feature, variables)
     * </pre></blockquote>
     *
     * @param service   业务参数, must not be {@literal null}
     * @param feature   功能参数, must not be {@literal null}
     * @param variables 额外变量
     * @return redis key name
     * @see RedisUtils#generateKey(boolean, boolean, String, String, String...)
     * @since 1.0.7
     */
    public static String genKeyWithEnv(String service, String feature, String... variables) {
        return generateKey(true, false, service, feature, variables);
    }

    /**
     * 生成附带[服务名]的redis key, 等于:
     * <blockquote><pre>
     *     generateKey(false, true, service, feature, variables)
     * </pre></blockquote>
     *
     * @param service   业务参数, must not be {@literal null}
     * @param feature   功能参数, must not be {@literal null}
     * @param variables 额外变量
     * @return redis key name
     * @see RedisUtils#generateKey(boolean, boolean, String, String, String...)
     * @since 1.0.7
     */
    public static String genKeyWithApp(String service, String feature, String... variables) {
        return generateKey(false, true, service, feature, variables);
    }

    /**
     * 生成附带[环境配置][服务名]的redis key, 等于:
     * <blockquote><pre>
     *     generateKey(true, true, service, feature, variables)
     * </pre></blockquote>
     *
     * @param service   业务参数, must not be {@literal null}
     * @param feature   功能参数, must not be {@literal null}
     * @param variables 额外变量
     * @return redis key name
     * @see RedisUtils#generateKey(boolean, boolean, String, String, String...)
     * @since 1.0.7
     */
    public static String genKeyWithAll(String service, String feature, String... variables) {
        return generateKey(true, true, service, feature, variables);
    }

    /**
     * 规范定义动态生成Redis KeyName
     * <p>
     * v1.0.0 规范定义如下:
     * </p>
     * <blockquote><pre>
     *     [profile]:[application-name]:[service-name]:[feature-name]:[var1]:[var2]:...
     *     [环境配置]:[服务名称]:[业务名称]:[功能名称]:[变量1]:[变量2]:...
     * </pre></blockquote>
     *
     * @param withEnv   是否在keyname中添加环境参数
     * @param withApp   是否在keyname中添加服务参数
     * @param service   业务参数, must not be {@literal null}
     * @param feature   功能参数, must not be {@literal null}
     * @param variables 额外变量
     * @return redis key name
     * @since 1.0.7
     */
    public static String generateKey(boolean withEnv, boolean withApp, String service, String feature, String... variables) {

        Assert.notNull(service, "service name must not be null!");
        Assert.notNull(feature, "feature name must not be null!");

        String env = withEnv ? SpringContextUtil.getEnv() : null;
        String app = withApp ? SpringContextUtil.getApplicationName() : null;

        List<String> fields = List.of(env, app, service, feature);
        CollectionUtil.addAll(fields, variables);
        return StringUtils.join(fields.stream().filter(Objects::nonNull).collect(Collectors.toList()), SEPARATOR);
    }
}
