/*
 * Copyright: Carlos F. Heuberger. All rights reserved.
 *
 */
package cfh.zipinfo;

import static java.util.Objects.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author Carlos F. Heuberger, 2022-10-31
 *
 */
abstract sealed class FieldType {

    final String name;
    
    protected FieldType(String name) {
        this.name = requireNonNull(name);
    }
    
    final void read(List<Field<?>> fields, RandomAccessInput input) throws IOException {
        var field = readField(fields, input);
        fields.add(field);
    }
    
    protected abstract Field<?> readField(List<Field<?>> fields, RandomAccessInput input) throws IOException;
    
    @Override
    public int hashCode() {
        return 5 * name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        return Objects.equals(((FieldType)obj).name, this.name);
    }
    
    @Override
    public String toString() {
        return "%s(%s)".formatted(name, getClass().getSimpleName());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    static final class Signature extends FieldType {
        private final int value;
        Signature(int value) {
            super("Signature");
            this.value = requireNonNull(value);
        }
        @Override
        protected Field<Integer> readField(List<Field<?>> fields, RandomAccessInput input) throws IOException {
            return new Field<>(this, value);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    static final class TwoBytes extends FieldType {
        TwoBytes(String name) {
            super(name);
        }
        @Override
        protected Field<Short> readField(List<Field<?>> fields, RandomAccessInput input) throws IOException {
            return new Field<>(this, input.reaShort());
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    static final class Length extends FieldType {
        Length(String name) {
            super(name);
        }
        @Override
        protected Field<Short> readField(List<Field<?>> fields, RandomAccessInput input) throws IOException {
            return new Field<>(this, input.reaShort());
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    static final class FourBytes extends FieldType {
        FourBytes(String name) {
            super(name);
        }
        @Override
        protected Field<Integer> readField(List<Field<?>> fields, RandomAccessInput input) throws IOException {
            return new Field<>(this, input.readInt());
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    static final class Size extends FieldType {
        Size(String name) {
            super(name);
        }
        @Override
        protected Field<Integer> readField(List<Field<?>> fields, RandomAccessInput input) throws IOException {
            return new Field<>(this, input.readInt());
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    static final class Text extends FieldType {
        Text(String name) {
            super(name);
        }
        @Override
        protected Field<String> readField(List<Field<?>> fields, RandomAccessInput input) throws IOException {
            var length = fields
            .stream()
            .filter(field -> field.type().name.equals(this.name) && field.type() instanceof Length)
            .findFirst()
            .map(Field::value)
            .map(Short.class::cast)
            .orElseThrow()
            .intValue();
            return new Field<>(this, input.readText(length));
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    static final class Bytes extends FieldType {
        Bytes(String name) {
            super(name);
        }
        @Override
        protected Field<byte[]> readField(List<Field<?>> fields, RandomAccessInput input) throws IOException {
            var length = fields
            .stream()
            .filter(field -> field.type().name.equals(this.name) && field.type() instanceof Length)
            .findFirst()
            .map(Field::value)
            .map(Short.class::cast)
            .orElseThrow()
            .intValue();
            return new Field<>(this, input.readBytes(length));
        }
    }
}
