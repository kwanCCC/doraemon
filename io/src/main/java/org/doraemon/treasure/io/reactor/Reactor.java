package org.doraemon.treasure.io.reactor;

import sun.nio.ch.SelectionKeyImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;


public class Reactor implements Runnable {

    private final Selector            selector;
    private final ServerSocketChannel serverSocketChannel;


    public Reactor(String host, int port) throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(host, port));
        serverSocketChannel.configureBlocking(false);
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKeyImpl.OP_ACCEPT);
        selectionKey.attach(new Acceptor())
    }

    @Override
    public void run() {

    }
}
