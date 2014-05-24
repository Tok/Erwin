package erwin.shared;

public final class Complex {
    public static final Complex I = Complex.valueOf(0D, 1D);
    public static final Complex ZERO = Complex.valueOf(0D, 0D);
    public static final Complex ONE = Complex.valueOf(1D, 0D);

    private double re;
    private double im;

    private Complex(final double real, final double imaginary) { this.re = real; this.im = imaginary; }
    private Complex(final int real, final int imaginary) { this(Double.valueOf(real), Double.valueOf(imaginary)); }
    private Complex(final double real) { this(real, 0D); }
    private Complex(final int real) { this(Double.valueOf(real), 0D); }

    public static Complex valueOf(final double real, final double imaginary) { return new Complex(real, imaginary); }
    public static Complex valueOf(final double real) { return Complex.valueOf(real, 0D); }
    public static Complex valueOfIm(final double imaginary) { return Complex.valueOf(0D, imaginary); }

    public static Complex fromMagAndPhase(final double magnitude, final double phase) {
        return Complex.valueOf(magnitude * Math.cos(phase), magnitude * Math.sin(phase));
    }

    public Complex negate() { return Complex.valueOf(-re, -im); }
    public Complex conjugate() { return Complex.valueOf(re, -im); }
    public Complex reverse() { return Complex.valueOf(-re, im); }

    public Complex add(final Complex c) { return Complex.valueOf(re + c.re, im + c.im); }
    public Complex add(final Double d) { return Complex.valueOf(re + d, im); }
    public Complex subtract(final Complex c) { return Complex.valueOf(re - c.re, im - c.im); }
    public Complex subtract(final Double d) { return Complex.valueOf(re - d, im); }
    public Complex multiply(final Complex c) { return Complex.valueOf((re * c.re) - (im * c.im), (re * c.im) + (im * c.re)); }
    public Complex divide(final Complex c) {
        if (c.re == 0D) { throw new IllegalArgumentException("Real part is 0."); }
        if (c.im == 0D) { throw new IllegalArgumentException("Imaginary part is 0."); }
        final double d = addSquares(c.re, c.im);
        return Complex.valueOf((re * c.re + im * c.im) / d, (im * c.re - re * c.im) / d);
    }

    public double getModulus() { return getMagnitude(); }
    public double getMagnitude() { return Math.sqrt(addSquares(re, im)); }
    public double getMagnitude2() { return addSquares(re, im); }
    public double getPhase() { return Math.atan2(im, re); }

    public double getIm() { return im; }
    public double getRe() { return re; }

    private double addSquares(final double re, final double im) {
        return (re * re) + (im * im);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(im);
        result = prime * result + (int) (temp ^ (temp >>> (prime + 1)));
        temp = Double.doubleToLongBits(re);
        result = prime * result + (int) (temp ^ (temp >>> (prime + 1)));
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        final Complex other = (Complex) obj;
        if (Double.doubleToLongBits(im) != Double.doubleToLongBits(other.im)) { return false; }
        return Double.doubleToLongBits(re) == Double.doubleToLongBits(other.re);
    }

    @Override
    public String toString() {
        if (this.equals(I)) { return "i"; }
        if (this.equals(Complex.valueOf(re))) { return String.valueOf(re); }
        if (this.equals(Complex.valueOf(im))) { return String.valueOf(im) + "*i"; }
        return re + (im < 0D ? "-" + -im : "+" + im) + "*i";
    }
}
