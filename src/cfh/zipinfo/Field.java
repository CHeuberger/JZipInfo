/*
 * Copyright: Carlos F. Heuberger. All rights reserved.
 *
 */
package cfh.zipinfo;

import java.util.Formatter;

import cfh.zipinfo.FieldType.Length;

/**
 * @author Carlos F. Heuberger, 2022-11-02
 *
 */
public record Field<T> (
    FieldType type,
    T value)
{
    @Override
    public String toString() {
        if (true)
        return type.format(this);
        try ( var formatter = new Formatter() ) {
            formatter.format("%s=", type);
            if (value instanceof byte[] bytes) {
                var first = true;
                formatter.format("[");
                for (var b : bytes) {
                    formatter.format(first?"%02x":" %02x", b & 0xFF);
                    first = false;
                }
                formatter.format("]");
            } else if (type instanceof Length) {
                formatter.format("%d", value);
            } else if (value instanceof Byte b) {
                formatter.format("0x%02x", b);
            } else if (value instanceof Short s) {
                formatter.format("0x%04x", s);
            } else if (value instanceof Integer i) {
                formatter.format("0x%s", Main.format4(i));
            } else if (value instanceof Long l) {
                formatter.format("0x%s", Main.format8(l));
            } else if (value instanceof Number x) {
                formatter.format("0x%x", x);
            } else {
                formatter.format("%s", value);
            }
            return formatter.toString();
        }
    }
}
