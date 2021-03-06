package org.yi.happy.archive.block;

import java.util.LinkedHashMap;
import java.util.Map;

import org.yi.happy.annotate.ExternalName;
import org.yi.happy.archive.Bytes;
import org.yi.happy.archive.key.FullKey;

/**
 * A data block that refers to another block by key.
 */
public class IndirectBlock extends AbstractBlock {

    /**
     * the meta-data field name for type.
     */
    @ExternalName
    public static final String TYPE_META = "type";

    /**
     * The type name for this block.
     */
    @ExternalName
    public static final String TYPE = "indirect";

    private final FullKey key;
    private final int size;

    /**
     * Make an indirect block.
     * 
     * @param key
     *            the key of the block this block refers to.
     */
    public IndirectBlock(FullKey key) {
        this.key = key;
        this.size = key.toString().length();
    }

    /**
     * @return the key of the block this block refers to.
     */
    public FullKey getKey() {
        return key;
    }

    @Override
    public Bytes getBody() {
        return new Bytes(key.toString());
    }

    @Override
    public Map<String, String> getMeta() {
        Map<String, String> out = new LinkedHashMap<String, String>();
        out.put(TYPE_META, TYPE);
        out.put(SIZE_META, Integer.toString(size));
        return out;
    }
}
