package org.yi.happy.archive;

import java.io.IOException;
import java.io.InputStream;

import org.yi.happy.annotate.EntryPoint;
import org.yi.happy.archive.block.encoder.BlockEncoderFactory;
import org.yi.happy.archive.commandLine.MyArgs;
import org.yi.happy.archive.commandLine.MyArgs.CommandLineException;
import org.yi.happy.archive.crypto.DigestFactory;
import org.yi.happy.archive.file_system.FileSystem;
import org.yi.happy.archive.file_system.RealFileSystem;
import org.yi.happy.archive.tag.Tag;
import org.yi.happy.archive.tag.TagBuilder;
import org.yi.happy.archive.tag.TagOutputStream;
import org.yi.happy.archive.tag.TagStreamIterator;

public class FileStoreTagAddMain {
    @EntryPoint
    public static void main(String[] args) throws IOException {
        /*
         * read a stream of tags of standard input, for the file tags that lack
         * a data attribute, store the file and fill in the data and hash
         * attribute.
         */
        FileSystem fs = new RealFileSystem();
        try {
            /*
             * parse command line
             */
            MyArgs cmd;
            try {
                cmd = MyArgs.parse(args);
            } catch (CommandLineException e) {
                e.showHelp(System.out);
                return;
            }

            if (cmd.getStore() == null) {
                MyArgs.showHelp(System.out);
                return;
            }

            FileBlockStore store = new FileBlockStore(fs, cmd.getStore());

            /*
             * do the work
             */

            StoreBlockStorage s = new StoreBlockStorage(
                    BlockEncoderFactory.getContentDefault(), store);

            TagOutputStream out = new TagOutputStream(System.out);
            TagStreamIterator in = new TagStreamIterator(System.in);

            for (Tag tag : in) {
                tag = process(tag, s, fs);
                out.write(tag);
            }
        } finally {
            System.out.flush();
        }
    }

    private static Tag process(Tag tag, StoreBlockStorage s, FileSystem fs)
            throws IOException {
        if (tag.get("data") != null) {
            return tag;
        }

        String name = tag.get("name");
        if (name == null) {
            return tag;
        }

        String type = tag.get("type");
        if (type != null && !type.equals("file")) {
            return tag;
        }

        if (!fs.isFile(name)) {
            return tag;
        }

        KeyOutputStream o1 = new KeyOutputStream(s);
        DigestOutputStream o2 = new DigestOutputStream(DigestFactory
                .getProvider("sha-256").get());
        TeeOutputStream o = new TeeOutputStream(o1, o2);
        InputStream in = fs.openInputStream(name);
        try {
            Streams.copy(in, o);
        } finally {
            in.close();
        }
        o.close();

        return new TagBuilder(tag).put("type", "file")
                .put("size", Long.toString(o2.getSize()))
                .put("data", o1.getFullKey().toString())
                .put("hash", "sha-256:" + o2.getHash()).create();
    }
}
