package erwin.shared.enums;

public enum Operator {
    MULTIPLY, ADD;
    
    public static final String OPERATOR_GROUP = "operator";
    
    public final String toString() {
        return name();
    }
}
