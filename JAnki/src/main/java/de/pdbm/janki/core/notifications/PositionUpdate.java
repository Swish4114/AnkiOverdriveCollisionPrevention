package de.pdbm.janki.core.notifications;

import de.pdbm.janki.core.RoadPiece;
import de.pdbm.janki.core.Vehicle;

/**
 * Represents information about a position update of a vehicle.
 * 
 * @author bernd
 *
 */
public final class PositionUpdate extends Notification {
	
	private final int location;
	private final int roadPieceId;
	private final RoadPiece roadPiece;
	private final boolean ascendingLocations;
	private final int speed;
	
	public PositionUpdate(Vehicle vehicle, int location, RoadPiece roadPiece, int roadPieceId, boolean ascendingLocations,
						  int speed) {
		super(vehicle);
		this.location = location;
		this.roadPiece = roadPiece;
		this.ascendingLocations = ascendingLocations;
		this.roadPieceId = roadPieceId;
		this.speed = speed;
	}
	
	@Override
	public String toString() {
		return "PositionUpdate(" + String.format("%1$2s", "" + location) + ", " + roadPiece + ", " +
				ascendingLocations + ", " + roadPieceId+ ", " + speed + ")";
	}
	

	/**
	 * Returns the location ID.
	 * 
	 * @return location ID
	 * 
	 */
	public int getLocation() {
		return location;
	}

	/**
	 * Returns the road piece ID
	 * @return the road piece ID
	 */
	public int getRoadPieceId() {
		return roadPieceId;
	}

	/**
	 * Returns the road piece.
	 * 
	 * @return road piede
	 */
	public RoadPiece getRoadPiece() {
		return roadPiece;
	}

	/**
	 * Returns true, if and only if, location IDs are passed in ascending order. 
	 * 
	 * @return true, if location IDs are passed in ascending order, false otherwise
	 * 
	 */
	public boolean isAscendingLocations() {
		return ascendingLocations;
	}

	public int getSpeed() {
		return speed;
	}
	
}
