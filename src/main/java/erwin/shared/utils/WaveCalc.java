package erwin.shared.utils;

import erwin.shared.Complex;
import erwin.shared.Const;
import erwin.shared.enums.Operator;

public final class WaveCalc {
    private static final double MANDALA_DIVISOR = 100D;
    private static final double INTENSITY_MULTIPLIER = 10D;
    private final int centerX;
    private final int centerY;
    private final boolean useMagnitude;

    public WaveCalc(final int x0, final int y0, final boolean useMagnitude) {
        this.centerX = x0;
        this.centerY = y0;
        this.useMagnitude = useMagnitude;
    }

    private double calculateIntensity(final double distance) {
        final double intensity = (INTENSITY_MULTIPLIER * (centerX + centerY)) / (Math.pow(distance, 2D) * Const.TAU);
        return intensity > 1D ? 1D : intensity;
    }

    private double calculateDistanceToCenter(final int x, final int y, final int cX, final int cY) {
        return Math.sqrt(Math.pow(x - cX, 2D) + Math.pow(y - cY, 2D));
    }

    private double calculateDistanceToCenter(final int x, final int y) {
        return calculateDistanceToCenter(x, y, centerX, centerY);
    }

    public Complex calculateWave(final int x, final int y, final long t,
            final double waveNumber, final Operator op) {
        final double phase =
                (Math.pow(Const.H_BAR, 2D) * Math.pow(Math.PI, 2D)
                / (2D * Const.MASS) * Math.pow(waveNumber, 2D))
                * (Math.pow(x - centerX, 2D) + Math.pow(y - centerY, 2D));
        final double mag = useMagnitude ? calculateIntensity(calculateDistanceToCenter(x, y)) : 1D;
        if (Operator.ADD.equals(op)) {
            return Complex.fromMagAndPhase(mag, (Const.HALF * phase) - t);
        } else {
            return Complex.fromMagAndPhase(mag, (Const.HALF * phase) * t);
        }
    }

    public Complex calculateDual(final int x, final int y, final int centerX, final int centerY, final long t, final double waveNumber) {
        final double distanceToCenter = calculateDistanceToCenter(x, y, centerX, centerY);
        final double phase = (distanceToCenter - t) * waveNumber;
        final double mag = useMagnitude ? calculateIntensity(calculateDistanceToCenter(x, y)) : 1D;
        return Complex.fromMagAndPhase(mag, phase);
    }

    public Complex calculateMandala(final int x, final int y,
            final long t, final double waveNumber, final Operator op, final Complex old) {
        final double distanceToCenter = calculateDistanceToCenter(x, y);
        final double oldPhase = old.getPhase() != 0D ? old.getPhase() : 1D;
        final double ph = (distanceToCenter - (Double.valueOf(t) / MANDALA_DIVISOR)) * waveNumber;
        final double phase = op.equals(Operator.ADD) ? oldPhase + ph : oldPhase * ph;
        final double mag = useMagnitude ? calculateIntensity(calculateDistanceToCenter(x, y)) : 1D;
        return Complex.fromMagAndPhase(old.getPhase() != 0D ? mag : 0D, phase);
    }
}
