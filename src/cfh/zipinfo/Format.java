/*
 * Copyright: Carlos F. Heuberger. All rights reserved.
 *
 */
package cfh.zipinfo;

import java.util.Formatter;

/**
 * @author Carlos F. Heuberger, 2022-11-02
 *
 */
public abstract class Format {

    private Format() {
        throw new AssertionError("should not be instanciated");
    }
    
    private static final String INDENT = "\n        ";
    
    private static final String[] FILE_ATTRIBUTE = """
        MS-DOS and OS/2
        Amiga
        OpenVMS
        UNIX
        VM/CMS
        Atari ST
        OS/2 H.P.F.S.
        Macintosh
        Z-System
        CP/M
        Windows NTFS
        MVS (OS/390 - Z/OS)
        VSE
        Acorn Risc
        VFAT
        alternate MVS
        BeOS
        Tandem
        OS/400
        OS X (Darwin)
        """.split("\n");
    private static final String[] FLAGS = """
         0 - file is encrypted
         1 - compression mode
         2 - compression mode
         3 - data descriptor present
         4 - reserved
         5 - compressed patched data
         6 - strong encryption
         7 - unused
         8 - unused
         9 - unused
        10 - unused
        11 - language encoding (UTF-8)
        12 - reserved
        13 - encrypting central directory
        14 - reserved
        15 - reserved
        """.split("\n");
    
    public static String hex1(byte value) {
        return "0x%02x".formatted(value);
    }
    
    public static String hex2(short value) {
        return "0x%04x".formatted(value);
    }
    
    public static String hex4(int value) { 
        return "0x%04x_%04x".formatted((value >> 16)&0xFFFF, value&0xFFFF);
    }
    
    public static String hex8(long value) { 
        return "0x%04x_%04x_%04x_%04x".formatted(
            (value >> 48)&0xFFFF, 
            (value >> 32)&0xFFFF, 
            (value >> 16)&0xFFFF, 
            value&0xFFFF);
    }
    
    public static String bytes(byte[] bytes) {
        try ( var formatter = new Formatter() ) {
            var first = true;
            formatter.format("[");
            for (var b : bytes) {
                formatter.format(first?"%02x":" %02x", b & 0xFF);
                first = false;
            }
            formatter.format("]");
            return formatter.toString();
        }
    }

    public static String text(String text) {
        return "\"" + text + "\"";
    }
    
    public static String version(short version) {
        var i = (version >> 8) & 0xFF;
        var file = (i < FILE_ATTRIBUTE.length) ? FILE_ATTRIBUTE[i] : "unknown";
        var spec = version & 0xFF;
        return hex2(version)
            + INDENT + "- file attribute information = " + file
            + INDENT + "- ZIP specification version = " + spec/10 + "." + spec%10;
    }
    
    public static String flags(short flags) {
        var string = hex2(flags);
        int f = flags & 0xFFFF;
        for (var flag : FLAGS) {
            if ((f & 0x01) != 0) {
                string += INDENT + flag;
            }
            f >>>= 1;
        }
        return string;
    }
}
