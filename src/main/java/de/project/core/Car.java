package de.project.core;

import de.pdbm.janki.core.RoadPiece;
import de.pdbm.janki.core.Vehicle;
import de.pdbm.janki.core.notifications.IntersectionUpdate;
import de.pdbm.janki.core.notifications.IntersectionUpdateListener;
import de.pdbm.janki.core.notifications.PositionUpdate;
import de.pdbm.janki.core.notifications.PositionUpdateListener;
import de.pdbm.janki.core.notifications.TransitionUpdate;
import de.pdbm.janki.core.notifications.TransitionUpdateListener;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class Car is a base class that represents an Anki Overdrive.
 * It handles the events thrown by anki. This class is thread safe.
 */
public abstract class Car implements PositionUpdateListener, TransitionUpdateListener, IntersectionUpdateListener {

    private static ArrayList<Car> allCars = new ArrayList<>();

    public static ArrayList<Car> getAllCars() {
        return allCars;
    }

    public final Lock mutex = new ReentrantLock();

    protected Logger logger;

    private Vehicle vehicle;
    private Track track;

    public TrackPiece currentTrackPiece;
    public PositionUpdate currentPositionUpdate;

    //timestamp when the car leaves the intersection (so it enters the next curve)
    public double intersectionExitingTimeStampSec;

    //last curve positionUpdate (so not an intersection positionUpdate)
    public PositionUpdate lastCurvePositionUpdate;
    //the trackPiece on that the lastCurvePositionUpdate occurred
    public TrackPiece trackPieceOnLastCurvePositionUpdate;


    public Car(Vehicle vehicle, TrackPiece currentTrackPiece) {
        this.vehicle = vehicle;
        allCars.add(this);

        track = Track.getInstance();

        logger = Logger.getLogger(vehicle.getMacAddress());
        Utils.addFileHandler(logger, vehicle.getMacAddress() + ".log");
        logger.setLevel(Level.ALL);
        logger.finest(vehicle.toString());

        this.currentTrackPiece = currentTrackPiece;

        vehicle.addNotificationListener(this);
    }


    /**
     * Handles the positionUpdateEvent.
     * Calls the abstract method handlePositionUpdate after it updated all internal properties.
     * @param positionUpdate The current positionUpdate thrown by the anki car
     */
    @Override
    public void onPositionUpdate(PositionUpdate positionUpdate) {
        mutex.lock();
        currentPositionUpdate = positionUpdate;
        if (positionUpdate.getRoadPiece().equals(RoadPiece.CORNER)) {
            lastCurvePositionUpdate = positionUpdate;
            trackPieceOnLastCurvePositionUpdate = currentTrackPiece;
        }
        handlePositionUpdate(positionUpdate);
        mutex.unlock();
        logger.finest(positionUpdate.toString());
    }

    /**
     * Abstract method to handle the positionUpdate
     * @param positionUpdate The current positionUpdate thrown by the anki car
     */
    public abstract void handlePositionUpdate(PositionUpdate positionUpdate);


    /**
     * Handles the transitionUpdateEvent.
     * It updates the currentTrackPiece the car is driving on.
     * @param transitionUpdate The current transitionUpdate,
     *                        but it is ignored (due to the fact that it has no information)
     */
    @Override
    public void onTransitionUpdate(TransitionUpdate transitionUpdate) {
        mutex.lock();

        if (currentTrackPiece.equals(RoadPiece.INTERSECTION)) {
            intersectionExitingTimeStampSec = Utils.msToSec(System.currentTimeMillis());
        }

        currentTrackPiece = track.getNextTrackPiece(currentTrackPiece);

        handleTransitionUpdate();
        mutex.unlock();
        logger.finest( "Current-TrackPiece:" + currentTrackPiece);
    }

    /**
     * Abstract method to handle the transitionUpdate
     */
    public abstract void handleTransitionUpdate();

    /**
     * Handles the intersectionUpdate.
     * It just calls the abstract method handleIntersectionUpdate.
     * @param intersectionUpdate The current interectionUpdate thrown by the anki car.
     */
    @Override
    public void onIntersectionUpdate(IntersectionUpdate intersectionUpdate) {
        mutex.lock();
        handleIntersectionUpdate(intersectionUpdate);
        mutex.unlock();

        logger.finest(intersectionUpdate.toString());
    }

    /**
     * Abstract method to handle current intersectionUpdateEvent
     * @param intersectionUpdate The current intersectionUpdate thrown by the anki car.
     */
    public abstract void handleIntersectionUpdate(IntersectionUpdate intersectionUpdate);

    /**
     * Method that calculates the rest length to the intersection Entry.
     * If the car is currently on the intersection, this method just returns the length of the three Curves
     * to the next trackPiece.
     * @return The rest length to intersection (until the real intersection point)
     */
    public double getRestLengthToIntersectionInMm() {
        mutex.lock();

        final double currentTimeSec = Utils.msToSec(System.currentTimeMillis());
        final double alreadyDrivenLength = ((currentTimeSec - intersectionExitingTimeStampSec) * (double) getSpeed());

        //190 is the length of the intersection entry
        final double halfEightLength = TrackPiece.getCurveLength(lastCurvePositionUpdate,
                        !Track.isBeforeSameIntersection(trackPieceOnLastCurvePositionUpdate, currentTrackPiece)) * 3.0 + 190;

        if (currentTrackPiece.equals(RoadPiece.INTERSECTION)) {
            mutex.unlock();
            return halfEightLength;
        }
        mutex.unlock();
        return halfEightLength - alreadyDrivenLength;
    }

    /**
     * Checks whether the this and an other car is driving is driving to the same intersection entry.
     * @param other The other car.
     * @return True, if the other car is driving to same intersection entry.
     */
    public boolean isDrivingToTheSameIntersectionSide(Car other) {
        mutex.lock();
        other.mutex.lock();
        try {
            return Track.isBeforeSameIntersection(other.currentTrackPiece, this.currentTrackPiece);
        } finally {
            other.mutex.unlock();
            mutex.unlock();
        }
    }

    /**
     * Checks if the car is on the intersection.
     * @return True, if the car is on the intersection.
     */
    public boolean isOnIntersection() {
        mutex.lock();
        try {
            return currentTrackPiece.equals(RoadPiece.INTERSECTION);
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Checks if the car is on last curve before the intersection, also the last positionUpdate for this curve
     * must already be thrown
     * @return True, if the car is before the intersection
     */
    public boolean isBeforeIntersection() {
        mutex.lock();
        try {
            return track.getNextTrackPiece(currentTrackPiece).equals(RoadPiece.INTERSECTION) &&
                    TrackPiece.getLocationPositionOnCurveLine(currentPositionUpdate).equals(TrackPiece.LocationPosition.LAST);
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Checks if the car is near the intersection
     * @return True, if the car is on the intersection or before the intersection
     */
    public boolean isNearIntersection() {
        return isOnIntersection() || isBeforeIntersection();
    }

    /**
     * Sets the speed of the car
     * @param speed The speed
     */
    public void setSpeed(int speed) {
        mutex.lock();
        vehicle.setSpeed(speed);
        mutex.unlock();
        logger.finest("setted speed: " + speed);
    }

    /**
     * Sets the speed of the car
     * @param speed The speed of the car
     * @param acceleration The acceleration of the car
     */
    public void setSpeed(int speed, int acceleration) {
        mutex.lock();
        vehicle.setSpeed(speed, acceleration);
        mutex.unlock();
        logger.finest("setted speed: " + speed);
    }

    /**
     * Returns the current speed.
     * @return The current speed.
     */
    public int getSpeed() {
        mutex.lock();
        try {
            return vehicle.getSpeed();
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Returns the mac address of the car
     * @return The mac address
     */
    public String getMacAddress() {
        return vehicle.getMacAddress();
    }

    /**
     * Returns the janki vehicle object.
     * @return The janki vehicle object
     */
    public Vehicle getVehicle() {
        return vehicle;
    }
}
