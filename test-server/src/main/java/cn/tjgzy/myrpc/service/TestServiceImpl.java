package cn.tjgzy.myrpc.service;

import cn.tjgzy.myrpc.TestService;

/**
 * @author GongZheyi
 * @create 2021-09-16-16:09
 */
public class TestServiceImpl implements TestService {
    @Override
    public int getNumber(int i) {
        return i * 2;
    }
}
