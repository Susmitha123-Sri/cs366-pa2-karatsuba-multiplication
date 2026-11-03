package edu.wne.cs366;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StudentKaratsubaTest {

    private static BigInteger big(String value) {
        return new BigInteger(value);
    }

    private static void assertMatchesMultiply(BigInteger x, BigInteger y) {
        assertEquals(x.multiply(y), KaratsubaMultiplier.karatsuba(x, y),
                () -> x + " x " + y);
    }

    @Test
    @DisplayName("Multiplication by zero gives zero in either position")
    void multiplicationByZero() {
        BigInteger big = big("98765432109876543210");
        assertEquals(BigInteger.ZERO, KaratsubaMultiplier.karatsuba(BigInteger.ZERO, BigInteger.ZERO));
        assertEquals(BigInteger.ZERO, KaratsubaMultiplier.karatsuba(BigInteger.ZERO, big));
        assertEquals(BigInteger.ZERO, KaratsubaMultiplier.karatsuba(big, BigInteger.ZERO));
        assertEquals(BigInteger.ZERO, KaratsubaMultiplier.karatsuba(BigInteger.ZERO, big.negate()));
    }

    @Test
    @DisplayName("One-digit factors use the base case, including 9 x 9")
    void oneDigitBaseCases() {
        for (int a = 0; a <= 9; a++) {
            for (int b = 0; b <= 9; b++) {
                assertEquals(BigInteger.valueOf((long) a * b),
                        KaratsubaMultiplier.karatsuba(BigInteger.valueOf(a), BigInteger.valueOf(b)));
            }
        }
        assertMatchesMultiply(big("7"), big("123456789"));
        assertMatchesMultiply(big("123456789"), big("7"));
    }

    @Test
    @DisplayName("Unequal digit counts, including a factor shorter than the split point")
    void unequalDigitCounts() {
        assertMatchesMultiply(big("10"), big("1000000000"));
        assertMatchesMultiply(big("99"), big("123456789012345"));
        assertMatchesMultiply(big("123456789012345678901234567890"), big("12"));
        assertMatchesMultiply(big("5"), big("98765432109876543210"));
        assertMatchesMultiply(big("100"), big("99999"));
    }

    @Test
    @DisplayName("Exactly one negative factor gives a negative product")
    void oneNegativeFactor() {
        assertEquals(big("-5196476"), KaratsubaMultiplier.karatsuba(big("-3412"), big("1523")));
        assertEquals(big("-5196476"), KaratsubaMultiplier.karatsuba(big("3412"), big("-1523")));
        assertMatchesMultiply(big("-123456789012345"), big("987654321"));
        assertMatchesMultiply(big("123456789"), big("-98765432109876"));
    }

    @Test
    @DisplayName("Two negative factors give a positive product")
    void twoNegativeFactors() {
        assertEquals(big("5196476"), KaratsubaMultiplier.karatsuba(big("-3412"), big("-1523")));
        assertMatchesMultiply(big("-123456789012345"), big("-987654321098"));
        assertMatchesMultiply(big("-7"), big("-8"));
    }

    @Test
    @DisplayName("A large value needs several recursive levels")
    void largeValueSeveralLevels() {
        BigInteger x = big("3141592653589793238462643383279502884197169399375105820974944592307816406286");
        BigInteger y = big("2718281828459045235360287471352662497757247093699959574966967627724076630353");
        assertMatchesMultiply(x, y);
        assertMatchesMultiply(x, x);

        // Repeated 9s make the digit sums carry, which stresses the (high + low) product.
        BigInteger nines = BigInteger.TEN.pow(60).subtract(BigInteger.ONE);
        assertMatchesMultiply(nines, nines);
    }

    @Test
    @DisplayName("Matches BigInteger.multiply on chosen pairs and seeded random pairs")
    void matchesBigIntegerMultiply() {
        BigInteger[][] pairs = {
            {big("1"), big("1")},
            {big("12"), big("34")},
            {big("3412"), big("1523")},
            {big("12345"), big("6789")},
            {big("999"), big("999")},
            {big("1000"), big("1000")},
            {big("123456789"), big("987654321")},
            {big("-31415926535"), big("27182818284")},
        };
        for (BigInteger[] pair : pairs) {
            assertMatchesMultiply(pair[0], pair[1]);
        }

        Random random = new Random(366);
        for (int i = 0; i < 200; i++) {
            BigInteger x = new BigInteger(1 + random.nextInt(200), random);
            BigInteger y = new BigInteger(1 + random.nextInt(200), random);
            if (random.nextBoolean()) {
                x = x.negate();
            }
            if (random.nextBoolean()) {
                y = y.negate();
            }
            assertMatchesMultiply(x, y);
        }
    }

    @Test
    @DisplayName("Null arguments throw NullPointerException")
    void nullArgumentsThrow() {
        assertThrows(NullPointerException.class, () -> KaratsubaMultiplier.karatsuba(null, BigInteger.ONE));
        assertThrows(NullPointerException.class, () -> KaratsubaMultiplier.karatsuba(BigInteger.ONE, null));
        assertThrows(NullPointerException.class, () -> KaratsubaMultiplier.karatsuba(null, null));
    }

    @Test
    @DisplayName("Input values are left unchanged and the product is commutative")
    void inputsUnchangedAndCommutative() {
        BigInteger x = big("-123456789012345678901234567890");
        BigInteger y = big("987654321098765432109876543210");
        BigInteger xCopy = new BigInteger(x.toByteArray());
        BigInteger yCopy = new BigInteger(y.toByteArray());

        BigInteger forward = KaratsubaMultiplier.karatsuba(x, y);
        BigInteger backward = KaratsubaMultiplier.karatsuba(y, x);

        assertEquals(xCopy, x);
        assertEquals(yCopy, y);
        assertEquals(forward, backward);
        assertEquals(x.multiply(y), forward);
    }
}
