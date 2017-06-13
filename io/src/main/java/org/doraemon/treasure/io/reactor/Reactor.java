package org.doraemon.treasure.io.reactor;

import sun.nio.ch.SelectionKeyImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;


public class Reactor implements Runnable {

    private final Selector            selector;
    private final ServerSocketChannel serverSocketChannel;

    public Reactor(String host, int port) throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(host, port));
        serverSocketChannel.configureBlocking(false);
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKeyImpl.OP_ACCEPT);
        selectionKey.attach(new Acceptor());
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                selector.select();
                Set<SelectionKey> selected = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selected.iterator();
                while (iterator.hasNext()) {
                    dispatch(iterator.next());
                }
                selected.clear();
            }
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void dispatch(SelectionKey key) throws IllegalAccessException {
        if (key.attachment() instanceof Runnable) {
            Runnable attachment = (Runnable) key.attachment();
            attachment.run();
        } else {
            throw new IllegalAccessException("selector attachment is not Runable");
        }
    }

    private class Acceptor implements Runnable {
        @Override
        public void run() {
            try {
                SocketChannel channel = serverSocketChannel.accept();
                if (Objects.nonNull(channel)) {
                    new ReadHandler(selector, channel).attach();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
