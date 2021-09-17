package cn.tjgzy.myrpc.transport.socket.client;

import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.transport.RpcClient;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author GongZheyi
 * @create 2021-09-16-12:03
 */
public class SocketClient implements RpcClient {

    private static String host;
    private static int port;
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }


    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用时有错误发生：", e);
            return null;
        }
    }
}

