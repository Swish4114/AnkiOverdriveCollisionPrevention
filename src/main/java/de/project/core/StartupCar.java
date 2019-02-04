package de.project.core;

import de.pdbm.janki.core.IntersectionCode;
import de.pdbm.janki.core.Vehicle;
import de.pdbm.janki.core.notifications.IntersectionUpdate;
import de.pdbm.janki.core.notifications.IntersectionUpdateListener;
import de.pdbm.janki.core.notifications.TransitionUpdate;
import de.pdbm.janki.core.notifications.TransitionUpdateListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class that represents logic to handle the start up phase of an anki overdrive car.
 * This class property isReady() is false until the anki car exits the intersection for the first time (then true)
 * Thats basically all this class does
 */
public class StartupCar implements IntersectionUpdateListener, TransitionUpdateListener {

    /**
     * @return all start up cars
     */
    public static ArrayList<StartupCar> getStartupCars() {
        return startupCars;
    }

    private static ArrayList<StartupCar> startupCars = new ArrayList<>();

    private AtomicBoolean isReady = new AtomicBoolean();
    private AtomicInteger numSeenTransitionsUpdatesAfterReady = new AtomicInteger();

    public Vehicle getVehicle() {
        return vehicle;
    }

    private Vehicle vehicle;

    /**
     * Method to get number of transitionUpdates this car has triggerd.
     * It used to know on which trackPiece the car is on.
     * @return The number of triggered/seen transitionUpdates.
     */
    public int getNumSeenTransitionsUpdatesAfterReady() {
        return numSeenTransitionsUpdatesAfterReady.get();
    }

    /**
     * @return Returns true if the car has exited the intersection at least once, else false
     */
    public boolean isReady() {
        return isReady.get();
    }

    public StartupCar(Vehicle vehicle) {
        this.vehicle = vehicle;
        startupCars.add(this);
        vehicle.addNotificationListener(this);
    }

    /**
     * Method that handles the intersection update and checks if the car exists the intersection.
     * If yes, the car is ready
     * @param intersectionUpdate
     */
    @Override
    public void onIntersectionUpdate(IntersectionUpdate intersectionUpdate) {
        System.out.println(vehicle.getMacAddress() + ": " + intersectionUpdate);
        if (!intersectionUpdate.getCode().equals(IntersectionCode.NONE) && intersectionUpdate.isExiting()) {
            isReady.set(true);
        }
    }

    /**
     * Method to handle the transitionUpdate.
     * Increases the seen transitionUpdates counter.
     * @param transitionUpdate
     */
    @Override
    public void onTransitionUpdate(TransitionUpdate transitionUpdate) {
        if (isReady()) {
            numSeenTransitionsUpdatesAfterReady.incrementAndGet();
        }
    }
}
