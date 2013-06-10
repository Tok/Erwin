package erwin.shared.enums;

public enum Resolution {
    ONE(1, false), TWO(2, false), THREE(3, true), FOUR(4, false), FIVE(5, false);

    public static final String GROUP_NAME = "resolution";

    private final int resolution;
    private final boolean isDefault;

    private Resolution(final int resolution, final boolean isDefault) {
        this.resolution = resolution;
        this.isDefault = isDefault;
    }

    public static final int getDefault() {
        for (final Resolution v : values()) {
            if (v.isDefault()) {
                return v.getResolution();
            }
        }
        throw new IllegalArgumentException("Resolution has no default.");
    }

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

    public final boolean isDefault() {
        return isDefault;
    }

    public final String toString() {
        return name().substring(0, 1) + name().substring(1, name().length()).toLowerCase();
    }
}
