package com.st.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.util.Scanner;

/**
 * Created by bobo on 2018/2/28.
 *
 * @email ruantianbo@163.com
 */
public class ClientServer {

    public void run() throws Exception{
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();

        b.group(workerGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ByteBuf delimiter = Unpooled.copiedBuffer("#".getBytes());
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });
        ChannelFuture channelFuture = b.connect("127.0.0.1",8765).sync();
        Scanner scanner = new Scanner(System.in);
        String line = "";
        while(!(line=scanner.nextLine()).equals("exit")){

           channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer((line+"#").getBytes()));
        }
        channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer("exit#".getBytes()));
        channelFuture.channel().closeFuture().sync();//等待

        workerGroup.shutdownGracefully();




    }

    public static void main(String[] args) throws Exception{
        new ClientServer().run();
    }
}
