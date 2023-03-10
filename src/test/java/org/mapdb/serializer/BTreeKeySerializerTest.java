package org.mapdb.serializer;

import kotlin.jvm.functions.Function0;
import org.junit.Test;import org.mapdb.*;

import java.io.DataInput;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mapdb.Serializer.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class BTreeKeySerializerTest {

    @Test public void testLong(){
        DB db = DBMaker.memoryDB()
                .make();
        Map m = db.treeMap("test")
                .keySerializer(Serializer.LONG)
                .make();

        for(long i = 0; i<1000;i++){
            m.put(i*i,i*i+1);
        }

        for(long i = 0; i<1000;i++){
            assertEquals(i * i + 1, m.get(i * i));
        }
    }


    void checkKeyClone(GroupSerializer ser, Object[] keys) throws IOException {
        DataOutput2 out = new DataOutput2();
        ser.valueArraySerialize(out,ser.valueArrayFromArray(keys));
        DataInput2.ByteArray in = new DataInput2.ByteArray(out.copyBytes());

        Object[] keys2 = ser.valueArrayToArray(ser.valueArrayDeserialize(in,keys.length));
        assertEquals(in.pos, out.pos);

        assertArrayEquals(keys,keys2);
    }

    @Test public void testLong2() throws IOException {
        Object[][] vals = new Object[][]{
                {Long.MIN_VALUE,Long.MAX_VALUE},
                {Long.MIN_VALUE,1L,Long.MAX_VALUE},
                {-1L,0L,1L},
                {-1L,Long.MAX_VALUE}
        };

        for(Object[] v:vals){
            checkKeyClone(Serializer.LONG, v);
        }
    }

    @Test public void testLong3(){
        final int SIZE = 5;
        long[] testData = new long[SIZE];

        for(int testDataIndex = 0; testDataIndex < SIZE; testDataIndex++){
          testData[testDataIndex] = (long)(testDataIndex + 1);
        }

        for(int testDataIndex = 0; testDataIndex < SIZE; testDataIndex++){
           assertEquals("The returned data for the indexed key for GroupSerializer did not match the data for the key.",
               (long)Serializer.LONG.valueArrayGet(testData, testDataIndex), testData[testDataIndex]);
        }
    }

    @Test public void testInt2() throws IOException {
        Object[][] vals = new Object[][]{
                {Integer.MIN_VALUE,Integer.MAX_VALUE},
                {Integer.MIN_VALUE,1,Integer.MAX_VALUE},
                {-1,0,1},
                {-1,Integer.MAX_VALUE}
        };

        for(Object[] v:vals){
            checkKeyClone(Serializer.INTEGER, v);
        }
    }

    @Test public void testInt3(){
        final int TEST_DATA_SIZE = 5;
        int[] testData = new int[TEST_DATA_SIZE];

        for(int i = 0; i < TEST_DATA_SIZE; i++){
          testData[i] = (int)(i + 1);
        }

        for(int i = 0; i < TEST_DATA_SIZE; i++){
            assertEquals("The returned data for the indexed key for GroupSerializer did not match the data for the key.", 
                (long)Serializer.INTEGER.valueArrayGet(testData, i), testData[i]);
        }
    }

    @Test public void testString(){


        DB db = DBMaker.memoryDB()
                .make();
        Map m =  db.treeMap("test")
                .keySerializer(Serializer.STRING)
                .make();


        List<String> list = new ArrayList <String>();
        for(long i = 0; i<1000;i++){
            String s = ""+ Math.random()+(i*i*i);
            m.put(s,s+"aa");
        }

        for(String s:list){
            assertEquals(s+"aa",m.get(s));
        }
    }


    @Test public void testUUID() throws IOException {
        List<java.util.UUID> ids = new ArrayList<java.util.UUID>();
        for(int i=0;i<100;i++)
            ids.add(java.util.UUID.randomUUID());

        long[] vv = (long[]) Serializer.UUID.valueArrayFromArray(ids.toArray());

        int i=0;
        for(java.util.UUID u:ids){
            assertEquals(u.getMostSignificantBits(),vv[i++]);
            assertEquals(u.getLeastSignificantBits(),vv[i++]);
        }

        //clone
        DataOutput2 out = new DataOutput2();
        Serializer.UUID.valueArraySerialize(out, vv);

        DataInput2 in = new DataInput2.ByteArray(out.copyBytes());
        long[] nn = (long[]) Serializer.UUID.valueArrayDeserialize(in,  ids.size());

        assertArrayEquals(vv, nn);

        //test key addition
        java.util.UUID r = java.util.UUID.randomUUID();
        ids.add(10,r);
        long[] vv2 = (long[]) Serializer.UUID.valueArrayPut(vv,10,r);
        i=0;
        for(java.util.UUID u:ids){
            assertEquals(u.getMostSignificantBits(),vv2[i++]);
            assertEquals(u.getLeastSignificantBits(),vv2[i++]);
        }

        vv2 = (long[]) Serializer.UUID.valueArrayDeleteValue(vv2,10+1);

        assertArrayEquals(vv,vv2);
    }



    @Test public void string_formats_compatible() throws IOException {
        ArrayList keys = new ArrayList();
        for(int i=0;i<1000;i++){
            keys.add("common prefix "+ TT.randomString(10 + new Random().nextInt(100), 0));
        }

        checkStringSerializers(keys);
    }


    @Test public void string_formats_compatible_no_prefix() throws IOException {
        ArrayList keys = new ArrayList();
        for(int i=0;i<1000;i++){
            keys.add(TT.randomString(10 + new Random().nextInt(100),0));
        }

        checkStringSerializers(keys);
    }

    @Test public void string_formats_compatible_equal_size() throws IOException {
        ArrayList keys = new ArrayList();
        for(int i=0;i<1000;i++){
            keys.add("common prefix "+ TT.randomString(10,0));
        }

        checkStringSerializers(keys);
    }



    public void checkStringSerializers(ArrayList keys) throws IOException {
        Collections.sort(keys);
        //first check clone on both
        checkKeyClone(Serializer.STRING,keys.toArray());
        checkKeyClone(Serializer.STRING_DELTA,keys.toArray());
        checkKeyClone(Serializer.STRING_DELTA2,keys.toArray());
//    TODO compatible format between STRING DELTA SER?
//        //now serializer and deserialize with other and compare
//        {
//            DataOutput2 out = new DataOutput2();
//            Serializer.STRING_DELTA.valueArraySerialize(out, Serializer.STRING_DELTA.valueArrayFromArray(keys.toArray()));
//
//            DataInput2.ByteArray in = new DataInput2.ByteArray(out.buf);
//            Object[] keys2 = Serializer.STRING_DELTA2.valueArrayToArray(Serializer.STRING_DELTA2.valueArrayDeserialize(in, keys.size()));
//
//            assertArrayEquals(keys.toArray(), keys2);
//        }
//
//        {
//            DataOutput2 out = new DataOutput2();
//            Serializer.STRING_DELTA2.valueArraySerialize(out, Serializer.STRING_DELTA2.valueArrayFromArray(keys.toArray()));
//
//            DataInput2.ByteArray in = new DataInput2.ByteArray(out.buf);
//            Object[] keys2 = Serializer.STRING_DELTA.valueArrayToArray(Serializer.STRING_DELTA.valueArrayDeserialize(in, keys.size()));
//
//            assertArrayEquals(keys.toArray(), keys2);
//        }

        //convert to byte[] and check with BYTE_ARRAY serializers
        for(int i=0;i<keys.size();i++){
            keys.set(i,((String)keys.get(i)).getBytes());
        }

        //first check clone on both
        checkKeyClone(Serializer.BYTE_ARRAY,keys.toArray());
        checkKeyClone(Serializer.BYTE_ARRAY_DELTA,keys.toArray());
        checkKeyClone(Serializer.BYTE_ARRAY_DELTA2,keys.toArray());
//    TODO compatible format between byte[] DELTA SER?
//        //now serializer and deserialize with other and compare
//        {
//            DataOutput2 out = new DataOutput2();
//            Serializer.BYTE_ARRAY.valueArraySerialize(out,  Serializer.BYTE_ARRAY.valueArrayFromArray(keys.toArray()));
//
//            DataInput2.ByteArray in = new DataInput2.ByteArray(out.buf);
//            Object[] keys2 = Serializer.BYTE_ARRAY_DELTA2.valueArrayToArray(Serializer.BYTE_ARRAY_DELTA2.valueArrayDeserialize(in, keys.size()));
//
//            assertArrayEquals(keys.toArray(), keys2);
//        }
//
//        {
//            DataOutput2 out = new DataOutput2();
//            Serializer.BYTE_ARRAY_DELTA2.valueArraySerialize(out, Serializer.BYTE_ARRAY_DELTA2.valueArrayFromArray(keys.toArray()));
//
//            DataInput2.ByteArray in = new DataInput2.ByteArray(out.buf);
//            Object[] keys2 = Serializer.BYTE_ARRAY.valueArrayToArray(Serializer.BYTE_ARRAY.valueArrayDeserialize(in, keys.size()));
//
//            assertArrayEquals(keys.toArray(), keys2);
//        }

    }

    @Test public void stringPrefixLen(){
        checkPrefixLen(0, "");
        checkPrefixLen(4, "aaaa");
        checkPrefixLen(2, "aa","aaaa");
        checkPrefixLen(2, "aaaa","aa");
        checkPrefixLen(2, "aa","aabb");
        checkPrefixLen(2, "aaBB","aabb");
        checkPrefixLen(2, "aaBB","aabb","aabbaa");
        checkPrefixLen(2, "aabbaa","aaBB","aabb");
    }

    void checkPrefixLen(int expected, Object... keys){
        SerializerStringDelta2.StringArrayKeys keys1 =
                (SerializerStringDelta2.StringArrayKeys) Serializer.STRING_DELTA2.valueArrayFromArray(keys);
        assertEquals(expected, keys1.commonPrefixLen());

        char[][] keys2 = (char[][]) Serializer.STRING_DELTA.valueArrayFromArray(keys);
        assertEquals(expected, SerializerStringDelta.commonPrefixLen(keys2));

    }

    @Test
    public void testContainsUnicode() {

    	String nonUnicodeCharactersSmall[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", 
    			"o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    	String nonUnicodeCharactersBig[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
    			"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    	String unicodeCharacters[] = {"??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??",
    			"??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??",
    			"??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??", "??",
    			"??", "??", "??", "??", "??", "??"};

    	// Test for known issues: https://en.wikipedia.org/wiki/Bush_hid_the_facts
    	assertEquals(false, SerializerStringDelta2.ByteArrayKeys.containsUnicode("Bush hid the facts"));
    	assertEquals(false, SerializerStringDelta2.ByteArrayKeys.containsUnicode("this app can break"));
    	assertEquals(false, SerializerStringDelta2.ByteArrayKeys.containsUnicode("acre vai pra globo"));
    	assertEquals(false, SerializerStringDelta2.ByteArrayKeys.containsUnicode("aaaa aaa aaa aaaaa"));
    	assertEquals(false, SerializerStringDelta2.ByteArrayKeys.containsUnicode("a "));

    	for(String s: nonUnicodeCharactersSmall){
    		assertFalse("containsUnicode() must return false for "+ s, SerializerStringDelta2.ByteArrayKeys.containsUnicode(s));
    	}
    	for(String s: nonUnicodeCharactersBig){
    		assertFalse("containsUnicode() must return false for "+ s, SerializerStringDelta2.ByteArrayKeys.containsUnicode(s));
    	}
    	for (String s: unicodeCharacters) {
    		assertTrue("containsUnicode() must return true for "+ s, SerializerStringDelta2.ByteArrayKeys.containsUnicode(s));
    	}

    }
    
}
