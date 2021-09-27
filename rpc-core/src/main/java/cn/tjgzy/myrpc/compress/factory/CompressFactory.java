package cn.tjgzy.myrpc.compress.factory;

import cn.tjgzy.myrpc.compress.Compress;
import cn.tjgzy.myrpc.compress.DefaultCompress;
import cn.tjgzy.myrpc.compress.GzipCompress;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author GongZheyi
 * @create 2021-09-27-22:01
 */
public class CompressFactory {

    private static final Map<Integer,Compress> MAP = new HashMap<>();

    static {
        MAP.put(0,new DefaultCompress());
        MAP.put(1,new GzipCompress());
    }

    public static Compress getCompressInstance(int type) {
        if (type == 1) {
            return MAP.get(1);
        }
        return MAP.get(0);
    }
}
