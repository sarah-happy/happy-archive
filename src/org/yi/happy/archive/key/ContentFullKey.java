package org.yi.happy.archive.key;

/**
 * a content full key. what is required to decode a content block.
 */
public final class ContentFullKey extends AbstractContentFullKey implements
	FullKey {

    @Override
    public String getType() {
	return KeyType.CONTENT_HASH;
    }

    /**
     * create
     * 
     * @param hash
     *            the hash
     * @param pass
     *            the cipher key
     */
    public ContentFullKey(byte[] hash, byte[] pass) {
	super(hash, pass);
    }
}
