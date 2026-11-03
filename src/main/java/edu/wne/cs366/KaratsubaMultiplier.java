package edu.wne.cs366;

import java.math.BigInteger;

/**
 * CS366 Programming Assignment 02: Karatsuba Multiplication.
 *
 * @author Sita Susmitha Prakki
 */
public class KaratsubaMultiplier {

    private static final BigInteger TEN = BigInteger.TEN;

    /**
     * Multiplies two signed integers using recursive Karatsuba multiplication.
     *
     * @param x first factor
     * @param y second factor
     * @return x multiplied by y
     * @throws NullPointerException when either argument is null
     */
    public static BigInteger karatsuba(BigInteger x, BigInteger y) {
        if (x == null || y == null) {
            throw new NullPointerException("factors must not be null");
        }

        // Record the sign of the result, then work only with nonnegative values.
        boolean negative = x.signum() * y.signum() < 0;
        BigInteger product = karatsubaNonnegative(x.abs(), y.abs());
        return negative ? product.negate() : product;
    }

    private static BigInteger karatsubaNonnegative(BigInteger x, BigInteger y) {
        // Base case: either factor has a single decimal digit (this includes zero).
        if (x.compareTo(TEN) < 0 || y.compareTo(TEN) < 0) {
            return x.multiply(y);
        }

        // n is the greater digit count; split both factors at the same power of ten.
        int n = Math.max(decimalDigits(x), decimalDigits(y));
        int m = n / 2;
        BigInteger base = TEN.pow(m);

        BigInteger[] xParts = x.divideAndRemainder(base);
        BigInteger[] yParts = y.divideAndRemainder(base);
        BigInteger xHigh = xParts[0];
        BigInteger xLow = xParts[1];
        BigInteger yHigh = yParts[0];
        BigInteger yLow = yParts[1];

        // Exactly three recursive products.
        BigInteger highProduct = karatsubaNonnegative(xHigh, yHigh);
        BigInteger lowProduct = karatsubaNonnegative(xLow, yLow);
        BigInteger sumProduct = karatsubaNonnegative(xHigh.add(xLow), yHigh.add(yLow));
        BigInteger crossProduct = sumProduct.subtract(highProduct).subtract(lowProduct);

        // result = high * 10^(2m) + cross * 10^m + low
        return highProduct.multiply(TEN.pow(2 * m))
                .add(crossProduct.multiply(base))
                .add(lowProduct);
    }

    private static int decimalDigits(BigInteger value) {
        if (value.signum() == 0) {
            return 1;
        }
        // Estimate from the bit length, then correct against exact powers of ten.
        int digits = (int) ((value.bitLength() - 1) * Math.log10(2)) + 1;
        while (digits > 1 && TEN.pow(digits - 1).compareTo(value) > 0) {
            digits--;
        }
        while (TEN.pow(digits).compareTo(value) <= 0) {
            digits++;
        }
        return digits;
    }

    public static void main(String[] args) {
        BigInteger x = new BigInteger("3412");
        BigInteger y = new BigInteger("1523");
        System.out.printf("%s × %s = %s%n", x, y, karatsuba(x, y));
    }
}