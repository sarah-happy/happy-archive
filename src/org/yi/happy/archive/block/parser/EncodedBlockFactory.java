package org.yi.happy.archive.block.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.yi.happy.annotate.SmellsMessy;
import org.yi.happy.archive.Bytes;
import org.yi.happy.archive.block.BlobEncodedBlock;
import org.yi.happy.archive.block.ContentEncodedBlock;
import org.yi.happy.archive.block.NameEncodedBlock;
import org.yi.happy.archive.crypto.Cipher;
import org.yi.happy.archive.crypto.CipherProvider;
import org.yi.happy.archive.crypto.DigestProvider;
import org.yi.happy.archive.key.NameLocatorKey;

@SmellsMessy
public class EncodedBlockFactory {
    public static BlobEncodedBlock create(DigestProvider digest,
	    CipherProvider cipher, Bytes body) {
	cipher = normalizeCipherName(cipher);

	return new BlobEncodedBlock(digest, cipher, body);
    }

    public static NameEncodedBlock createName(NameLocatorKey key,
	    DigestProvider digest, CipherProvider cipher, Bytes body) {
	cipher = normalizeCipherName(cipher);

	return new NameEncodedBlock(key, digest, cipher, body);
    }

    private static String normalizeCipherName(String cipher) {
	String c = normalizeMap.get(cipher);
	if (c != null) {
	    return c;
	}

	return cipher;
    }

    private static CipherProvider normalizeCipherName(
	    final CipherProvider cipher) {
	String c = normalizeCipherName(cipher.getAlgorithm());
	if (c.equals(cipher.getAlgorithm())) {
	    return cipher;
	}
	return new CipherProvider(c) {
	    @Override
	    public Cipher get() {
		return cipher.get();
	    }
	};
    }

    public static ContentEncodedBlock createContent(DigestProvider digest,
	    CipherProvider cipher, Bytes body) {
	cipher = normalizeCipherName(cipher);

	return new ContentEncodedBlock(digest, cipher, body);
    }

    /**
     * translation map to normalize cipher names.
     */
    private static final Map<String, String> normalizeMap;

    static {
	Map<String, String> n = new HashMap<String, String>(3);
	n.put("rijndael-128-cbc", "aes-128-cbc");
	n.put("rijndael-192-cbc", "aes-192-cbc");
	n.put("rijndael-256-cbc", "aes-256-cbc");
	normalizeMap = Collections.unmodifiableMap(n);
    }
}
