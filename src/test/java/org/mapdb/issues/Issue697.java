package org.mapdb.issues;

import org.junit.Test;
import org.mapdb.CC;
import org.mapdb.Serializer;
import org.mapdb.SortedTableMap;
import org.mapdb.TT;
import org.mapdb.volume.MappedFileVol;
import org.mapdb.volume.Volume;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by jan on 5/2/16.
 */
public class Issue697 {
    @Test
    public void test(){

        SortedTableMap.Sink<Integer, String> sink = SortedTableMap.create(
                CC.DEFAULT_MEMORY_VOLUME_FACTORY.makeVolume(null, false),
                Serializer.INTEGER,
                Serializer.STRING)
                .createFromSink();

        for (int i = 0; i < 10; i++)
        {
            sink.put(i, "value" + i);
        }

        Map m = sink.create();
        for (int i = 0; i < 10; i++)
        {
            assertEquals("value" + i, m.get(i));
        }


    }
}
