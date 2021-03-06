package org.yi.happy.archive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yi.happy.archive.block.EncodedBlock;
import org.yi.happy.archive.key.LocatorKey;

/**
 * A storage driver backed by memory. So far this could probably be replaced
 * with a logging StoreBlock implementation.
 */
public class BlockStoreMemory implements BlockStore {
    private Map<LocatorKey, EncodedBlock> data = new HashMap<LocatorKey, EncodedBlock>();
    private Set<LocatorKey> broken = new HashSet<LocatorKey>();

    @Override
    public LocatorKey put(EncodedBlock block) throws IOException {
        LocatorKey key = block.getKey();
        broken.remove(key);
        data.put(key, block);
        return key;
    }

    /**
     * test if the store contains the given key.
     * 
     * @param key
     *            the key to check for.
     * @return true if the store contains the key.
     */
    @Override
    public boolean contains(LocatorKey key) {
        return data.containsKey(key) || broken.contains(key);
    }

    @Override
    public EncodedBlock get(LocatorKey key) throws IOException {
        if (broken.contains(key)) {
            throw new DecodeException();
        }
        return data.get(key);
    }

    @Override
    public void remove(LocatorKey key) throws IOException {
        broken.remove(key);
        data.remove(key);
    }

    @Override
    public long getTime(LocatorKey key) throws IOException {
        return 0;
    }

    /**
     * Make retrieval of a particular key throw an exception. Used to check that
     * store failures are handled properly.
     * 
     * @param key
     *            the key to mark in the store.
     */
    public void putBroken(LocatorKey key) {
        data.remove(key);
        broken.add(key);
    }

    @Override
    public Iterator<LocatorKey> iterator() {
        List<LocatorKey> keys = new ArrayList<LocatorKey>(data.keySet());
        keys.addAll(broken);
        Collections.sort(keys);
        return keys.iterator();
    }
}
