package cn.rsd.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * @author 焦云亮
 * @date 2018/10/29
 * @modifyUser
 * @modifyDate
 */
public interface NettyServer extends Server
{
    /**
     * ServerBootstrap创建成功后会有一个ChannelInitializer(即pipeline factory), 本方法主要用于获取这个
     * ChannelInitializer
     *
     * @return
     */
    public ChannelInitializer<? extends Channel> getChannelInitializer();

    /**
     * 设置自己的ChannelInitializer
     *
     * @param initializer
     *            pipeline的工厂类，主要为每个新的链接创建一个pipeline
     */
    public void setChannelInitializer(ChannelInitializer<? extends Channel> initializer);

    /**
     * 获取netty server的configuration
     *
     * @return .
     */
    public NettyConfig getNettyConfig();

}