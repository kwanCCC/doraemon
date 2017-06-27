package com.oneapm.redismq.client.testkit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Path;

public class ReloadProperties {

    public static void reloadProperties(Path path, String origin, String target) throws IOException {
        StringBuffer sb = new StringBuffer();
        FileInputStream fileInputStream = new FileInputStream(path.toFile());
        LineIterator lineIterator = IOUtils.lineIterator(new InputStreamReader(fileInputStream));
        while (lineIterator.hasNext()) {
            String s = lineIterator.nextLine();
            if (s.contains(origin)) {
                s = s.replace(origin, target);
            }
            sb.append(s);
            sb.append("\n");
        }
        writeFile(path.toString(), sb);
    }

    private static void writeFile(String pathfile, StringBuffer buffer) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(pathfile);
        printWriter.write(buffer.toString().toCharArray());
        printWriter.flush();
        printWriter.close();
    }
}
