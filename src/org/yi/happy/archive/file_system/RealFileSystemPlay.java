package org.yi.happy.archive.file_system;

import java.io.IOException;

public class RealFileSystemPlay {
    public static void main(String[] args) throws IOException {
	RealFileSystem fs = new RealFileSystem();
	fs.mkdir("a");

	/*
	 * save a temporary file
	 */
	fs.save("a/a.tmp", new byte[] { 1 });

	/*
	 * rename to a final file
	 */
	fs.rename("a/a.tmp", "a/a");

	/*
	 * load the final file
	 */
	System.out.println((int) fs.load("a/a")[0]);

	/*
	 * save a temporary file
	 */
	fs.save("a/a.tmp", new byte[] { 2 });

	/*
	 * rename to a final file
	 */
	fs.rename("a/a.tmp", "a/a");

	/*
	 * load the final file
	 */
	System.out.println((int) fs.load("a/a")[0]);

	fs.mkdir("a/b");

	fs.rename("a/a", "a/b");
    }
}