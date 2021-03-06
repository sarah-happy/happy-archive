package org.yi.happy.archive.block;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.yi.happy.archive.ByteString;
import org.yi.happy.archive.Bytes;

/**
 * tests for Block
 */
public class GenericBlockTest {
    /**
     * check that headers are case sensitive
     */
    @Test
    public void testBlockHeader() {
        GenericBlock block = GenericBlock.create(new Bytes(), "aA", "b");

        assertEquals("b", block.getMeta().get("aA"));
        assertEquals(null, block.getMeta().get("Aa"));
    }

    /**
     * check that a block converts to bytes
     */
    @Test
    public void testAsBytes() {
        GenericBlock block = createSampleBlock();

        byte[] have = block.asBytes();

        byte[] want = createSampleBytes();
        assertArrayEquals(want, have);
    }

    /**
     * create the sample block
     * 
     * @return the sample block
     */
    public static GenericBlock createSampleBlock() {
        Map<String, String> meta = new LinkedHashMap<String, String>();
        meta.put("a", "c");
        meta.put("b", "d");
        meta.put("c", "e");

        Bytes body = new Bytes(ByteString.toBytes("body\ndata\n"));

        return new GenericBlock(meta, body);
    }

    /**
     * create the bytes for the sample block
     * 
     * @return the bytes for the sample block
     */
    public static byte[] createSampleBytes() {
        return ByteString.toBytes("a: c\r\nb: d\r\nc: e\r\n\r\nbody\ndata\n");
    }
}
