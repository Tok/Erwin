package erwin.shared.enums;

public enum Operator {
    MULTIPLY, ADD;

    public static final String GROUP_NAME = "operator";

    public final String toString() {
        return name();
    }
}
