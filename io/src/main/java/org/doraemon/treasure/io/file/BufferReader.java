package org.doraemon.treasure.io.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class BufferReader extends MakeFile {
    public BufferReader() throws IOException {
        super();
    }

    private final char[] buffer = new char[1024];

    public void readBigRock1G() throws IOException {
        create1G();
        try {
            long nanoTime = System.nanoTime();
            FileReader fileReader = new FileReader(BIGROCK_1G.toFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.read(buffer) != -1) {
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
            FileReader fileReader = new FileReader(BIGROCK_1kb.toFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.read(buffer) != -1) {
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
            FileReader fileReader = new FileReader(BIGROCK_1M.toFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.read(buffer) != -1) {
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
            FileReader fileReader = new FileReader(BIGROCK_10M.toFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.read(buffer) != -1) {
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
            FileReader fileReader = new FileReader(BIGROCK_100M.toFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.read(buffer) != -1) {
            }
            System.out.println("Read 100M Waste nanoTime :" + (System.nanoTime() - nanoTime));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        BufferReader reader = new BufferReader();
        reader.readBigRock1M();
        reader.readBigRock1KB();
        reader.readBigRock10M();
        reader.readBigRock100M();
        reader.readBigRock1G();
    }
}
