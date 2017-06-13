package org.doraemon.treasure.io.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ReadHandler implements Runnable {
    private final Selector      selector;
    private final SocketChannel channel;
    private final ByteBuffer in = ByteBuffer.allocate(1024);
    private final SelectionKey selectionKey;

    public ReadHandler(Selector selector, SocketChannel channel) throws IOException {
        this.selector = selector;
        this.channel = channel;
        this.channel.configureBlocking(false);
        selectionKey = this.channel.register(selector, SelectionKey.OP_READ);
    }

    public void attach() {
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    @Override
    public void run() {
        read();
        try {
            new SendHandler(selector, channel).attach();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read() {
        try {
            while (channel.read(in) > 0) {
                in.flip();
                while (in.hasRemaining()) {
                    byte b = in.get();
                    byte[] a = {b};
                    System.out.println(new String(a));
                }
                in.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
