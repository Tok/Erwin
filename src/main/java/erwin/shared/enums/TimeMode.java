package erwin.shared.enums;

public enum TimeMode {
    RELATIVE, ABSOLUTE;

    public static final String GROUP_NAME = "time mode";

    public final String toString() {
        return name();
    }
}
