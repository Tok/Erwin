package erwin.shared.enums;

public enum Resolution {
    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6);

    public static final String GROUP_NAME = "resolution";

    private final int pixels;

    private Resolution(final int pixels) {
        this.pixels = pixels;
    }

    public final int getPixels() {
        return pixels;
    }

    public static final int getDefault() { return THREE.getPixels(); }

    public static final Resolution valueOf(final int pixels) {
        for (final Resolution v : values()) {
            if (pixels == v.getPixels()) {
                return v;
            }
        }
        throw new IllegalArgumentException("No resolution with " + pixels + " pixels defined.");
    }

    public final String toString() {
        return name().substring(0, 1) + name().substring(1, name().length()).toLowerCase();
    }
}
