package de.project.dsa;

import de.pdbm.janki.core.IntersectionCode;
import de.pdbm.janki.core.RoadPiece;
import de.pdbm.janki.core.Vehicle;
import de.pdbm.janki.core.notifications.IntersectionUpdate;
import de.pdbm.janki.core.notifications.PositionUpdate;
import de.project.core.Car;
import de.project.core.Range;
import de.project.core.StartupCar;
import de.project.core.Track;
import de.project.core.TrackPiece;
import de.project.core.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DSACar extends Car {

    private static final int ACCELERATION = 10000;  // mm/sec^2
    private static final double MIN_SPEED = 300;    // mm/sec
    private static final double MAX_SPEED = 800;    // mm/sec
    private static final int TRACK_LINE_GAP = 9;    // mm, this is the distance between the lines of a TrackPiece

    public DSACar(Vehicle vehicle, TrackPiece currentTrackPiece) {
        super(vehicle, currentTrackPiece);
    }

    @Override
    public void handleTransitionUpdate() {
        //nothing
    }

    @Override
    public void handlePositionUpdate(PositionUpdate positionUpdate) {
        //nothing
    }

    /**
     * Checks if the car is really on the intersection and
     * handles the collision prevention when exiting the intersection
     * @param intersectionUpdate The current intersectionUpdate thrown by the anki car.
     */
    @Override
    public void handleIntersectionUpdate(IntersectionUpdate intersectionUpdate) {
        if (!currentTrackPiece.equals(RoadPiece.INTERSECTION)) {
            logger.severe("current trackpiece was not intersection! " + intersectionUpdate);
            System.exit(1);
        }

        if (!intersectionUpdate.getCode().equals(IntersectionCode.NONE) && intersectionUpdate.isExiting()) {
            handleCollisionPrevention();
        }
    }

    /**
     * Takes care about the collision prevention.
     * Basically all it does, is to regulate this car speeds according to the other cars speed
     * and so the time when the other cars and this car would hit on intersection
     * In order to do so buffers will bet set around the other cars
     */
    private void handleCollisionPrevention() {
        List<Range> forbiddenRanges = new ArrayList<>();

        double offsetThisCar = 0;

        for (Car otherCar : getAllCars()) {

            //check if the other car is relevant to this car
            if (otherCar == this || isDrivingToTheSameIntersectionSide(otherCar)) {
                continue;
            }

            final double speedOther = otherCar.getSpeed();

            //offset when the other car would hit this car on the intersection
            final double offsetOther = (TrackPiece.getCurveTrackLine(lastCurvePositionUpdate.getLocation(),
                    Track.isBeforeSameIntersection(trackPieceOnLastCurvePositionUpdate, currentTrackPiece)) + 1) * TRACK_LINE_GAP;

            final double restLengthToIntersectionOther = otherCar.getRestLengthToIntersectionInMm() + offsetOther;
            final double timeToInterOtherInMs = Utils.secToMs(restLengthToIntersectionOther / speedOther);

            final double threshold = Utils.secToMs(400 / speedOther);

            //create a new range when the other car reaches the intersection (including a buffer around the car)
            forbiddenRanges.add(new Range(timeToInterOtherInMs - threshold, timeToInterOtherInMs + threshold));

            otherCar.mutex.lock();
            //offset when this car would hit the other car on the intersection
            double offset = (TrackPiece.getCurveTrackLine(otherCar.lastCurvePositionUpdate.getLocation(),
                    !Track.isBeforeSameIntersection(otherCar.trackPieceOnLastCurvePositionUpdate, otherCar.currentTrackPiece)) + 1) * TRACK_LINE_GAP;
            otherCar.mutex.unlock();
            offsetThisCar += offset;
        }

        if (forbiddenRanges.size() > 0) {
            final double thisRestToIntersection = getRestLengthToIntersectionInMm() + (offsetThisCar / forbiddenRanges.size());
            generateRandomSpeed(thisRestToIntersection, forbiddenRanges);
        }
    }

    /**
     * Generates a new valid speed for this car to prevent a collision with other cars.
     * Is only be called by the handleCollisionPrevention() method.
     * Basically all it does to calculate the complementary of the time ranges of the other cars.
     * Then a random speed will be selected of this complementary.
     * When no complementary can be calculated, then the speed will be set to 250, to avoid a collision
     * (the minimum speed is 300 so there cannot be a collision happening)
     * @param distanceToIntersectionInMm This car distance to the intersection
     * @param ranges The time ranges of the other cars when they reach the intersection
     */
    private void generateRandomSpeed(double distanceToIntersectionInMm, List<Range> ranges) {

        final double currentSpeed = getSpeed();

        final int limitMaxMs = (int) Utils.secToMs(distanceToIntersectionInMm / MIN_SPEED);
        final int limitMinMs = (int) Utils.secToMs(distanceToIntersectionInMm / MAX_SPEED);

        List<Range> rangesToSelect = createValidTimeRanges(ranges, limitMinMs, limitMaxMs);

        if (rangesToSelect.size() > 0) {
            final Range randomRange = rangesToSelect.get(Utils.getRandomValue(1, rangesToSelect.size()) - 1);
            final int randomMs = Utils.getRandomValue((int) randomRange.getMin(), (int) randomRange.getMax());

            final double newSpeed = distanceToIntersectionInMm / Utils.msToSec(randomMs);

            setSpeed((int) newSpeed, ACCELERATION);

            String log = (getMacAddress() + ": generated final Speed: " + newSpeed +
                    " randomMs:" + randomMs +
                    " distance: " + distanceToIntersectionInMm +
                    " SpeedBefore: " + currentSpeed +
                    " rangesToSelect: " + rangesToSelect +
                    " ranges:" + ranges + " max_ms: " + limitMaxMs + " min_ms: " + limitMinMs);
            logger.fine(log);
            System.out.println(log);
        } else {
            logger.severe(getMacAddress() + ": unable to calculate new speed. Setted speed to 250." +
                    " distance: " + distanceToIntersectionInMm +
                    " currentSpeed: " + currentSpeed +  " rangesToSelect: " + rangesToSelect +
                    " ranges:" + ranges + " max_ms: " + limitMaxMs + " min_ms: " + limitMinMs);
            setSpeed(250, ACCELERATION);
        }
        System.out.println();
    }

    private enum RangesMinMaxKind {
        LIMIT_MIN, LIMIT_MAX, MIN, MAX
    }


    /**
     * Calculates the complementary of the given ranges. Also there is limit_min and limit_max value to use as borders.
     * DSACarTest contains examples.
     * @param ranges The ranges
     * @param LIMIT_MIN_MS The limit min value
     * @param LIMITS_MAX_MS The limit max value
     * @return
     */
    public static List<Range> createValidTimeRanges(List<Range> ranges, final double LIMIT_MIN_MS, final double LIMITS_MAX_MS) {
        Map<Double, RangesMinMaxKind> timesToKind = new HashMap<>();

        timesToKind.put(LIMIT_MIN_MS, RangesMinMaxKind.LIMIT_MIN);
        timesToKind.put(LIMITS_MAX_MS, RangesMinMaxKind.LIMIT_MAX);

        for (Range r: ranges) {
            final double min = r.getMin();
            final double max = r.getMax();
            timesToKind.put(min, RangesMinMaxKind.MIN);
            timesToKind.put(max, RangesMinMaxKind.MAX);
        }

        List<Double> times = new ArrayList<>(timesToKind.keySet());
        Collections.sort(times);

        List<Range> resultRanges = new ArrayList<>();
        int i = 0;
        RangesMinMaxKind timeToKind = timesToKind.get(times.get(0));
        int openConnections = 0;
        while (i < times.size() && !timeToKind.equals(RangesMinMaxKind.LIMIT_MAX)) {
            switch (timeToKind) {
                case MIN:
                    openConnections++;
                    break;
                case MAX:
                    openConnections--;
                    break;
            }
            if (openConnections == 0 && times.get(i) >= LIMIT_MIN_MS) {
                resultRanges.add(new Range(times.get(i), times.get(i + 1)));
            }
            i++;
            timeToKind = timesToKind.get(times.get(i));
        }

        return resultRanges;
    }

    public static void main(String[] args) {
        Track track = Track.getInstance();
        System.out.println(track);

        Vehicle.getVehicles().forEach(StartupCar::new);

        System.out.println("In 4 seconds the prevention will start");
        Utils.sleep(4000);

        for (int i = 0; i < Vehicle.getVehicles().size(); i++) {
            Vehicle.getVehicles().get(i).setSpeed(400 + i * 100, ACCELERATION);
        }

        //check if cars have the reached the intersection
        while(!StartupCar.getStartupCars().stream().allMatch(StartupCar::isReady)) {
            Utils.sleep(1);
        }

        //remove the start up car from the listener and start the real prevention
        StartupCar.getStartupCars().forEach(startupCar -> {
            TrackPiece currentTrackPiece = track.getTrackPieces().get(startupCar.getNumSeenTransitionsUpdatesAfterReady());
            new DSACar(startupCar.getVehicle(), currentTrackPiece);
            startupCar.getVehicle().removeNotificationListener(startupCar);
            System.out.println(startupCar.getVehicle().getMacAddress() + " to: " + currentTrackPiece);
        });

        System.out.println("Start!");

        while (true) {
        }
    }
}
