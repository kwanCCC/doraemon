package org.doraemon.treasure.io.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class ScannerInput extends MakeFile {
    public ScannerInput() throws IOException {
        super();
    }

    private final char[] buffer = new char[1024];

    public void readBigRock1G() throws IOException {
        create1G();
        try {
            long nanoTime = System.nanoTime();
            Scanner fileReader = new Scanner(BIGROCK_1G.toFile());
            while (fileReader.hasNextByte()) {
                fileReader.nextByte();
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
            Scanner fileReader = new Scanner(BIGROCK_1kb.toFile());
            while (fileReader.hasNextByte()) {
                fileReader.nextByte();
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
            Scanner fileReader = new Scanner(BIGROCK_1M.toFile());
            while (fileReader.hasNextByte()) {
                fileReader.nextByte();
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
            Scanner fileReader = new Scanner(BIGROCK_10M.toFile());
            while (fileReader.hasNextByte()) {
                fileReader.nextByte();
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
            Scanner fileReader = new Scanner(BIGROCK_100M.toFile());
            while (fileReader.hasNextByte()) {
                fileReader.nextByte();
            }
            System.out.println("Read 100M Waste nanoTime :" + (System.nanoTime() - nanoTime));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ScannerInput scannerInput = new ScannerInput();
        scannerInput.readBigRock1M();
        scannerInput.readBigRock1KB();
        scannerInput.readBigRock10M();
        scannerInput.readBigRock100M();
        scannerInput.readBigRock1G();
    }
}
