package org.doraemon.treasure.io;

import org.apache.commons.io.output.StringBuilderWriter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * use BIO write a simple server
 */
public class BIOServer {

    static final int EOF = -1;

    public static void main(String[] args) throws IOException {
        server(9999);
    }

    public static void server(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port, 1000);
        while (true) {
            Socket socket = serverSocket.accept();
            Runnable outBound = () -> outBoundFunc(socket);
            new Thread(outBound).start();
            Runnable inBound = () -> inBoundFunc(socket);
            new Thread(inBound).start();
        }
    }

    private static void outBoundFunc(Socket socket) {
        try {
            InputStreamReader reader = new InputStreamReader(socket.getInputStream());
            char[] buffer = new char[1024 * 4];
            StringBuilderWriter sw = new StringBuilderWriter();
            long count = 0;
            int n;
            while (EOF != (n = reader.read(buffer))) {
                sw.write(buffer, 0, n);
                count += n;
            }
            System.out.println("Accept from " + socket.getInetAddress().toString() + " with Content \n" +
                               sw.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void inBoundFunc(Socket socket) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write("Server Response \n".getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
