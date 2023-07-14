package kono.ene.napi.constant;

public class RedisKey {
    private static final String BASE_KEY = "nintendo:";


    public static String getKey(String key, Object... objects) {
        return BASE_KEY + String.format(key, objects);
    }

}
