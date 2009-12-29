package org.yi.happy.archive;

import java.io.IOException;
import java.io.OutputStream;

import org.yi.happy.archive.key.FullKey;
import org.yi.happy.archive.key.KeyParse;


/**
 * A command line tool to decode a single block. The block is read from the
 * named file and the given full key is used to decode it. The decoded block is
 * sent to standard output.
 */
public class DecodeBlockMain {
	private final FileSystem fs;
	private final OutputStream out;

	public DecodeBlockMain(FileSystem fs, OutputStream out) {
		this.fs = fs;
		this.out = out;
	}

	public static void main(String[] args) throws Exception {
		FileSystem fs = new RealFileSystem();
		OutputStream out = System.out;

		new DecodeBlockMain(fs, out).run(args);

		out.flush();
	}

	public void run(String... args) throws IOException {
		EncodedBlock b = new EncodedBlockParse().parse(fs.load(args[0],
				Blocks.MAX_SIZE));
		FullKey k = new KeyParse().parseFullKey(args[1]);
		Block d = b.decode(k);
		out.write(d.asBytes());
	}
}
