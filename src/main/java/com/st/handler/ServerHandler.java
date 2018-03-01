package com.st.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bobo on 2018/2/28.
 *
 * @email ruantianbo@163.com
 */
public class ServerHandler extends ChannelHandlerAdapter{


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.name()+" has connected");
        ctx.writeAndFlush(Unpooled.copiedBuffer((" Welcome Client #"+ ctx.name()).getBytes()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.name()+" has disconnected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = (ByteBuf)msg;
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        String translation = new String(req,"UTF-8");
        System.out.println("receive: "+msg);
        if(msg .equals("exit")){
            ctx.writeAndFlush(Unpooled.copiedBuffer(("ByeBye#").getBytes())).addListener(ChannelFutureListener.CLOSE);
        }

        ctx.writeAndFlush(Unpooled.copiedBuffer(("I have receive Your ["+msg+"] , at time:"+new SimpleDateFormat("yyyy.MMMMM.dd hh:mm aaa").format(new Date())+"#").getBytes()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
