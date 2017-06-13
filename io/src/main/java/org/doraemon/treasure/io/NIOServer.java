package org.doraemon.treasure.io;

import org.doraemon.treasure.io.reactor.Reactor;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NIOServer {

    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Reactor("localhost", 9999));
    }
}
