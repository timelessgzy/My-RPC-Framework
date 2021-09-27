package cn.tjgzy.myrpc.compress;

import org.junit.Test;

import java.util.Arrays;

/**
 * @author GongZheyi
 * @create 2021-09-27-21:28
 */
public class GzipCompressTest {
    @Test
    public void gzipCompressTest() {
        Compress gzipCompress = new GzipCompress();
        String s = "哈哈额啊我晚点萨德";
        byte[] bytes = s.getBytes();
        byte[] compress = gzipCompress.compress(bytes);
        System.out.println(Arrays.toString(compress));
        byte[] decompress = gzipCompress.decompress(compress);
        String s1 = new String(decompress);
        System.out.println(s1);
    }
}
