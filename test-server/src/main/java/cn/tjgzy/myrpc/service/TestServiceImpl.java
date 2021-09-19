package cn.tjgzy.myrpc.service;

import cn.tjgzy.myrpc.TestService;
import cn.tjgzy.myrpc.annotation.RpcService;

/**
 * @author GongZheyi
 * @create 2021-09-16-16:09
 */
@RpcService
public class TestServiceImpl implements TestService {
    @Override
    public int getNumber(int i) {
        return i * 2;
    }
}
