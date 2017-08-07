package org.doraemon.dnsagent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestServer implements Runnable {

    private AtomicBoolean init = new AtomicBoolean(false);
    private final Selector            selector;
    private final ServerSocketChannel socketChannel;
    private final ByteBuffer in = ByteBuffer.allocate(1024);

    public TestServer(String host, int port) throws IOException {
        selector = Selector.open();
        socketChannel = ServerSocketChannel.open();
        socketChannel.bind(new InetSocketAddress(host, port));
        socketChannel.configureBlocking(false);
        SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new ReaderHandler());
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Set<SelectionKey> selected = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selected.iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                ReaderHandler attachment = (ReaderHandler) next.attachment();
                attachment.run();
            }
            selected.clear();
        }
    }

    private class ReaderHandler implements Runnable {
        @Override
        public void run() {
            try {
                SocketChannel channel = socketChannel.accept();
                while (channel.read(in) > 0) {
                    in.flip();
                    while (in.hasRemaining()) {
                        byte b = in.get();
                        byte[] a = {b};
                        System.out.println(new String(a));
                    }
                    in.clear();
                }
                init.compareAndSet(false, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean status() {
        return init.get();
    }
}
