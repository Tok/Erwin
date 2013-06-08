package erwin.shared.utils;

import erwin.shared.Complex;
import erwin.shared.Const;


public final class WaveUtil {
    private WaveUtil() { }

    public static Complex calculateWave(final double x, final double y, final double t,
            final double wavelength) {
        final double phase =
                (Math.pow(Const.H_BAR, 2D) * Math.pow(Math.PI, 2D)
                / (2D * Const.MASS) * Math.pow(wavelength, 2D))
                * (Math.pow(x, 2D) + Math.pow(y, 2D));
        return Complex.fromMagAndPhase(1D, (Const.HALF * phase) - t);
        //return Complex.fromMagAndPhase(1D, (Const.HALF * phase) * t);
    }
}
