package org.yi.happy.archive.file_system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.yi.happy.annotate.GlobalFilesystem;
import org.yi.happy.archive.Streams;

/**
 * An implementation of {@link FileSystem} that acts on the real file system.
 */
@GlobalFilesystem
public class FileSystemFile implements FileSystem {

    @Override
    public byte[] load(String name) throws IOException {
        FileInputStream in = new FileInputStream(name);
        try {
            return Streams.load(in);
        } finally {
            in.close();
        }
    }

    @Override
    public byte[] load(String name, int limit) throws IOException {
        FileInputStream in = new FileInputStream(name);
        try {
            return Streams.load(in, limit);
        } finally {
            in.close();
        }
    }

    @Override
    public InputStream openInputStream(String name) throws IOException {
        return new FileInputStream(name);
    }

    @Override
    public void save(String name, byte[] bytes) throws IOException {
        FileOutputStream out = new FileOutputStream(name);
        try {
            out.write(bytes);
        } finally {
            out.close();
        }
    }

    @Override
    public String join(String base, String name) {
        return new File(base, name).getPath();
    }

    @Override
    public boolean mkdir(String path) throws IOException {
        File f = new File(path);

        if (f.mkdir()) {
            return true;
        }

        if (f.isDirectory()) {
            return false;
        }

        throw new IOException();
    }

    @Override
    public List<String> list(String path) throws IOException {
        String[] names = new File(path).list();
        if (names == null) {
            throw new IOException();
        }
        return Arrays.asList(names);
    }

    @Override
    public boolean isDir(String path) {
        return new File(path).isDirectory();
    }

    @Override
    public boolean isFile(String path) {
        return new File(path).isFile();
    }
}