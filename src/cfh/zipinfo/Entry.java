/*
 * Copyright: Carlos F. Heuberger. All rights reserved.
 *
 */
package cfh.zipinfo;

import static java.util.Objects.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

/**
 * @author Carlos F. Heuberger, 2022-11-02
 *
 */
public class Entry {

    private final long position;
    private final EntryType type;
    private final List<Field<?>> fields;
    
    private Entry(long position, EntryType type, List<Field<?>> fields) {
        this.position = position;
        this.type = requireNonNull(type);
        this.fields = Collections.unmodifiableList(fields);
    }
    
    @Override
    public String toString() {
        try ( var formatter = new Formatter() ) {
            formatter.format("%s (0x%x)%n", type, position);
            for (var field : fields) {
                formatter.format("    %s%n", field);
            }
            return formatter.toString();
        }
    }

    //==============================================================================================
    
    public static Entry read(RandomAccessInput input) throws IOException {
        var position = input.position();
        var signature = input.readInt();
        var type = EntryType.forSignature(signature);
        if (type == null)
            throw new IOException("Unrecognized type: " + Main.format4(signature));
        var fields = new ArrayList<Field<?>>();
        for (var fieldType : type.fields()) {
            fieldType.read(fields, input);
        }
        return new Entry(position, type, fields);
    }
}
