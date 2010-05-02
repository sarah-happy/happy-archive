package org.yi.happy.archive.file_system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * tests for {@link Path}.
 */
public class PathTest {
    /*
     * as a concept, the most general path is what is acceptable after the
     * domain of a URL, any length string in each element, the first element of
     * the path is either absolute or relative to differentiate the two cases.
     */

    /*
     * The file path will be used as a look up key for the file system
     * implementations.
     */

    /**
     * Thinking about this from the point of view of the fake file system first,
     * given a path, I want to know the base name of the path, in other words
     * the part of the path before the final name. The base will be used as a
     * hash key as well.
     */
    @Test
    public void testBase1() {
        Path p = Path.ABSOLUTE.child("foo").child("bar");

        Path have = p.getBase();

        Path want = Path.ABSOLUTE.child("foo");
        assertEquals(want, have);
        assertEquals(want.hashCode(), have.hashCode());
    }

    /**
     * The base of the absolute path is the absolute path
     */
    @Test
    public void testBase2() {
        assertEquals(Path.ABSOLUTE, Path.ABSOLUTE.getBase());
    }

    /**
     * The base of the relative path is the base of the relative path
     */
    @Test
    public void testBase3() {
        assertEquals(Path.RELATIVE, Path.RELATIVE.getBase());
    }

    /**
     * two constructed paths to the same place should be equal.
     */
    @Test
    public void testEqual1() {
        Path have1 = Path.ABSOLUTE.child("foo");
        Path have2 = Path.ABSOLUTE.child("foo");

        assertTrue(have1.equals(have2));
        assertEquals(have1.hashCode(), have2.hashCode());
    }

    /**
     * build up two directories.
     */
    @Test
    public void testParent4() {
        Path have = Path.RELATIVE.child("..").child("..");

        assertEquals("../..", have.toString());
    }

    /**
     * build an absolute path
     */
    @Test
    public void test1() {
        Path have = Path.ABSOLUTE.child("foo");

        assertEquals("/foo", have.toString());
    }

    /**
     * build an absolute path
     */
    @Test
    public void test2() {
        Path have = Path.ABSOLUTE.child("foo").child("bar");

        assertEquals("/foo/bar", have.toString());
    }

    /**
     * build a relative path
     */
    @Test
    public void test3() {
        Path have = Path.RELATIVE.child("foo");

        assertEquals("foo", have.toString());
    }

    /**
     * get the parent of a relative path.
     */
    @Test
    public void testParent1() {
        Path have = Path.RELATIVE.child("foo").parent();

        assertEquals(Path.RELATIVE, have);
    }

    /**
     * the parent of the absolute base is the absolute base.
     */
    @Test
    public void testParent2() {
        Path have = Path.ABSOLUTE.parent();

        assertEquals(Path.ABSOLUTE, have);
    }

    /**
     * the parent of the relative base is "..".
     */
    @Test
    public void testParent3() {
        Path have = Path.RELATIVE.parent();

        assertEquals("..", have.toString());
    }

    /**
     * the parent of a child is the original.
     */
    @Test
    public void testEqual2() {
        Path have1 = Path.ABSOLUTE.child("foo").parent();
        Path have2 = Path.ABSOLUTE;

        assertTrue(have1.equals(have2));
        assertEquals(have1.hashCode(), have2.hashCode());
    }

    /**
     * the parent of an absolute child is not relative.
     */
    @Test
    public void testEqual3() {
        Path have1 = Path.ABSOLUTE.child("foo").parent();
        Path have2 = Path.RELATIVE;

        assertFalse(have1.equals(have2));
    }

    /**
     * two parents from relative is the same.
     */
    @Test
    public void testEqual4() {
        Path have1 = Path.RELATIVE.parent().parent();
        Path have2 = Path.RELATIVE.parent().parent();

        assertTrue(have1.equals(have2));
        assertEquals(have1.hashCode(), have2.hashCode());
    }

    /**
     * Check that the size calculates right.
     */
    @Test
    public void testSize0() {
        Path p = Path.RELATIVE;

        assertEquals(0, p.size());
    }

    /**
     * Check that the size calculates right.
     */
    @Test
    public void testSize1() {
        Path p = Path.RELATIVE.child("foo");

        assertEquals(1, p.size());
    }

    /**
     * Check that the size calculates right.
     */
    @Test
    public void testSize2() {
        Path p = Path.RELATIVE.child("foo").child("bar");

        assertEquals(2, p.size());
    }
}