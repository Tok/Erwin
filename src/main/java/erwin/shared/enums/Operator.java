package erwin.shared.enums;

public enum Operator {
    MULTIPLY, ADD;

    public static final String GROUP_NAME = "operator";

    public final String toString() {
        return name().substring(0, 1) + name().substring(1, name().length()).toLowerCase();
    }
}
