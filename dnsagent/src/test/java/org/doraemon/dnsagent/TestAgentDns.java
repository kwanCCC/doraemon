package org.doraemon.dnsagent;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class TestAgentDns {

    ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @Test
    public void when_request_outlookWen_and_then_request_local_actually() {
        try {
            TestServer server = new TestServer("localhost", 9090);
            service.submit(server);

            request();

            await().atMost(20, TimeUnit.SECONDS).until(() -> server.status() == true);

        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }

    public void request() throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("www.baidu.com", 80));
        socket.close();
    }
}
