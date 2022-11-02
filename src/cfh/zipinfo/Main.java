/*
 * Copyright: Carlos F. Heuberger. All rights reserved.
 *
 */
package cfh.zipinfo;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Carlos F. Heuberger, 2022-10-31
 *
 */
public class Main {

    public static void main(String[] args) {
        var list = false;
        
        var i = 0;
        while (i < args.length) {
            var arg = args[i];
            if (arg.equals("--") || !arg.startsWith("-"))
                break;
            switch (arg) {
                case "-h", "-?" -> usage(null);
                case "-l" -> list = true;
                default -> usage("Unrecognized option: " + arg);
            }
            i += 1;
        }
        
        if (i == args.length) {
            usage("Please provide path to file");
        }
        var path = args[i++];
        
        if (i < args.length) {
            usage("Only one path please");
        }
        
        var main = new Main(path);
        try {
            if (list) {
                main.listContent();
            } else {
                main.analyse();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void usage(String message) {
        var out = System.out;
        if (message != null) {
            out = System.err;
            out.println(message);
            out.println();
        }
        out.println("""
            Arguments: -h  |  [-l] <file>
            
                -h    show this help and closes
                -l    list content using Java
                file  the file to analyse
            """);
        System.exit(message == null ? 0 : 1);
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    private final File file;
    
    private Main(String path) {
        file = new File(path);
    }
    
    private void listContent() throws IOException {
        System.out.printf("File: %s%n", file.getAbsolutePath());
        var zip = new ZipFile(file, ZipFile.OPEN_READ);
        try (zip) {
            var entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                System.out.printf("  %s (%s)%n", entry.getName(), entry.getComment());
            }
            if (zip.getComment() != null) {
                System.out.printf("Comment: \"%s\"%n", zip.getComment());
            }
        }
        System.out.println("=".repeat(40));

    }
    
    private void analyse() throws IOException {
        var input = new RandomAccessInput(file);
        try (input) {
            while (true) {
                System.out.println(format8(input.position()));
                var entry = Entry.read(input);
                System.out.println(entry);
                break;  // XXX
            }
        }
    }
    
    //----------------------------------------------------------------------------------------------
    
    public static String format8(long value) {
        return format4((int)(value >> 32))+ "_" + format4((int)value);
    }
    
    public static String format4(int value) {
        return "%04x_%04x".formatted(
            (value>>16)&0xFFFF, 
            value&0xFFFF);
    }
}
