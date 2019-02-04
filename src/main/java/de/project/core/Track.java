package de.project.core;

import de.pdbm.janki.core.RoadPiece;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class that represents the Anki Overdrive Track. The form of the track is a eight "8"
 * It is a singleton.
 */
public class Track {

    private static Track instance;

    public static Track getInstance() {
        if (instance == null) {
            instance = new Track();
        }
        return instance;
    }

    private Logger logger = Utils.MAIN_LOGGER;
    private List<TrackPiece> trackPieces = new ArrayList<>();

    /**
     * Returns the next trackPiece of a given trackPiece
     * So the trakcPiece that comes after the given trackPiece
     * @param tp The given trackPiece
     * @return The next trackPiece
     */
    public TrackPiece getNextTrackPiece(TrackPiece tp) {
        int index = tp.getUniqueId();
        index++;
        if (index == trackPieces.size()) {
            //We skip the first intersection, because this would be the intersection again
            index = 1;
        }
        return trackPieces.get(index);
    }

    /**
     * Returns the trackPiece that was before of a given trackPiece
     * @param tp The given trackPiece
     * @return The trackPiece before
     */
    public TrackPiece getTrackPieceBefore(TrackPiece tp) {
        int index = tp.getUniqueId();
        index--;
        if (index == 0) {
            index = 8;
        }
        return trackPieces.get(index);
    }

    /**
     * Static function to check whether two trackPieces are before the same intersection entry.
     * @param t1 The first trackPiece
     * @param t2 The second trackPiece
     * @return True, if both trackPieces are before the same intersection entry.
     */
    public static boolean isBeforeSameIntersection(TrackPiece t1, TrackPiece t2) {
        int interT1 = ((t1.getUniqueId() / 4) + 1) % 2;
        int interT2 = ((t2.getUniqueId() / 4) + 1) % 2;
        return interT1 == interT2;
    }

    /**
     * Returns all trackPieces of the track
     * @return The trackPieces
     */
    public List<TrackPiece> getTrackPieces() {
        return trackPieces;
    }

    /**
     * Helper methos to convert a RoadPiece to a trackPiece and to add it to the track.
     * @param rp
     */
    private void addTrackPiece(RoadPiece rp) {
        trackPieces.add(new TrackPiece(rp));
    }

    /**
     * Constructor that builds the track.
     */
    private Track() {
        /*
        The track is build as a 8. So with drive through the intersection twice.
        The track is build with three intersections, which seems not correct at the beginning.
        The first intersection is only at the startup phase used and will during the runtime
        always be skipped (so only two intersections are used basically during runtime)
         */
        addTrackPiece(RoadPiece.INTERSECTION);
        addTrackPiece(RoadPiece.CORNER);
        addTrackPiece(RoadPiece.CORNER);
        addTrackPiece(RoadPiece.CORNER);
        addTrackPiece(RoadPiece.INTERSECTION);
        addTrackPiece(RoadPiece.CORNER);
        addTrackPiece(RoadPiece.CORNER);
        addTrackPiece(RoadPiece.CORNER);
        addTrackPiece(RoadPiece.INTERSECTION);
        logger.fine("Track:\n" +
                trackPieces.stream().map(TrackPiece::toString).collect(Collectors.joining("\n")));
    }

    @Override
    public String toString() {
        return "Track{" +
                "trackPieces=" + trackPieces +
                '}';
    }
}
