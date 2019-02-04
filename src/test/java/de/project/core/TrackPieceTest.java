package de.project.core;

import de.pdbm.janki.core.RoadPiece;
import de.pdbm.janki.core.notifications.PositionUpdate;
import org.junit.Assert;
import org.junit.Test;

public class TrackPieceTest {

    @Test
    public void testTrackLineOnCurve() {
        int [][] trackLinesIds = new int[][] {
                {0, 1},
                {2, 3},
                {4, 5},
                {6, 7},
                {8, 9},
                {10, 11},
                {12, 13},
                {14, 15},
                {16, 17},
                {18, 19},
                {20, 21, 22},
                {23, 24, 25},
                {26, 27, 28},
                {29, 30, 31},
                {32, 33, 34},
                {35, 36, 37}
        };

        for (int i = 0; i < trackLinesIds.length; i++) {
            int[] trackLineIds = trackLinesIds[i];
            for (int id: trackLineIds) {
                Assert.assertEquals("Not correct trackpiece line. Mirrored false", i, TrackPiece.getCurveTrackLine(id, false));
                Assert.assertEquals("Not correct trackpiece line. Mirrored true", 15 - i, TrackPiece.getCurveTrackLine(id, true));
            }
        }
    }

    @Test
    public void testLocationPositionOnLine() {
        PositionUpdate pu = new PositionUpdate(null, 0, RoadPiece.CORNER, 17, true, 500);
        Assert.assertEquals("Must be first", TrackPiece.LocationPosition.FIRST, TrackPiece.getLocationPositionOnCurveLine(pu));

        pu = new PositionUpdate(null, 1, RoadPiece.CORNER, 17, true, 500);
        Assert.assertEquals("Must be last", TrackPiece.LocationPosition.LAST, TrackPiece.getLocationPositionOnCurveLine(pu));

        pu = new PositionUpdate(null, 32, RoadPiece.CORNER, 17, true, 500);
        Assert.assertEquals("Must be first", TrackPiece.LocationPosition.FIRST, TrackPiece.getLocationPositionOnCurveLine(pu));

        pu = new PositionUpdate(null, 33, RoadPiece.CORNER, 17, true, 500);
        Assert.assertEquals("Must be middle", TrackPiece.LocationPosition.MIDDLE, TrackPiece.getLocationPositionOnCurveLine(pu));

        pu = new PositionUpdate(null, 34, RoadPiece.CORNER, 17, true, 500);
        Assert.assertEquals("Must be last", TrackPiece.LocationPosition.LAST, TrackPiece.getLocationPositionOnCurveLine(pu));
    }

    @Test
    public void testCurveLength() {
        PositionUpdate pu = new PositionUpdate(null, 16, RoadPiece.CORNER, 17, true, 500);
        Assert.assertEquals("Not correct length", 446.1, TrackPiece.getCurveLength(pu, false), 1e-5);

        pu = new PositionUpdate(null, 0, RoadPiece.CORNER, 17, true, 500);
        Assert.assertEquals("Not correct length", 333.0, TrackPiece.getCurveLength(pu, false), 1e-5);

        pu = new PositionUpdate(null, 36, RoadPiece.CORNER, 17, true, 500);
        Assert.assertEquals("Not correct length", 545.1, TrackPiece.getCurveLength(pu, false), 1e-5);
    }
}
