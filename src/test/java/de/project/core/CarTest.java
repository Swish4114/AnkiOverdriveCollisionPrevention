package de.project.core;

import de.pdbm.janki.core.IntersectionCode;
import de.pdbm.janki.core.RoadPiece;
import de.pdbm.janki.core.Vehicle;
import de.pdbm.janki.core.notifications.IntersectionUpdate;
import de.pdbm.janki.core.notifications.PositionUpdate;
import de.pdbm.janki.core.notifications.TransitionUpdate;
import org.junit.Assert;
import org.junit.Test;

public class CarTest {

    public static class TestCar extends Car {
        public TestCar(Vehicle vehicle, TrackPiece currentTrackPiece) {
            super(vehicle, currentTrackPiece);
        }

        @Override
        public void handlePositionUpdate(PositionUpdate positionUpdate) {

        }

        @Override
        public void handleIntersectionUpdate(IntersectionUpdate intersectionUpdate) {

        }

        @Override
        public void handleTransitionUpdate() {

        }
    }

    @Test
    public void testPositionUpdate() {
        Vehicle vehicle = Vehicle.getVehicles().get(0);
        Car car = new TestCar(vehicle, Track.getInstance().getTrackPieces().get(1));

        Assert.assertEquals("First TrackPiece must be Corner", new TrackPiece(RoadPiece.CORNER, 1), car.currentTrackPiece);
        Assert.assertNull("PositonUpdate must be null", car.currentPositionUpdate);


        PositionUpdate pu = new PositionUpdate(vehicle, 2, RoadPiece.CORNER, 10, true, 500);
        car.onPositionUpdate(pu);
        Assert.assertSame("Wrong currentPositionUpdate", pu, car.currentPositionUpdate);

        PositionUpdate pu2 = new PositionUpdate(vehicle, 5, RoadPiece.CORNER, 10, true, 500);
        car.onPositionUpdate(pu2);
        Assert.assertSame("Wrong currentPositionUpdate", pu2, car.currentPositionUpdate);
    }

    @Test
    public void testTransitionUpdate() {
        Vehicle vehicle = Vehicle.getVehicles().get(0);
        Car car = new TestCar(vehicle, Track.getInstance().getTrackPieces().get(0));

        Assert.assertEquals("Current TrackPiece must be Intersection 0",
                new TrackPiece(RoadPiece.INTERSECTION, 0), car.currentTrackPiece);

        TransitionUpdate tu = new TransitionUpdate(vehicle, 0, null);

        car.onTransitionUpdate(tu);
        Assert.assertEquals("Current TrackPiece must be Corner 1",
                new TrackPiece(RoadPiece.CORNER, 1), car.currentTrackPiece);

        car.onTransitionUpdate(tu);
        Assert.assertEquals("Current TrackPiece must be Corner 2",
                new TrackPiece(RoadPiece.CORNER, 2), car.currentTrackPiece);

        car.onTransitionUpdate(tu);
        Assert.assertEquals("Current TrackPiece must be Corner 3",
                new TrackPiece(RoadPiece.CORNER, 3), car.currentTrackPiece);

        car.onTransitionUpdate(tu);
        Assert.assertEquals("Current TrackPiece must be Intersection 4",
                new TrackPiece(RoadPiece.INTERSECTION, 4), car.currentTrackPiece);

        car.onTransitionUpdate(tu);
        Assert.assertEquals("Current TrackPiece must be Corner 5",
                new TrackPiece(RoadPiece.CORNER, 5), car.currentTrackPiece);

        car.onTransitionUpdate(tu);
        Assert.assertEquals("Current TrackPiece must be Corner 6",
                new TrackPiece(RoadPiece.CORNER, 6), car.currentTrackPiece);

        car.onTransitionUpdate(tu);
        Assert.assertEquals("Current TrackPiece must be Corner 7",
                new TrackPiece(RoadPiece.CORNER, 7), car.currentTrackPiece);

        car.onTransitionUpdate(tu);
        Assert.assertEquals("Current TrackPiece must be Intersection 8",
                new TrackPiece(RoadPiece.INTERSECTION, 8), car.currentTrackPiece);

        //one round completed

        car.onTransitionUpdate(tu);
        Assert.assertEquals("Current TrackPiece must be Corner 1",
                new TrackPiece(RoadPiece.CORNER, 1), car.currentTrackPiece);

        car.onTransitionUpdate(tu);
        Assert.assertEquals("Current TrackPiece must be Corner 2",
                new TrackPiece(RoadPiece.CORNER, 2), car.currentTrackPiece);

        car.onTransitionUpdate(tu);
        Assert.assertEquals("Current TrackPiece must be Corner 3",
                new TrackPiece(RoadPiece.CORNER, 3), car.currentTrackPiece);

        car.onTransitionUpdate(tu);
        Assert.assertEquals("Current TrackPiece must be Intersection 4",
                new TrackPiece(RoadPiece.INTERSECTION, 4), car.currentTrackPiece);

        car.onTransitionUpdate(tu);
        Assert.assertEquals("Current TrackPiece must be Corner 5",
                new TrackPiece(RoadPiece.CORNER, 5), car.currentTrackPiece);

        car.onTransitionUpdate(tu);
        Assert.assertEquals("Current TrackPiece must be Corner 6",
                new TrackPiece(RoadPiece.CORNER, 6), car.currentTrackPiece);

        car.onTransitionUpdate(tu);
        Assert.assertEquals("Current TrackPiece must be Corner 7",
                new TrackPiece(RoadPiece.CORNER, 7), car.currentTrackPiece);

        car.onTransitionUpdate(tu);
        Assert.assertEquals("Current TrackPiece must be Intersection 8",
                new TrackPiece(RoadPiece.INTERSECTION, 8), car.currentTrackPiece);

    }


    @Test
    public void testGetRestLengthToIntersection() {
        Vehicle vehicle = Vehicle.getVehicles().get(0);
        long s = System.currentTimeMillis();
        vehicle.setSpeed(500);

        final double halfEigthLength = 333 * 3 + 190;

        Car car = new TestCar(vehicle, Track.getInstance().getTrackPieces().get(7));
        car.onPositionUpdate(new PositionUpdate(vehicle, 35, RoadPiece.CORNER, 15, false, 500));
        car.onTransitionUpdate(new TransitionUpdate(vehicle, 0, null));
        //now must be on Intersection
        car.onPositionUpdate(new PositionUpdate(vehicle, 1, RoadPiece.INTERSECTION, 10, true, 500));
        car.onIntersectionUpdate(new IntersectionUpdate(vehicle, IntersectionCode.ENTRY_FIRST, false));
        car.onIntersectionUpdate(new IntersectionUpdate(vehicle, IntersectionCode.EXIT_FIRST, true));


        Assert.assertEquals("Wrong length to intersection",
                halfEigthLength, car.getRestLengthToIntersectionInMm(),1e-5);

        car.onTransitionUpdate(new TransitionUpdate(vehicle, 0, null)); //now must be Corner 1

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals("Wrong length to intersection",
                halfEigthLength - 0.1 * 500, car.getRestLengthToIntersectionInMm(),3);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals("Wrong length to intersection",
                halfEigthLength - 0.2 * 500, car.getRestLengthToIntersectionInMm(),3);


    }

}
