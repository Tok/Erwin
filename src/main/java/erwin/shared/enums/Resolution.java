package erwin.shared.enums;

public enum Resolution {
    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6);

    public static final String GROUP_NAME = "resolution";

    private final int resolution;

    private Resolution(final int resolution) {
        this.resolution = resolution;
    }

    public static final int getDefault() { return THREE.getResolution(); }

    public static final Resolution valueOf(final int res) {
        for (final Resolution v : values()) {
            if (res == v.getResolution()) {
                return v;
            }
        }
        throw new IllegalArgumentException("Resolution unknown: " + res);
    }

    public final int getResolution() {
        return resolution;
    }

    public final String toString() {
        return name().substring(0, 1) + name().substring(1, name().length()).toLowerCase();
    }
}
