package de.pdbm.janki.core;

public enum IntersectionCode {
    NONE, ENTRY_FIRST, EXIT_FIRST, ENTRY_SECOND, EXIT_SECOND;

    public static IntersectionCode valueOf(int id) {
        switch (id) {
            case 0:
                return NONE;
            case 1:
                return ENTRY_FIRST;
            case 2:
                return EXIT_FIRST;
            case 3:
                return ENTRY_SECOND;
            case 4:
                return EXIT_SECOND;
        }
        throw new IllegalArgumentException("unknown intersection code id: " + id);
    }
}
