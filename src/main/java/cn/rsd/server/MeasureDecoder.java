package cn.rsd.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author 焦云亮
 * @date 2018/10/30
 * @modifyUser
 * @modifyDate
 */
public class MeasureDecoder extends ByteToMessageDecoder {
    final static int MAX_PACK_NUMBER = 246;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < MAX_PACK_NUMBER) {
            return;
        }
        byte[] b = new byte[MAX_PACK_NUMBER];
        in.readBytes(b);
        out.add(b);
    }
}
