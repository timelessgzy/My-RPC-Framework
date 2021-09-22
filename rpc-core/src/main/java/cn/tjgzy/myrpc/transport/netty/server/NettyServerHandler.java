package cn.tjgzy.myrpc.transport.netty.server;

import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.entity.RpcResponse;
import cn.tjgzy.myrpc.provider.ServiceProviderImpl;
import cn.tjgzy.myrpc.provider.ServiceProvider;
import cn.tjgzy.myrpc.transport.RequestHandler;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        if (msg.getHeartBeat()) {
            logger.info("接收到客户端发来的心跳包");
            return;
        }

        String interfaceName = msg.getInterfaceName();
        Object service = serviceProvider.getService(interfaceName);
        Object result = requestHandler.handle(msg, service);
        // 构造Response报文
        RpcResponse<Object> rpcResponse = RpcResponse.success(result,msg.getRequestId());
        if (ctx.channel().isActive() && ctx.channel().isWritable()) {
            ctx.writeAndFlush(rpcResponse);
        } else {
            logger.error("通道不可写");
        }
//        ChannelFuture future = ctx.writeAndFlush(rpcResponse);
//        future.addListener(ChannelFutureListener.CLOSE);

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
