/*
 * Copyright: Carlos F. Heuberger. All rights reserved.
 *
 */
package cfh.zipinfo;

import static java.util.Objects.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Carlos F. Heuberger, 2022-10-31
 *
 */
abstract sealed class FieldType<T> {

    private final String name;
    private final Function<T, String> formatter;
    
    protected FieldType(String name, Function<T, String> formatter) {
        this.name = requireNonNull(name);
        this.formatter = requireNonNull(formatter);
    }
    
    final void read(List<Field<?>> fields, RandomAccessInput input) throws IOException {
        var field = readField(fields, input);
        fields.add(field);
    }
    
    String name() {
        return name;
    }
    
    String format(Field<T> field) {
        return this + ": " + formatter.apply(field.value());
    }
    
    protected abstract Field<T> readField(List<Field<?>> fields, RandomAccessInput input) throws IOException;
    
    @Override
    public int hashCode() {
        return 5 * name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        return Objects.equals(((FieldType<?>)obj).name, this.name);
    }
    
    @Override
    public String toString() {
        return "%s(%s)".formatted(name, getClass().getSimpleName());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    static final class Signature extends FieldType<Integer> {
        private final int value;
        Signature(int value) {
            super("Signature", Main::format4);
            this.value = requireNonNull(value);
        }
        @Override
        protected Field<Integer> readField(List<Field<?>> fields, RandomAccessInput input) throws IOException {
            return new Field<>(this, value);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    static final class TwoBytes extends FieldType<Short> {
        TwoBytes(String name, Function<Short, String> formatter) {
            super(name, formatter);
        }
        @Override
        protected Field<Short> readField(List<Field<?>> fields, RandomAccessInput input) throws IOException {
            return new Field<>(this, input.reaShort());
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    static final class Length extends FieldType<Short> {
        Length(String name) {
            super(name, Object::toString);
        }
        @Override
        protected Field<Short> readField(List<Field<?>> fields, RandomAccessInput input) throws IOException {
            return new Field<>(this, input.reaShort());
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    static final class FourBytes extends FieldType<Integer> {
        FourBytes(String name, Function<Integer, String> formatter) {
            super(name, formatter);
        }
        @Override
        protected Field<Integer> readField(List<Field<?>> fields, RandomAccessInput input) throws IOException {
            return new Field<>(this, input.readInt());
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    static final class Text extends FieldType<String> {
        Text(String name) {
            super(name, Format::text);
        }
        @Override
        protected Field<String> readField(List<Field<?>> fields, RandomAccessInput input) throws IOException {
            var length = fields
            .stream()
            .filter(field -> field.type().name().equals(this.name()) && field.type() instanceof Length)
            .findFirst()
            .map(Field::value)
            .map(Short.class::cast)
            .orElseThrow()
            .intValue();
            return new Field<>(this, input.readText(length));
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    static final class Bytes extends FieldType<byte[]> {
        Bytes(String name) {
            super(name, Format::bytes);
        }
        @Override
        protected Field<byte[]> readField(List<Field<?>> fields, RandomAccessInput input) throws IOException {
            var length = fields
            .stream()
            .filter(field -> field.type().name().equals(this.name()) && field.type() instanceof Length)
            .findFirst()
            .map(Field::value)
            .map(Short.class::cast)
            .orElseThrow()
            .intValue();
            return new Field<>(this, input.readBytes(length));
        }
    }
}
