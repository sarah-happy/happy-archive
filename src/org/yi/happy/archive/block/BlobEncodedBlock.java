package org.yi.happy.archive.block;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yi.happy.archive.BlockParse;
import org.yi.happy.archive.ByteString;
import org.yi.happy.archive.Cipher;
import org.yi.happy.archive.CipherFactory;
import org.yi.happy.archive.DigestFactory;
import org.yi.happy.archive.UnknownDigestException;
import org.yi.happy.archive.key.BlobFullKey;
import org.yi.happy.archive.key.BlobLocatorKey;
import org.yi.happy.archive.key.FullKey;
import org.yi.happy.archive.key.HexEncode;
import org.yi.happy.archive.key.UnknownAlgorithmException;

/**
 * A valid blob encoded block.
 */
public final class BlobEncodedBlock extends AbstractBlock implements
	EncodedBlock {
    private final BlobLocatorKey key;
    private final String digest;
    private final String cipher;
    private final byte[] body;

    /**
     * create with all details available, they are checked.
     * 
     * @param key
     *            the locator key for the block.
     * @param digest
     *            the digest being used.
     * @param cipher
     *            the cipher being used.
     * @param body
     *            the bytes of the body.
     * @throws IllegalArgumentException
     *             if the details do not check out.
     */
    public BlobEncodedBlock(BlobLocatorKey key, String digest, String cipher,
	    byte[] body) {
	GenericBlock.checkValue(digest);
	GenericBlock.checkValue(cipher);

	byte[] hash = getHash(digest, cipher, body);
	if (!Arrays.equals(key.getHash(), hash)) {
	    throw new IllegalArgumentException();
	}

	this.key = key;
	this.digest = digest;
	this.cipher = cipher;
	this.body = body.clone();
    }

    /**
     * create with minimal details, the rest are calculated.
     * 
     * @param digest
     *            the digest being used.
     * @param cipher
     *            the cipher being used.
     * @param body
     *            the bytes of the body.
     * @throws IllegalArgumentException
     *             if the details are invalid.
     */
    public BlobEncodedBlock(String digest, String cipher, byte[] body) {
	GenericBlock.checkValue(digest);
	GenericBlock.checkValue(cipher);

	byte[] hash = getHash(digest, cipher, body);

	this.key = new BlobLocatorKey(hash);
	this.digest = digest;
	this.cipher = cipher;
	this.body = body.clone();
    }

    public BlobLocatorKey getKey() {
	return key;
    }

    public String getDigest() {
	return digest;
    }

    public String getCipher() {
	return cipher;
    }

    public byte[] getBody() {
	return body.clone();
    }

    @Override
    public Map<String, String> getMeta() {
	Map<String, String> out = new LinkedHashMap<String, String>();
	out.put("key-type", key.getType());
	out.put("key", HexEncode.encode(key.getHash()));
	out.put("digest", digest);
	out.put("cipher", cipher);
	out.put("size", Integer.toString(body.length));
	return out;
    }

    /**
     * get the blob hash for a block.
     * 
     * @param digest
     *            the normalized digest name.
     * @param cipher
     *            the normalized cipher name.
     * @param body
     *            the body.
     * @return the hash value.
     */
    public static byte[] getHash(String digest, String cipher, byte[] body) {
	try {
	    MessageDigest d = DigestFactory.create(digest);
	    d.update(ByteString.toUtf8("digest: "));
	    d.update(ByteString.toUtf8(digest));
	    d.update(ByteString.toUtf8("\r\ncipher: "));
	    d.update(ByteString.toUtf8(cipher));
	    d.update(ByteString.toUtf8("\r\nsize: "));
	    d.update(ByteString.toUtf8(Integer.toString(body.length)));
	    d.update(ByteString.toUtf8("\r\n\r\n"));
	    d.update(body);
	    return d.digest();
	} catch (UnknownAlgorithmException e) {
	    throw new UnknownDigestException(digest, e);
	}
    }

    public Block decode(FullKey fullKey) {
	if (!(fullKey instanceof BlobFullKey)) {
	    throw new IllegalArgumentException();
	}
	BlobFullKey k = (BlobFullKey) fullKey;

	Cipher c = CipherFactory.create(this.cipher);
	c.setPass(k.getPass());

	if (body.length % c.getBlockSize() != 0) {
	    throw new IllegalArgumentException(
		    "size is not a multiple of the cipher block size");
	}

	byte[] out = body.clone();
	c.decrypt(out);

	return BlockParse.load(out);
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + Arrays.hashCode(body);
	result = prime * result + ((cipher == null) ? 0 : cipher.hashCode());
	result = prime * result + ((digest == null) ? 0 : digest.hashCode());
	result = prime * result + ((key == null) ? 0 : key.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	BlobEncodedBlock other = (BlobEncodedBlock) obj;
	if (!Arrays.equals(body, other.body))
	    return false;
	if (cipher == null) {
	    if (other.cipher != null)
		return false;
	} else if (!cipher.equals(other.cipher))
	    return false;
	if (digest == null) {
	    if (other.digest != null)
		return false;
	} else if (!digest.equals(other.digest))
	    return false;
	if (key == null) {
	    if (other.key != null)
		return false;
	} else if (!key.equals(other.key))
	    return false;
	return true;
    }
}