package org.yi.happy.archive;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.yi.happy.archive.block.Block;
import org.yi.happy.archive.block.EncodedBlock;
import org.yi.happy.archive.key.FullKey;

/**
 * Block retrieval and decoding service that connects to a block store.
 */
public class ClearBlockSourceStore implements ClearBlockSource {

    /**
     * create attached to a storage object
     * 
     * @param blocks
     *            the block store.
     */
    public ClearBlockSourceStore(BlockStore blocks) {
        this.blocks = blocks;
    }

    /**
     * the storage service I am attached to.
     */
    private final BlockStore blocks;

    @Override
    public Block get(FullKey key) throws IOException {
        EncodedBlock b;
        try {
            b = blocks.get(key.toLocatorKey());
            if (b == null) {
                return null;
            }
        } catch (IllegalArgumentException e) {
            return null;
        } catch (FileNotFoundException e) {
            return null;
        } catch (DecodeException e) {
            return null;
        }

        try {
            return b.decode(key);
        } catch (IllegalArgumentException e) {
            throw new DecodeException(e);
        }
    }
}
