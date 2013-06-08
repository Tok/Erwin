package erwin.shared.utils;

import erwin.shared.Complex;
import erwin.shared.Const;


public final class ColorUtil {
    private ColorUtil() { }

    public static String getColor(final Complex c) {
        return getColorFromMagnitudeAndPhase(c.getMagnitude(), c.getPhase());
    }

    public static String getColorFromMagnitudeAndPhase(final double magnitude, final double phase) {
        if (magnitude <= 1D / Const.MAX_RGB) { return Const.BLACK; }
        final double mag = Math.min(1D, magnitude);
        final double pha = (phase < 0D) ? phase + Const.TAU : phase;
        final double p = (pha * 6D) / Const.TAU;
        final int range = Double.valueOf(Math.min(5, Math.max(0D, p))).intValue();
        final double fraction = p - range;
        double r = 0D, g = 0D, b = 0D;
        switch(range) {
          case 0: r = 1D; g = fraction; b = 0D; break; //Red -> Yellow
          case 1: r = 1D - fraction; g = 1D; b = 0D; break; //Yellow -> Green
          case 2: r = 0D; g = 1D; b = fraction; break; //Green -> Cyan
          case 3: r = 0D; g = 1D - fraction; b = 1D; break; //Cyan -> Blue
          case 4: r = fraction; g = 0D; b = 1D; break; //Blue -> Magenta
          case 5: r = 1D; g = 0D; b = 1D - fraction; break; //Magenta -> Red
          default: throw new IllegalArgumentException("Out of range: " + range);
        }
        final int red = Double.valueOf(r * mag * Const.MAX_RGB).intValue();
        final int green = Double.valueOf(g * mag * Const.MAX_RGB).intValue();
        final int blue = Double.valueOf(b * mag * Const.MAX_RGB).intValue();
        return "rgb(" + red + "," + green + "," + blue + ")";
    }

}
