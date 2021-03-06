package org.yi.happy.archive.block;

import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yi.happy.annotate.MagicLiteral;
import org.yi.happy.archive.BadSignatureException;
import org.yi.happy.archive.ByteString;
import org.yi.happy.archive.Bytes;
import org.yi.happy.archive.UnknownDigestAlgorithmException;
import org.yi.happy.archive.block.parser.GenericBlockParse;
import org.yi.happy.archive.crypto.Cipher;
import org.yi.happy.archive.crypto.CipherProvider;
import org.yi.happy.archive.crypto.DigestProvider;
import org.yi.happy.archive.key.BlobFullKey;
import org.yi.happy.archive.key.BlobLocatorKey;
import org.yi.happy.archive.key.FullKey;
import org.yi.happy.archive.key.HashValue;

/**
 * A valid blob encoded block.
 */
public final class BlobEncodedBlock extends AbstractBlock implements
        EncodedBlock {
    private final BlobLocatorKey key;
    private final DigestProvider digest;
    private final CipherProvider cipher;
    private final Bytes body;

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
     * @throws BadSignatureException
     *             if the signature of the body does not match the signature
     *             given in the key.
     * @throws UnknownDigestAlgorithmException
     *             if the digest provider can not create the digest
     *             implementation.
     */
    public BlobEncodedBlock(BlobLocatorKey key, DigestProvider digest,
            CipherProvider cipher, Bytes body) throws IllegalArgumentException,
            BadSignatureException, UnknownDigestAlgorithmException {
        checkHeader(DIGEST_META, digest.getAlgorithm());
        checkHeader(CIPHER_META, cipher.getAlgorithm());

        byte[] hash = getHash(digest, cipher, body);
        if (!key.getHash().equalBytes(hash)) {
            throw new BadSignatureException();
        }

        this.key = key;
        this.digest = digest;
        this.cipher = cipher;
        this.body = body;
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
    public BlobEncodedBlock(DigestProvider digest, CipherProvider cipher,
            Bytes body) {
        checkHeader(DIGEST_META, digest.getAlgorithm());
        checkHeader(CIPHER_META, cipher.getAlgorithm());

        byte[] hash = getHash(digest, cipher, body);

        this.key = new BlobLocatorKey(new HashValue(hash));
        this.digest = digest;
        this.cipher = cipher;
        this.body = body;
    }

    @Override
    public BlobLocatorKey getKey() {
        return key;
    }

    @Override
    public DigestProvider getDigest() {
        return digest;
    }

    @Override
    public CipherProvider getCipher() {
        return cipher;
    }

    @Override
    public Bytes getBody() {
        return body;
    }

    @Override
    public Map<String, String> getMeta() {
        Map<String, String> out = new LinkedHashMap<String, String>();
        out.put(KEY_TYPE_META, key.getType());
        out.put(KEY_META, key.getHash().toString());
        out.put(DIGEST_META, digest.getAlgorithm());
        out.put(CIPHER_META, cipher.getAlgorithm());
        out.put(SIZE_META, Integer.toString(body.getSize()));
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
    @MagicLiteral
    public byte[] getHash(DigestProvider digest, CipherProvider cipher,
            Bytes body) {
        MessageDigest d = digest.get();
        d.update(ByteString.toUtf8(DIGEST_META + ": "));
        d.update(ByteString.toUtf8(digest.getAlgorithm()));
        d.update(ByteString.toUtf8("\r\n" + CIPHER_META + ": "));
        d.update(ByteString.toUtf8(cipher.getAlgorithm()));
        d.update(ByteString.toUtf8("\r\n" + SIZE_META + ": "));
        d.update(ByteString.toUtf8(Integer.toString(body.getSize())));
        d.update(ByteString.toUtf8("\r\n" + "\r\n"));
        d.update(body.toByteArray());
        return d.digest();
    }

    @Override
    public GenericBlock decode(FullKey fullKey) {
        if (!fullKey.toLocatorKey().equals(key)) {
            throw new IllegalArgumentException("the key is not for this block");
        }
        BlobFullKey k = (BlobFullKey) fullKey;

        Cipher c = cipher.get();
        c.setKey(k.getPass().toByteArray());

        if (body.getSize() % c.getBlockSize() != 0) {
            throw new IllegalArgumentException(
                    "body size is not a multiple of the cipher block size");
        }

        byte[] out = body.toByteArray();
        c.decrypt(out);

        return GenericBlockParse.parse(out);
    }
}
