package com.st.handler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;

/**
 * Created by bobo on 2018/2/26.
 *
 * @email ruantianbo@163.com
 */
public class StartServer {

    private int port;


    public void run() throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024).option(ChannelOption.SO_RCVBUF,32*1024).option(ChannelOption.SO_SNDBUF,32*1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //   #作为其分割符号，
                        ByteBuf delimiter = Unpooled.copiedBuffer("#".getBytes());
                        socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
                        //  将发送过来的ByteBuf转化成String,默认为UTF-8
                        socketChannel.pipeline().addLast(new StringDecoder(Charset.forName("UTF-8")));
                        socketChannel.pipeline().addLast(new ServerHandler());
                    }
                });
        ChannelFuture channelFuture = b.bind(8765).sync(); //异步进行连接
        channelFuture.channel().closeFuture().sync();//等待关闭连接

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
//        EventLoopGroup bossGroup = new NioEventLoopGroup();
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//        try{
//            ServerBootstrap b = new ServerBootstrap();
//            b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
//                    .option(ChannelOption.SO_BACKLOG,1024)      //设置tcp缓冲区大小
//                    .option(ChannelOption.SO_SNDBUF,32*1024)    //设置发送缓冲区大小
//                    .option(ChannelOption.SO_RCVBUF,32*1024)    //设置接受缓冲区大小
//                    .childHandler(new ChannelInitializer<SocketChannel>() {
//                protected void initChannel(SocketChannel socketChannel) throws Exception {
//                    socketChannel.pipeline().addLast(new DiscardServerHandler());
//
//                }
//            }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
//
//            ChannelFuture f = b.bind(port).sync();
//            f.channel().closeFuture().sync();
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        finally {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//        }
    }

    public static void main(String[] args) throws Exception{
        (new StartServer()).run();
    }
}
