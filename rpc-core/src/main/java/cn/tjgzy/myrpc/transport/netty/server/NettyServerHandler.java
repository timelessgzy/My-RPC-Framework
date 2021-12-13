package cn.tjgzy.myrpc.transport.netty.server;

import cn.tjgzy.myrpc.constant.ResponseCode;
import cn.tjgzy.myrpc.constant.RpcError;
import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.entity.RpcResponse;
import cn.tjgzy.myrpc.provider.ServiceProviderImpl;
import cn.tjgzy.myrpc.provider.ServiceProvider;
import cn.tjgzy.myrpc.transport.RequestHandler;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/** 入站处理器，收到RpcRequest报文，发送RpcResponse报文
 * @author GongZheyi
 * @create 2021-09-17-11:01
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static ServiceProvider serviceProvider;

    static {
        requestHandler = new RequestHandler();
        serviceProvider = new ServiceProviderImpl();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) {
        System.out.println("NettyServerHandler" + msg.toString());
        if (msg.getHeartBeat()) {
            logger.info("接收到客户端发来的心跳包");
            return;
        }

        String serviceName = msg.getRpcServiceName();
        Object service = serviceProvider.getService(serviceName);
        // 反射调用
        Object result = null;
        RpcResponse<Object> rpcResponse = null;
        try {
            result = requestHandler.handle(msg, service);
            // 构造Response报文
            rpcResponse = RpcResponse.success(result,msg.getRequestId());
        } catch (Exception e) {
            e.printStackTrace();
            // TODO:发送一条错误报文
            rpcResponse = RpcResponse.fail(ResponseCode.FAIL, msg.getRequestId(),
                    "服务调用中有错误发生，请检查服务端端口");
        }
        if (ctx.channel().isActive() && ctx.channel().isWritable()) {
            ctx.writeAndFlush(rpcResponse);
        } else {
            logger.error("通道不可写");
        }
//        ChannelFuture future = ctx.writeAndFlush(rpcResponse);
//        future.addListener(ChannelFutureListener.CLOSE);
//        ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                logger.info("长时间未收到心跳包，断开连接...");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
