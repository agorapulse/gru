package com.agorapulse.gru.content;

import java.io.*;
import java.util.stream.Collectors;

public class ContentUtils {

    public static String getText(InputStream inputStream) {
        if (inputStream == null) {
            return "";
        }

        return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
    }

    public static byte[] getBytes(InputStream is) {
        if (is == null) {
            return new byte[0];
        }

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[100];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read bytes from " + is, e);
        }
    }
}
