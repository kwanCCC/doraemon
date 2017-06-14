package org.doraemon.treasure.io.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MakeFile {

    public static final int I_KB = 1024;
    public static final int I_M  = I_KB * 1024;
    public static final int X_M  = I_M * 10;
    public static final int C_M  = X_M * 10;
    public static final int I_GB = 1024 * I_M;

    public static final String BIGFILE_SUFFIX = ".stone";
    public static final String BIGFILE_0      = "bigRock_1kb";
    public final Path BIGROCK_1kb;

    public static final String BIGFILE_1 = "bigRock_1M";
    public final Path BIGROCK_1M;

    public static final String BIGFILE_2 = "bigRock_10M";
    public final Path BIGROCK_10M;

    public static final String BIGFILE_3 = "bigRock_100M";
    public final Path BIGROCK_100M;

    public static final String BIGFILE_4 = "bigRock_1G";
    public final Path BIGROCK_1G;

    public MakeFile() throws IOException {
        BIGROCK_1kb = Files.createTempFile(BIGFILE_0, BIGFILE_SUFFIX);
        BIGROCK_1kb.toFile().deleteOnExit();

        BIGROCK_1M = Files.createTempFile(BIGFILE_1, BIGFILE_SUFFIX);
        BIGROCK_1M.toFile().deleteOnExit();

        BIGROCK_10M = Files.createTempFile(BIGFILE_2, BIGFILE_SUFFIX);
        BIGROCK_10M.toFile().deleteOnExit();

        BIGROCK_100M = Files.createTempFile(BIGFILE_3, BIGFILE_SUFFIX);
        BIGROCK_100M.toFile().deleteOnExit();

        BIGROCK_1G = Files.createTempFile(BIGFILE_4, BIGFILE_SUFFIX);
        BIGROCK_1G.toFile().deleteOnExit();
    }

    public void create1KB() throws IOException {
        writeDumpData(BIGROCK_1kb.toFile(), I_KB);
    }

    public void create1M() throws IOException {
        writeDumpData(BIGROCK_1M.toFile(), I_M);
    }

    public void create10M() throws IOException {
        writeDumpData(BIGROCK_10M.toFile(), X_M);
    }

    public void create100M() throws IOException {
        writeDumpData(BIGROCK_100M.toFile(), C_M);
    }

    public void create1G() throws IOException {
        writeDumpData(BIGROCK_1G.toFile(), I_GB);
    }

    static void writeDumpData(File file, long size) throws IOException {
        System.out.println("File path :" + file.getAbsolutePath());
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        for (int i = 0; i < size; i++) {
            bufferedOutputStream.write((byte) i);
        }
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }
}
