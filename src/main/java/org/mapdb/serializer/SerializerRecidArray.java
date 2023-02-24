package org.mapdb.serializer;

import org.mapdb.DataIO;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;

import java.io.IOException;

/**
 * Created by jan on 2/28/16.
 */
public class SerializerRecidArray extends SerializerLongArray{

    @Override
    public void serialize(DataOutput2 out, long[] value) throws IOException {
        out.packInt(value.length);
        for (long recid : value) {
            DataIO.packRecid(out, recid);
        }
    }

    @Override
    public long[] deserialize(DataInput2 in, int available) throws IOException {
        int size = in.unpackInt();
        long[] ret = new long[size];
        for (int i = 0; i < size; i++) {
            ret[i] = DataIO.unpackRecid(in);
        }
        return ret;
    }


}
