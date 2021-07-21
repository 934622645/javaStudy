package com.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author haoge
 */
@SuppressWarnings("DuplicatedCode")
public class AioClient {
    public static void main(String[] args) {
        try {
            AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
            channel.connect(new InetSocketAddress("localhost", 52000), null, new CompletionHandler<>() {
                @Override
                public void completed(Void result, Object attachment) {
                    String s = UUID.randomUUID().toString();
                    channel.write(ByteBuffer.wrap(s.getBytes(StandardCharsets.UTF_8)), null, new CompletionHandler<>() {
                        @Override
                        public void completed(Integer result, Object attachment) {
                            System.out.println("Client write " + s + " wait respond");
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            channel.read(buffer, buffer, new CompletionHandler<>() {
                                @Override
                                public void completed(Integer result, ByteBuffer attachment) {
                                    buffer.flip();
                                    CharBuffer charBuffer = CharBuffer.allocate(1024);
                                    CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
                                    decoder.decode(buffer, charBuffer, false);
                                    charBuffer.flip();
                                    String data = new String(charBuffer.array(), 0, charBuffer.limit());
                                    System.out.println("server say : " + data);
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
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println("fail info: " + exc.getMessage());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
