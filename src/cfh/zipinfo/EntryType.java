/*
 * Copyright: Carlos F. Heuberger. All rights reserved.
 *
 */
package cfh.zipinfo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Carlos F. Heuberger, 2022-10-31
 *
 */
enum EntryType {
    
    LocalFileHeader(
        0x04034b50
        , version("Version needed")
        , flags("Flags")
        , method()
        , time("Time")
        , date("Date")
        , four("CRC")
        , size("Compressed")
        , size("Original")
        , length("Name")
        , length("Extra")
        , text("Name")
        , bytes("Extra")
        ),
    ;
    private final int signature;
    private final List<FieldType<?>> fields;
    
    private EntryType(int signature, FieldType<?>... fields) {
        this.signature = signature;
        this.fields = List.of(fields);
    }
    
    public int signature() {
        return signature;
    }
    
    public List<FieldType<?>> fields() {
        return fields;
    }

    //----------------------------------------------------------------------------------------------
    
    private static final Map<Integer, EntryType> types;
    static {
        types =
       Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(EntryType::signature, Function.identity()));
    }
    
    public static EntryType forSignature(int signature) {
        return types.get(signature);
    }
    
    //==============================================================================================
    
    private static FieldType<Short> two(String name) {
        return new FieldType.TwoBytes(name, Format::hex2);
    }
    
    private static FieldType<Integer> four(String name) {
        return new FieldType.FourBytes(name, Format::hex4);
    }
    
    private static FieldType<Short> version(String name) {
        return new FieldType.TwoBytes(name, Format::version);
    }
    
    private static FieldType<Short> flags(String name) {
        return new FieldType.TwoBytes(name, Format::flags);
    }
    
    private static FieldType<Short> method() {
        return two("Method");
    }
    
    private static FieldType<Short> time(String name) {
        return two(name);
    }
    
    private static FieldType<Short> date(String name) {
        return two(name);
    }
    
    private static FieldType<Integer> size(String name) {
        return new FieldType.FourBytes(name, Object::toString);
    }
    
    private static FieldType<Short> length(String name) {
        return new FieldType.Length(name);
    }
    
    private static FieldType<String> text(String name) {
        return new FieldType.Text(name);
    }
    
    private static FieldType<byte[]> bytes(String name) {
        return new FieldType.Bytes(name);
    }
}
