package erwin.shared.utils;

import erwin.shared.Complex;
import erwin.shared.Const;
import erwin.shared.enums.Operator;


public final class WaveUtil {
    private WaveUtil() { }

    public static Complex calculateWave(final double x, final double y, final long t,
            final double waveNumber, final Operator op) {
        final double phase =
                (Math.pow(Const.H_BAR, 2D) * Math.pow(Math.PI, 2D)
                / (2D * Const.MASS) * Math.pow(waveNumber, 2D))
                * (Math.pow(x, 2D) + Math.pow(y, 2D));
        if (Operator.ADD.equals(op)) {
            return Complex.fromMagAndPhase(1D, (Const.HALF * phase) - t);
        } else {
            return Complex.fromMagAndPhase(1D, (Const.HALF * phase) * t);
        }
    }
    
    public static Complex calculateDual(final int x, final int y, final int centerX, final int centerY, 
            final long t, final double waveNumber) {
        final double distanceToCenter = Math.sqrt(Math.pow(x - centerX, 2D) + Math.pow(y - centerY, 2D));
        final double phase = (distanceToCenter - t) * waveNumber;
        return Complex.fromMagAndPhase(1D, phase);
    }
}
