package org.yi.happy.archive.block.encoder;

import org.yi.happy.archive.crypto.CipherFactory;
import org.yi.happy.archive.crypto.DigestFactory;

/**
 * A factory for common configurations of the block encoder.
 */
public class BlockEncoderFactory {
    /**
     * Create a default content encoder. This default is historical but more
     * recent.
     * 
     * @return create a content block encoder configured with sha-256 and
     *         aes-128-cbc.
     */
    public static BlockEncoder getContentDefault() {
        return new BlockEncoderContent(DigestFactory.getProvider("sha-256"),
                CipherFactory.getProvider("aes-128-cbc"));
    }

    /**
     * Create a default content encoder. This default is historical.
     * 
     * @return create a content block encoder configured with sha-256 and
     *         rijndael256-256-cbc.
     */
    public static BlockEncoder getContentOldDefault() {
        return new BlockEncoderContent(DigestFactory.getProvider("sha-256"),
                CipherFactory.getProvider("rijndael256-256-cbc"));
    }
}
