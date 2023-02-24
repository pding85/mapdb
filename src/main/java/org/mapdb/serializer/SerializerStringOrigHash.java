package org.mapdb.serializer;

import org.jetbrains.annotations.NotNull;
import org.mapdb.DataIO;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;

import java.io.IOException;

/**
 * Created by jan on 2/28/16.
 */
public class SerializerStringOrigHash extends SerializerString {
    @Override
    public void serialize(DataOutput2 out, String value) throws IOException {
        out.writeUTF(value);
    }

    @Override
    public String deserialize(DataInput2 in, int available) throws IOException {
        return in.readUTF();
    }

    @Override
    public boolean isTrusted() {
        return true;
    }


//        @Override
//        public BTreeKeySerializer getBTreeKeySerializer(Comparator comparator) {
//            if(comparator!=null && comparator!=Fun.COMPARATOR) {
//                return super.getBTreeKeySerializer(comparator);
//            }
//            return BTreeKeySerializer.STRING;
//        }


    @Override
    public int hashCode(@NotNull String s, int seed) {
        return DataIO.intHash(s.hashCode() + seed);
    }
}
