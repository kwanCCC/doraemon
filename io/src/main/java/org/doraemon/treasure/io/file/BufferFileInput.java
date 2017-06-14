package org.doraemon.treasure.io.file;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BufferFileInput extends MakeFile {
    public BufferFileInput() throws IOException {
        super();
    }

    private final byte[] buffer = new byte[1024];

    public void readBigRock1G() throws IOException {
        create1G();
        try {
            long nanoTime = System.nanoTime();
            FileInputStream fileInputStream = new FileInputStream(BIGROCK_1G.toFile());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            while (bufferedInputStream.read(buffer) != -1) {
            }
            System.out.println("Read 1GB File Waste nanoTime :" + (System.nanoTime() - nanoTime));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void readBigRock1KB() throws IOException {
        create1KB();
        try {
            long nanoTime = System.nanoTime();
            FileInputStream fileInputStream = new FileInputStream(BIGROCK_1kb.toFile());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            while (bufferedInputStream.read(buffer) != -1) {
            }
            System.out.println("Read 1KB File Waste nanoTime :" + (System.nanoTime() - nanoTime));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void readBigRock1M() throws IOException {
        create1M();
        try {
            long nanoTime = System.nanoTime();
            FileInputStream fileInputStream = new FileInputStream(BIGROCK_1M.toFile());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            while (bufferedInputStream.read(buffer) != -1) {
            }
            System.out.println("Read 1M Waste nanoTime :" + (System.nanoTime() - nanoTime));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void readBigRock10M() throws IOException {
        create10M();
        try {
            long nanoTime = System.nanoTime();
            FileInputStream fileInputStream = new FileInputStream(BIGROCK_10M.toFile());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            while (bufferedInputStream.read(buffer) != -1) {
            }
            System.out.println("Read 10M Waste nanoTime :" + (System.nanoTime() - nanoTime));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void readBigRock100M() throws IOException {
        create100M();
        try {
            long nanoTime = System.nanoTime();
            FileInputStream fileInputStream = new FileInputStream(BIGROCK_100M.toFile());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            while (bufferedInputStream.read(buffer) != -1) {
            }
            System.out.println("Read 100M Waste nanoTime :" + (System.nanoTime() - nanoTime));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        BufferFileInput reader = new BufferFileInput();
        reader.readBigRock1M();
        reader.readBigRock1KB();
        reader.readBigRock10M();
        reader.readBigRock100M();
        reader.readBigRock1G();
    }
}
