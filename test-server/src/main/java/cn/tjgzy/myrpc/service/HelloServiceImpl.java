package cn.tjgzy.myrpc.service;

import cn.tjgzy.myrpc.HelloObject;
import cn.tjgzy.myrpc.HelloService;
import cn.tjgzy.myrpc.annotation.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author GongZheyi
 * @create 2021-09-16-11:51
 */
@RpcService
public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("接收到：{}", object.getMessage());
        return "这是调用的返回值，id=" + object.getId();
    }

}
