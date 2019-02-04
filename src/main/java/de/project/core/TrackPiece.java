package de.project.core;

import de.pdbm.janki.core.RoadPiece;
import de.pdbm.janki.core.notifications.PositionUpdate;

import java.util.Objects;

/**
 * Class that represents a single trackPiece.
 */
public class TrackPiece {
    private RoadPiece roadPiece; //the roadPiece of the track (so what kind is the trackPiece)
    private int uniqueId; //an unique id to make the track pieces unique.

    private static int uniqueIdCounter = 0;

    public TrackPiece(RoadPiece roadPiece) {
        this.roadPiece = roadPiece;
        this.uniqueId = uniqueIdCounter++;
    }

    public TrackPiece(RoadPiece roadPiece, int uniqueId) {
        this.roadPiece = roadPiece;
        this.uniqueId = uniqueId;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public RoadPiece getRoadPiece() {
        return roadPiece;
    }

    public boolean equals(RoadPiece rp) {
        return roadPiece.equals(rp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrackPiece)) return false;
        TrackPiece that = (TrackPiece) o;
        return roadPiece == that.roadPiece &&
                uniqueId == that.uniqueId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roadPiece);
    }

    @Override
    public String toString() {
        return "TrackPiece{" +
                "roadPiece=" + roadPiece +
                ", uniqueId=" + uniqueId +
                '}';
    }


    /**
     * Function that tells on which line of the track piece the car is driving on based of
     * a gives locationId that comes along of a position update event.
     * Due to the fact that an anki overdrive track piece has 16 lines the result will be between
     * 0 and 15 (0 is most inner line, 15 the most outter line)
     * @param locationId The locationId (comes from a position update event)
     * @param mirrorLocationId Boolean that tells if the result track line should be mirrored (so 15 - line)
     * @return The line the car is driving on (so the line that has locationID coded)
     */
    public static int getCurveTrackLine(int locationId, boolean mirrorLocationId) {
        //returns 0 - 15
        int result;
        if (locationId <= 19) {
            result = locationId / 2;
        } else {
            result = 10 + ((locationId - 20) / 3);
        }
        if (mirrorLocationId)
            return 15 - result;
        return result;
    }

    /**
     * Enum to represent a locationID position on a track piece
     */
    public enum LocationPosition {
        FIRST, MIDDLE, LAST;
    }

    /**
     * Function that tells the position on a track piece on which a position update occured.
     * For example a position update with location_id 22 (which is on line 10 with location update id's [20, 21, 22])
     * will return last (if ascending=true else it will return first)
     * @param pu The position update
     * @return The location position
     */
    public static LocationPosition getLocationPositionOnCurveLine(PositionUpdate pu) {
        final int numLocationIdsOnLine = (pu.getLocation() <= 19) ? 2 : 3;
        final int offset = (pu.getLocation() <= 19) ? 0 : 20;

        int pos;
        if (pu.isAscendingLocations()) {
            pos = Math.floorMod(pu.getLocation() - offset, numLocationIdsOnLine);
        } else {
            pos = Math.floorMod(-(pu.getLocation() + 1 - offset), numLocationIdsOnLine);
        }

        if (pu.getLocation() <= 19) {
            switch (pos) {
                case 0:
                    return LocationPosition.FIRST;
                case 1:
                    return LocationPosition.LAST;
            }
            throw new RuntimeException("unexpected position: " + pos);
        } else {
            switch (pos) {
                case 0:
                    return LocationPosition.FIRST;
                case 1:
                    return LocationPosition.MIDDLE;
                case 2:
                    return LocationPosition.LAST;
            }
            throw new RuntimeException("unexpected position: " + pos);
        }
    }

    /**
     * Returns the length of a curve line, depending on a given position update.
     * It's basically the same as getCurveTrackLine(), but just maps the line to curve length.
     * The resulting length are calculated with the circle circumference formula,
     * due to the fact, that a curve is a quarter of a circle (in cm): pi * 0.5 * (21.2 + i * 0.9), i is the current line (0 - 15)
     * @param pu The given position update
     * @param mirrorLocationId If the positionUpdate (so the resulting line) should be mirrored.
     * @return The length of a curve depending on the position update
     */
    public static double getCurveLength(PositionUpdate pu, boolean mirrorLocationId) {
        int trackLine = getCurveTrackLine(pu.getLocation(), mirrorLocationId);
        switch (trackLine) {
            case 0:
                return 333.0;
            case 1:
                return 347.1;
            case 2:
                return 361.3;
            case 3:
                return 375.4;
            case 4:
                return 389.6;
            case 5:
                return 403.7;
            case 6:
                return 417.8;
            case 7:
                return 432.0;
            case 8:
                return 446.1;
            case 9:
                return 460.2;
            case 10:
                return 474.4;
            case 11:
                return 488.5;
            case 12:
                return 502.7;
            case 13:
                return 516.8;
            case 14:
                return 530.9;
            case 15:
                return 545.1;
        }
        throw new RuntimeException("unknown id");
    }
}
