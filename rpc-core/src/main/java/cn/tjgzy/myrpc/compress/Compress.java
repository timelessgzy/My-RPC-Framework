package cn.tjgzy.myrpc.compress;

/**
 * @author GongZheyi
 * @create 2021-09-27-21:13
 */
public interface Compress {
    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);
}
