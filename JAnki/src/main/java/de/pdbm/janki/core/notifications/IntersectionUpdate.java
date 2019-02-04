package de.pdbm.janki.core.notifications;

import de.pdbm.janki.core.IntersectionCode;
import de.pdbm.janki.core.Vehicle;

import java.util.Objects;

/**
 * Represents information about an intersection update
 */
public class IntersectionUpdate extends Notification{
    private IntersectionCode code;
    private boolean isExiting;

    public IntersectionUpdate(Vehicle vehicle, IntersectionCode code, boolean isExiting) {
        super(vehicle);
        this.code = code;
        this.isExiting = isExiting;
    }

    public IntersectionCode getCode() {
        return code;
    }

    public boolean isExiting() {
        return isExiting;
    }

    @Override
    public String toString() {
        return "IntersectionUpdate{" +
                "code=" + code +
                ", isExiting=" + isExiting +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntersectionUpdate)) return false;
        IntersectionUpdate that = (IntersectionUpdate) o;
        return isExiting == that.isExiting &&
                code == that.code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, isExiting);
    }
}
