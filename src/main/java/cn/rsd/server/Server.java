package cn.rsd.server;

import java.net.InetSocketAddress;

/**
 * @author 焦云亮
 * @date 2018/10/29
 * @modifyUser
 * @modifyDate
 */
public interface Server {

    public interface TransmissionProtocol{

    }

    /**
     * 服务器使用的协议
     */

    public enum TRANSMISSION_PROTOCOL implements TransmissionProtocol {
        TCP,UDP
    }

    TransmissionProtocol getTransmissionProtocol();

    /**
     * 启动服务器
     */
    void startServer() throws Exception;

    void startServer(int port) throws Exception;;

    void startServer(InetSocketAddress socketAddress) throws Exception;

    void stopServer() throws Exception;

    InetSocketAddress getSocketAddress();

}
