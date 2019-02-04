package de.project.stopandgo;

import de.pdbm.janki.core.IntersectionCode;
import de.pdbm.janki.core.RoadPiece;
import de.pdbm.janki.core.Vehicle;
import de.pdbm.janki.core.notifications.IntersectionUpdate;
import de.pdbm.janki.core.notifications.PositionUpdate;
import de.project.core.Car;
import de.project.core.StartupCar;
import de.project.core.Track;
import de.project.core.TrackPiece;
import de.project.core.Utils;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;
import java.util.stream.Stream;


public class StopAndGoCar extends Car{

    private static Logger logger = Utils.MAIN_LOGGER;
    private static Deque<Car> queue = new ConcurrentLinkedDeque<>(); //queue to register the stopped cars

    public StopAndGoCar(Vehicle vehicle, TrackPiece currentTrackPiece) {
        super(vehicle, currentTrackPiece);
    }

    /**
     * Due to the fact, that when a anki car stops on the intersection and restarts again on the intersection,
     * wrong transition udpate events will be send,
     * we have to patch the currentTrackPiece to the real currentTrackPiece again.
     * @param intersectionUpdate The current intersectionUpdate thrown by the anki car.
     */
    @Override
    public void handleIntersectionUpdate(IntersectionUpdate intersectionUpdate) {
        if (!intersectionUpdate.getCode().equals(IntersectionCode.NONE)) {
            if (currentTrackPiece.equals(RoadPiece.CORNER)) {
                do {
                    currentTrackPiece = Track.getInstance().getTrackPieceBefore(currentTrackPiece);
                } while (!currentTrackPiece.equals(RoadPiece.INTERSECTION));
            }
        }
    }

    @Override
    public void handlePositionUpdate(PositionUpdate positionUpdate) {
        //nothing
    }

    /**
     * Checks whether the intersection is alredy occupied, if yes this car will be stopped.
     */
    @Override
    public void handleTransitionUpdate() {
        if (isOnIntersection()) {
            if (getRelevantOtherCars(this).anyMatch(Car::isNearIntersection)) {
                queue.add(this);
                setSpeed(0);
            }
        }
    }

    /**
     * Static method that checks if the lastCurvePositionUpdate of two cars are same ascending.
     * So basically if the two cars are driving in the same direction.
     * @param car Car one
     * @param otherCar Car two
     * @return True if the lastCurvePositionUpdate have the same isAscendingLocations value
     */
    private static boolean areLastCurePositionUpdatesBeforeSameIntersection(Car car, Car otherCar) {
        car.mutex.lock();
        otherCar.mutex.lock();
        try {
            return car.lastCurvePositionUpdate.isAscendingLocations() !=
                    otherCar.lastCurvePositionUpdate.isAscendingLocations();
        } finally {
            otherCar.mutex.unlock();
            car.mutex.unlock();
        }
    }

    /**
     * Static method that returns the relevant other cars of a given car
     * @param car The car
     * @return The relevant other cars.
     */
    private static Stream<Car> getRelevantOtherCars(Car car) {
        return Car.getAllCars().stream().
                filter(otherCar -> otherCar != car). // different car
                filter(otherCar -> areLastCurePositionUpdatesBeforeSameIntersection(car, otherCar)). // driving to the same direction
                filter(otherCar -> !queue.contains(otherCar)); // the car must not be already stopped.
    }

    public static void main(String[] args) {
        Map<Vehicle, Integer> vehicleToSpeed= new HashMap<>();

        Track track = Track.getInstance();
        System.out.println(track);

        Vehicle.getVehicles().forEach(StartupCar::new);

        System.out.println("In 4 seconds the prevention will start");
        Utils.sleep(4000);

        for (int i = 0; i < Vehicle.getVehicles().size(); i++) {
            final int speed = 400 + i * 50;
            Vehicle vehicle = Vehicle.getVehicles().get(i);
            vehicle.setSpeed(speed);
            vehicleToSpeed.put(vehicle, speed);
        }

        //check if cars have the reached the intersection
        while(!StartupCar.getStartupCars().stream().allMatch(StartupCar::isReady)) {
            Utils.sleep(1);
        }

        //remove the start up car from the listener and start the real prevention
        StartupCar.getStartupCars().forEach(startupCar -> {
            TrackPiece currentTrackPiece = track.getTrackPieces().get(startupCar.getNumSeenTransitionsUpdatesAfterReady());
            new StopAndGoCar(startupCar.getVehicle(), currentTrackPiece);
            startupCar.getVehicle().removeNotificationListener(startupCar);
            System.out.println(startupCar.getVehicle().getMacAddress() + " to: " + currentTrackPiece);
        });

        System.out.println("Start!");

        //Checks the queue and restarts a stopped car again, if the intersection is not occupied anymore
        //so no other car near the intersection anymore
        while (true) {
            if (queue.size() > 0) {
                Car car = queue.getFirst();
                if (getRelevantOtherCars(car).noneMatch(Car::isNearIntersection)) {
                    queue.removeFirst();
                    car.setSpeed(vehicleToSpeed.get(car.getVehicle()));
                }
            }
            Utils.sleep(10);
        }
    }

}
