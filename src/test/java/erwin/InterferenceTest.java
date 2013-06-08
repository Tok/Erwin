package erwin;

import junit.framework.TestCase;
import erwin.shared.Complex;
import erwin.shared.Const;
import erwin.shared.utils.ColorUtil;

public class InterferenceTest extends TestCase {
    private static final double HALF = 0.5D;
    private static final double BLUE_DEG = -120.0D;
    private static final double YELLOW_DEG = 45.0D;
    private static final double PURPLE_DEG = -75.0D;

    private final Complex halfRed = Complex.valueOf(HALF, 0.0D);
    private final Complex halfCyan = Complex.valueOf(-HALF, 0.0D);
    private final Complex halfGreen = Complex.valueOf(0.0D, HALF);
    private final Complex halfBlue = Complex.fromMagAndPhase(HALF, Math.toRadians(BLUE_DEG));
    private final Complex halfYellow = Complex.fromMagAndPhase(HALF, Math.toRadians(YELLOW_DEG));
    private final Complex fullBlue = Complex.fromMagAndPhase(1.0D, Math.toRadians(BLUE_DEG));
    private final Complex fullYellow = Complex.fromMagAndPhase(1.0D, Math.toRadians(YELLOW_DEG));

    public final void testColors() {
        assertEquals(HALF, halfRed.getMagnitude());
        assertEquals(0.0D, halfRed.getPhase());
        assertEquals("rgb(127,0,0)", ColorUtil.getColor(halfRed));
        assertEquals(HALF, halfCyan.getMagnitude());
        assertEquals(Math.PI, halfCyan.getPhase());
        assertEquals("rgb(0,127,127)", ColorUtil.getColor(halfCyan));
        assertEquals(HALF, halfGreen.getMagnitude());
        assertEquals(Math.PI / 2D, halfGreen.getPhase());
        assertEquals("rgb(63,127,0)", ColorUtil.getColor(halfGreen));
        assertEquals("rgb(0,0,127)", ColorUtil.getColor(halfBlue));
        assertEquals("rgb(127,95,0)", ColorUtil.getColor(halfYellow));
        assertEquals("rgb(0,0,254)", ColorUtil.getColor(fullBlue));
        assertEquals("rgb(255,191,0)", ColorUtil.getColor(fullYellow));
    }

    public final void testConstructive() {
        final Complex constructive = halfRed.add(halfRed);
        assertEquals(halfRed.getRe() * 2D, constructive.getRe());
        assertEquals(halfRed.getIm(), constructive.getIm());
        assertEquals(halfRed.getMagnitude() * 2D, constructive.getMagnitude());
        assertEquals(halfRed.getPhase(), constructive.getPhase());
        assertEquals("rgb(255,0,0)", ColorUtil.getColor(constructive));
    }

    public final void testDestructive() {
        final Complex destructive = halfRed.add(halfCyan);
        assertEquals(0.0D, destructive.getRe());
        assertEquals(0.0D, destructive.getIm());
        assertEquals(0.0D, destructive.getMagnitude());
        assertEquals(0.0D, destructive.getPhase());
        assertEquals(Const.BLACK, ColorUtil.getColor(destructive));
    }

    public final void testAddition() {
        final Complex added = halfRed.add(halfGreen);
        assertEquals(HALF, added.getRe());
        assertEquals(HALF, added.getIm());
        assertEquals(Math.sqrt(2D) / 2D, added.getMagnitude());
        assertEquals(YELLOW_DEG, Math.toDegrees(added.getPhase()));
        assertEquals("rgb(180,135,0)", ColorUtil.getColor(added));
    }

    public final void testMultiplicationFullMagnitude() {
        final Complex product = fullBlue.multiply(fullYellow);
        assertEquals(Math.round(1.0D), Math.round(product.getMagnitude()));
        assertEquals(Math.round(PURPLE_DEG), Math.round(Math.toDegrees(product.getPhase())));
        assertEquals("rgb(191,0,254)", ColorUtil.getColor(product));
    }

    public final void testMultiplicationHalfMagnitude() {
        final Complex product = halfBlue.multiply(halfYellow);
        assertEquals(Math.round(HALF / 2D), Math.round(product.getMagnitude()));
        assertEquals(Math.round(PURPLE_DEG), Math.round(Math.toDegrees(product.getPhase())));
        assertEquals("rgb(47,0,63)", ColorUtil.getColor(product));
    }
}
