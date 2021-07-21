package com.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

/**
 * @author haoge
 */
@SuppressWarnings("DuplicatedCode")
public class AioServer {
    public static void main(String[] args) throws IOException {
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
        server.bind(new InetSocketAddress("localhost", 52000));
        System.out.println("服务正在52000端口守候");

        server.accept(null, new CompletionHandler<>() {

            @Override
            public void completed(AsynchronousSocketChannel channel, Object attachment) {
                server.accept(null, this);

                ByteBuffer buffer = ByteBuffer.allocate(1024);
                channel.read(buffer, buffer, new CompletionHandler<>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        attachment.flip();
                        CharBuffer charBuffer = CharBuffer.allocate(1024);
                        CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
                        decoder.decode(attachment, charBuffer, false);
                        charBuffer.flip();
                        String s = new String(charBuffer.array(), 0, charBuffer.limit());
                        System.out.println("client said: " + s);
                        channel.write(ByteBuffer.wrap((s + " + 666").getBytes(StandardCharsets.UTF_8)));
                        try {
                            channel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        System.out.println("fail info: " + exc.getMessage());
                    }
                });
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("fail info: " + exc.getMessage());
            }
        });

        //noinspection AlibabaUndefineMagicConstant
        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
