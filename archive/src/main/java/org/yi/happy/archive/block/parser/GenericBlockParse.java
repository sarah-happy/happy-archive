package org.yi.happy.archive.block.parser;

import java.util.LinkedHashMap;
import java.util.Map;

import org.yi.happy.annotate.SmellsProcedural;
import org.yi.happy.archive.ByteString;
import org.yi.happy.archive.Bytes;
import org.yi.happy.archive.block.Block;
import org.yi.happy.archive.block.GenericBlock;

/**
 * parser for {@link GenericBlock}.
 */
@SmellsProcedural
public class GenericBlockParse {

    /**
     * A very tolerant generic block parser. If the size header is present then
     * it must be valid, and the body is trimmed to the value of it.
     * 
     * @param bytes
     *            the bytes for the block.
     * @return the parsed block.
     * @throws IllegalArgumentException
     *             if a header is repeated, or if the size header is not valid.
     */
    public static GenericBlock parse(byte[] bytes) {
        Map<String, String> meta = new LinkedHashMap<String, String>();

        Segment rest = new Segment(0, bytes.length);

        /*
         * parse the headers
         */
        while (true) {
            Segment endOfLine = findNewLine(bytes, rest);
            Segment line = rest.before(endOfLine);
            rest = rest.after(endOfLine);

            /*
             * if the line has zero length, then the headers are done and the
             * rest is the body.
             */
            if (line.getLength() == 0) {
                break;
            }

            Segment divider = findDivider(bytes, line);

            String name = ByteString.fromUtf8(bytes, line.before(divider));
            String value = ByteString.fromUtf8(bytes, line.after(divider));

            if (meta.containsKey(name)) {
                throw new IllegalArgumentException("repeated header: " + name);
            }
            meta.put(name, value);
        }

        trim: {
            /*
             * if the size header is present...
             */
            String s = meta.get(GenericBlock.SIZE_META);
            if (s == null) {
                break trim;
            }

            /*
             * it is required to be valid, it is not valid if it does not parse
             * as a number, or it is less than zero, or it is greater than the
             * length of the rest of the data.
             */
            int i;
            try {
                i = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("bad size header");
            }
            if (i < 0 || i > rest.getLength()) {
                throw new IllegalArgumentException("bad size header");
            }

            /*
             * if it matches the size of the rest of the data, then it does not
             * need trimming.
             */
            if (i == rest.getLength()) {
                break trim;
            }

            /*
             * otherwise trim it.
             */
            rest = new Segment(rest.getOffset(), i);
        }

        /*
         * copy the body out of the buffer.
         */
        Bytes body = new Bytes(bytes, rest.getOffset(), rest.getLength());

        return new GenericBlock(meta, body);
    }

    /**
     * find the field key/value divider in a line.
     * 
     * @param bytes
     *            the bytes being parsed.
     * @param line
     *            the segment that is the line.
     * @return the segment that is the divider, or the end of the segment if not
     *         found. Before this segment is the key, after this segment is the
     *         value.
     */
    private static Segment findDivider(byte[] bytes, Segment line) {
        for (int i = line.getOffset(); i < line.getEnd(); i++) {
            if (bytes[i] == Block.COLON) {
                if (i + 1 < line.getEnd() && bytes[i + 1] == Block.SPACE) {
                    return new Segment(i, 2);
                }
                return new Segment(i, 1);
            }
        }

        return new Segment(line.getEnd(), 0);
    }

    /**
     * find the first line break in the segment.
     * 
     * @param bytes
     *            the bytes.
     * @param segment
     *            the segment to search in.
     * @return segment that is the line break, or the end of the segment if none
     *         found. Before this segment is the first line, after this segment
     *         is the start of the next line.
     */
    private static Segment findNewLine(byte[] bytes, Segment segment) {
        for (int i = segment.getOffset(); i < segment.getEnd(); i++) {
            if (bytes[i] == Block.CR) {
                if (i + 1 < segment.getEnd() && bytes[i + 1] == Block.LF) {
                    return new Segment(i, 2);
                }
                return new Segment(i, 1);
            }
            if (bytes[i] == Block.LF) {
                if (i + 1 < segment.getEnd() && bytes[i + 1] == Block.CR) {
                    return new Segment(i, 2);
                }
                return new Segment(i, 1);
            }
        }

        return new Segment(segment.getEnd(), 0);
    }
}
