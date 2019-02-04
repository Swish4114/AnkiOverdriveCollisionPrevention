package de.project.core;

import org.junit.Assert;
import org.junit.Test;

public class RangeTest {

    @Test
    public void test() {
        Range range = new Range(10, 20);
        Assert.assertTrue("must be true", range.contains(15));
        Assert.assertTrue("must be true", range.contains(10));
        Assert.assertTrue("must be true", range.contains(20));
        Assert.assertFalse("must be false", range.contains(9));
        Assert.assertFalse("must be false", range.contains(21));
        Assert.assertEquals("must be 10", 10, range.getMin(), 1e-5);
        Assert.assertEquals("must be 20", 20, range.getMax(), 1e-5);
    }

}
