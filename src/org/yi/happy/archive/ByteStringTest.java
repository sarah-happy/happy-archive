package org.yi.happy.archive;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class ByteStringTest {
    @Test
    public void testInvalidUtf8() {
	String s = ByteString.fromUtf8(new byte[] { (byte) 0x80, -1, 0x20 });

	/*
	 * I don't know why it is this, but it is. Also it will not re-encode to
	 * the source data.
	 */
	assertEquals("\ufffd\ufffd ", s);
    }
}
