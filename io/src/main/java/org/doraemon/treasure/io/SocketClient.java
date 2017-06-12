package org.doraemon.treasure.io;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient {

    public static void main(String[] args) throws IOException {
        SocketClient.send();
    }


    public static void send() throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", 9999));
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("This is L-51".getBytes());
        outputStream.flush();
        outputStream.close();
        socket.close();
    }
}
