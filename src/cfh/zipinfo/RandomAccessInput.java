/*
 * Copyright: Carlos F. Heuberger. All rights reserved.
 *
 */
package cfh.zipinfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * @author Carlos F. Heuberger, 2022-10-31
 *
 */
public class RandomAccessInput implements AutoCloseable {

    private final RandomAccessFile input;
    
    RandomAccessInput(File file) throws FileNotFoundException {
        input = new RandomAccessFile(file, "r");
    }
    
    public long position() throws IOException {
        return input.getFilePointer();
    }
    
    public int read() throws IOException {
        long position = input.getFilePointer();
        try {
            return input.read();
        } catch (IOException ex) {
            throw (IOException) new IOException("exception at position %d (%1$x)".formatted(position)).initCause(ex);
        }
    }
    
    public byte readByte() throws IOException {
        long position = input.getFilePointer();
        try {
            return input.readByte();
        } catch (IOException ex) {
            throw (IOException) new IOException("at %d (%1$x)".formatted(position)).initCause(ex);
        }
    }
    
    public short reaShort() throws IOException {
        long position = input.getFilePointer();
        try {
            return Short.reverseBytes(input.readShort());
        } catch (IOException ex) {
            throw (IOException) new IOException("at %d (%1$x)".formatted(position)).initCause(ex);
        }
    }
    
    public int readInt() throws IOException {
        long position = input.getFilePointer();
        try {
            return Integer.reverseBytes(input.readInt());
        } catch (IOException ex) {
            throw (IOException) new IOException("at %d (%1$x)".formatted(position)).initCause(ex);
        }
    }
    
    public byte[] readBytes(int length) throws IOException {
        long position = input.getFilePointer();
        try {
            var bytes = new byte[length];
            input.readFully(bytes);
            return bytes;
        } catch (IOException ex) {
            throw (IOException) new IOException("at %d (%1$x)".formatted(position)).initCause(ex);
        }
    }
    
    public String readText(int length) throws IOException {
        var bytes = readBytes(length);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void close() throws IOException {
        long position = input.getFilePointer();
        try {
            input.close();
        } catch (IOException ex) {
            throw (IOException) new IOException("at %d (%1$x)".formatted(position)).initCause(ex);
        }
    }
}
