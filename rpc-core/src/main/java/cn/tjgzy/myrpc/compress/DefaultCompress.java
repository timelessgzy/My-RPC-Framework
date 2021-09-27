package cn.tjgzy.myrpc.compress;

/**
 * @author GongZheyi
 * @create 2021-09-27-21:56
 */
public class DefaultCompress implements Compress {

    @Override
    public byte[] compress(byte[] bytes) {
        return bytes;
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        return bytes;
    }
}
