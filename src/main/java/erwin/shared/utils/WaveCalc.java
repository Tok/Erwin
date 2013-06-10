package erwin.shared.utils;

import erwin.shared.Complex;
import erwin.shared.Const;
import erwin.shared.enums.Operator;


public final class WaveCalc {
    private static final double MANDALA_DIVISOR = 100D;
    private final int centerX;
    private final int centerY;

    public WaveCalc(final int x0, final int y0) {
        centerX = x0;
        centerY = y0;
    }

    private double calculateDistanceToCenter(final int x, final int y, final int cX, final int cY) {
        return Math.sqrt(Math.pow(x - cX, 2D) + Math.pow(y - cY, 2D));
    }

    private double calculateDistanceToCenter(final int x, final int y) {
        return calculateDistanceToCenter(x, y, centerX, centerY);
    }

    public Complex calculateWave(final double x, final double y, final long t,
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

    public Complex calculateDual(final int x, final int y, final int centerX, final int centerY, final long t, final double waveNumber) {
        final double distanceToCenter = calculateDistanceToCenter(x, y, centerX, centerY);
        final double phase = (distanceToCenter - t) * waveNumber;
        return Complex.fromMagAndPhase(1D, phase);
    }

    public Complex calculateMandala(final int x, final int y,
            final long t, final double waveNumber, final Operator op, final Complex old) {
        final double distanceToCenter = calculateDistanceToCenter(x, y);
        final double oldPhase = old.getPhase() != 0D ? old.getPhase() : 1D;
        final double ph = (distanceToCenter - (Double.valueOf(t) / MANDALA_DIVISOR)) * waveNumber;
        final double phase = op.equals(Operator.ADD) ? oldPhase + ph : oldPhase * ph;
        return Complex.fromMagAndPhase(1D, phase);
    }
}
