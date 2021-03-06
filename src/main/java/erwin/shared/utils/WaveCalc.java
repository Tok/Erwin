package erwin.shared.utils;

import erwin.shared.Complex;
import erwin.shared.Const;
import erwin.shared.enums.Operator;

public final class WaveCalc {
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
        return Math.sqrt(addSquares(x - cX, y - cY));
    }

    private double calculateDistanceToCenter(final int x, final int y) {
        return calculateDistanceToCenter(x, y, centerX, centerY);
    }

    public Complex calculateWave(final int x, final int y, final double t,
            final double waveNumber, final Operator op) {
        final double phase =
                Const.H_BAR * Const.H_BAR * Math.PI * Math.PI * addSquares(x - centerX, y - centerY)
                / (2D * Const.MASS * waveNumber * waveNumber);
        final double mag = useMagnitude ? calculateIntensity(calculateDistanceToCenter(x, y)) : 1D;
        final double halfPhase = Const.HALF * phase;
        return Complex.fromMagAndPhase(mag, Operator.ADD.equals(op) ? halfPhase - t : halfPhase * t);
    }

    public Complex calculateDual(final int x, final int y, final int centerX, final int centerY,
            final double t, final double waveNumber) {
        final double distanceToCenter = calculateDistanceToCenter(x, y, centerX, centerY);
        final double phase = (distanceToCenter - t) * waveNumber;
        final double mag = useMagnitude ? calculateIntensity(calculateDistanceToCenter(x, y)) : 1D;
        return Complex.fromMagAndPhase(mag, phase);
    }

    public Complex calculateMandala(final int x, final int y,
            final double dt, final double whatever, final Operator op, final Complex old) {
        final double distanceToCenter = calculateDistanceToCenter(
                Double.valueOf(x / whatever).intValue(),
                Double.valueOf(y / whatever).intValue());
        final double oldPhase = old.getPhase() != 0D ? old.getPhase() : 1D;
        final double dph = distanceToCenter * dt * 0.1D;
        final double phase = op.equals(Operator.ADD) ? oldPhase + dph : oldPhase * dph;
        final double mag = useMagnitude ? calculateIntensity(calculateDistanceToCenter(x, y)) : 1D;
        return Complex.fromMagAndPhase(old.getPhase() != 0D ? mag : 0D, phase);
    }

    private double addSquares(final double first, final double second) {
        return (first * first) + (second * second);
    }
}
