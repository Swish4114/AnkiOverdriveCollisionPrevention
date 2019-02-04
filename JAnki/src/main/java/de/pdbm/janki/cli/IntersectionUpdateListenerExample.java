package de.pdbm.janki.cli;

import de.pdbm.janki.core.notifications.IntersectionUpdate;
import de.pdbm.janki.core.notifications.IntersectionUpdateListener;

public class IntersectionUpdateListenerExample implements IntersectionUpdateListener {
    @Override
    public void onIntersectionUpdate(IntersectionUpdate intersectionUpdate) {
        System.out.println(intersectionUpdate);
    }
}
