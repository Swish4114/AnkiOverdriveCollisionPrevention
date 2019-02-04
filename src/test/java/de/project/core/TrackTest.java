package de.project.core;

import de.pdbm.janki.core.RoadPiece;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TrackTest {

    @Test
    public void test() {
        List<TrackPiece> expectedTrackPieces = new ArrayList<>();
        expectedTrackPieces.add(new TrackPiece(RoadPiece.INTERSECTION, 0));
        expectedTrackPieces.add(new TrackPiece(RoadPiece.CORNER, 1));
        expectedTrackPieces.add(new TrackPiece(RoadPiece.CORNER, 2));
        expectedTrackPieces.add(new TrackPiece(RoadPiece.CORNER, 3));
        expectedTrackPieces.add(new TrackPiece(RoadPiece.INTERSECTION, 4));
        expectedTrackPieces.add(new TrackPiece(RoadPiece.CORNER, 5));
        expectedTrackPieces.add(new TrackPiece(RoadPiece.CORNER, 6));
        expectedTrackPieces.add(new TrackPiece(RoadPiece.CORNER, 7));
        expectedTrackPieces.add(new TrackPiece(RoadPiece.INTERSECTION, 8));

        for (int i = 0; i < expectedTrackPieces.size(); i++) {
            TrackPiece expectedTrackPiece = expectedTrackPieces.get(i);
            Assert.assertTrue("TrackPiece is not correct road piece",
                    Track.getInstance().getTrackPieces().get(i).equals(expectedTrackPiece.getRoadPiece()));
            Assert.assertEquals("TrackPiece does not have correct unique id",
                    expectedTrackPiece.getUniqueId(), Track.getInstance().getTrackPieces().get(i).getUniqueId());
        }
    }
}
