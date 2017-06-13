package org.doraemon.treasure.io.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class SendHandler implements Runnable {

    private final Selector      selector;
    private final SocketChannel channel;
    private final ByteBuffer out = ByteBuffer.allocate(1024);
    private final SelectionKey selectionKey;

    public SendHandler(Selector selector, SocketChannel channel) throws IOException {
        this.selector = selector;
        this.channel = channel;
        this.channel.configureBlocking(false);
        selectionKey = this.channel.register(selector, SelectionKey.OP_WRITE);
    }

    public void attach() {
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
    }

    @Override
    public void run() {
        out.asCharBuffer().append("This is Commander !!!");
        try {
            channel.write(out);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
