/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2025 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.gru;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

class DefaultMultipartFile implements MultipartDefinition.MultipartFile {

    private final Client client;
    private final String parameterName;
    private final String filename;
    private final Content content;
    private final String contentType;

    DefaultMultipartFile(Client client, String parameterName, String filename, Content content, String contentType) {
        this.client = client;
        this.parameterName = parameterName;
        this.filename = filename;
        this.content = content;
        this.contentType = contentType;
    }

    public String getParameterName() {
        return parameterName;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getBytes() {
        InputStream stream = content.load(client);
        if (stream == null) {
            return new byte[0];
        }
        try {
            return toByteArray(stream);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read streams for " + content, e);
        }
    }

    public String getContentType() {
        return contentType;
    }


    // from Guava
    private static byte[] toByteArray(InputStream in) throws IOException {
        // Presize the ByteArrayOutputStream since we know how large it will need
        // to be, unless that value is less than the default ByteArrayOutputStream
        // size (32).
        ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(32, in.available()));
        copy(in, out);
        return out.toByteArray();
    }

    private static long copy(InputStream from, OutputStream to) throws IOException {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        byte[] buf = createBuffer();
        long total = 0;
        while (true) {
            int r = from.read(buf);
            if (r == -1) {
                break;
            }
            to.write(buf, 0, r);
            total += r;
        }
        return total;
    }

    private static byte[] createBuffer() {
        return new byte[8192];
    }
}
