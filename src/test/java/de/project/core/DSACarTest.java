package de.project.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static de.project.dsa.DSACar.createValidTimeRanges;

public class DSACarTest {

    private boolean compareRange(Range a, Range b) {
        return (Math.abs(a.getMin() - b.getMin()) < 1e-5) && (Math.abs(a.getMax() - b.getMax()) < 1e-5);
    }

    @Test
    public void testAddSelectRange1() {
        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range(1400.14, 1600));
        ranges.add(new Range(1700, 1800.356565645));
        List<Range> selectedRanges = createValidTimeRanges(ranges, 1200, 2000);
        Assert.assertTrue("must include Range", compareRange(selectedRanges.get(0), new Range(1200, 1400.14)));
        Assert.assertTrue("must include Range", compareRange(selectedRanges.get(1), new Range(1600, 1700)));
        Assert.assertTrue("must include Range", compareRange(selectedRanges.get(2), new Range(1800.356565645, 2000)));

        ranges = new ArrayList<>();
        ranges.add(new Range(1200, 1600));
        ranges.add(new Range(1700, 2000));
        selectedRanges = createValidTimeRanges(ranges, 1400, 1800);
        Assert.assertTrue("must include Range", compareRange(selectedRanges.get(0), new Range(1600, 1700)));

        ranges = new ArrayList<>();
        ranges.add(new Range(1400, 1700));
        ranges.add(new Range(1600, 1800));
        selectedRanges = createValidTimeRanges(ranges, 1200, 2000);
        Assert.assertTrue("must include Range", compareRange(selectedRanges.get(0), new Range(1200, 1400)));
        Assert.assertTrue("must include Range", compareRange(selectedRanges.get(1), new Range(1800, 2000)));

        ranges = new ArrayList<>();
        ranges.add(new Range(1300, 2100));
        ranges.add(new Range(1600, 1800));
        selectedRanges = createValidTimeRanges(ranges, 1200, 2000);
        Assert.assertTrue("must include Range", compareRange(selectedRanges.get(0), new Range(1200, 1300)));

        ranges = new ArrayList<>();
        ranges.add(new Range(1379, 3746.591624423597));
        ranges.add(new Range(3008.714287621635, 5865.857144764492));
        selectedRanges = createValidTimeRanges(ranges, 1338, 4624);
        Assert.assertTrue("must include Range", compareRange(selectedRanges.get(0), new Range(1338, 1379)));

        ranges = new ArrayList<>();
        ranges.add(new Range(237.7499790191655, 1987.7499790191655));
        selectedRanges = createValidTimeRanges(ranges, 2329, 6212);
        Assert.assertTrue("must include Range", compareRange(selectedRanges.get(0), new Range(2329, 6212)));
    }
}
