package cn.rsd.server;


import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

/**
 * @author 焦云亮
 * @date 2018/10/29
 * @modifyUser
 * @modifyDate
 */
public abstract class AbstractNettyServer implements NettyServer
{
    protected static Logger logger = LogManager.getLogger(AbstractNettyServer.class);
    /**用于管理所有的channel
     *
     */
    public static final ChannelGroup ALL_CHANNELS = new DefaultChannelGroup("NADRON-CHANNELS", GlobalEventExecutor.INSTANCE);
    protected final NettyConfig nettyConfig;
    protected ChannelInitializer<? extends Channel> channelInitializer;

    public AbstractNettyServer(NettyConfig nettyConfig,
                               ChannelInitializer<? extends Channel> channelInitializer)
    {
        this.nettyConfig = nettyConfig;
        this.channelInitializer = channelInitializer;
    }

    @Override
    public void startServer(int port) throws Exception
    {
        nettyConfig.setPortNumber(port);
        nettyConfig.setSocketAddress(new InetSocketAddress(port));
        startServer();
    }

    @Override
    public void startServer(InetSocketAddress socketAddress) throws Exception
    {
        nettyConfig.setSocketAddress(socketAddress);
        startServer();
    }

    @Override
    public void stopServer() throws Exception
    {
        logger.debug("In stopServer method of class: {}", this.getClass()
                .getName());
        ChannelGroupFuture future = ALL_CHANNELS.close();
        try
        {
            future.await();
        }
        catch (InterruptedException e)
        {
            logger.error(
                    "Execption occurred while waiting for channels to close: {}",
                    e);
        }
        finally
        {
            if (null != nettyConfig.getBossGroup())
            {
                nettyConfig.getBossGroup().shutdownGracefully();
            }
            if (null != nettyConfig.getWorkerGroup())
            {
                nettyConfig.getWorkerGroup().shutdownGracefully();
            }
        }
    }

    @Override
    public ChannelInitializer<? extends Channel> getChannelInitializer()
    {
        return channelInitializer;
    }

    @Override
    public NettyConfig getNettyConfig() {
        return nettyConfig;
    }

    protected EventLoopGroup getBossGroup(){
        return nettyConfig.getBossGroup();
    }

    protected EventLoopGroup getWorkerGroup(){
        return nettyConfig.getWorkerGroup();
    }

    @Override
    public InetSocketAddress getSocketAddress()
    {
        return nettyConfig.getSocketAddress();
    }

    @Override
    public String toString()
    {
        return "NettyServer [socketAddress=" + nettyConfig.getSocketAddress()
                + ", portNumber=" + nettyConfig.getPortNumber() + "]";
    }

}