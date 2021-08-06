# IEEE754浮点数

> 以下Java代码为JDK 1.8的Float包装类源码

## 浮点数的存储格式

IEEE754标准中规定

- float单精度浮点数在机器中表示用 1 位表示数字的符号，用 8 位表示指数，用 23 位表示尾数，即小数部分。
- double双精度浮点数，用 1 位表示符号，用 11 位表示指数，52 位表示尾数，其中指数域称为阶码。
- IEEE754浮点数的格式如下图所示：

![](https://upload.wikimedia.org/wikipedia/commons/thumb/7/75/General_floating_point_frac.svg/490px-General_floating_point_frac.svg.png)

float的指数偏移值为$$2^7-1=127$$
double的指数偏移值为$$2^{10}-1=1023$$

IEEE754标准中，一个规格化32位的浮点数$$x$$的真值表示为：
$$
x=(-1)^S*(1.M)*2^e
$$
其中，S为符号值，M为尾数，e为真实指数值，即减去偏移值后的值。

## 指数位的特殊情况

##### 当指数位全为1时，即$[11111111]_2$

- 当尾数位全为0，则表示为±∞。

    ```java
        /**
         * A constant holding the positive infinity of type
         * {@code float}. It is equal to the value returned by
         * {@code Float.intBitsToFloat(0x7f800000)}.
         */
        public static final float POSITIVE_INFINITY = 1.0f / 0.0f;
    
        /**
         * A constant holding the negative infinity of type
         * {@code float}. It is equal to the value returned by
         * {@code Float.intBitsToFloat(0xff800000)}.
         */
        public static final float NEGATIVE_INFINITY = -1.0f / 0.0f;
    ```

- 当尾数位不全为0，则表示这不是一个数（NaN）。

    ```java
        /**
         * A constant holding a Not-a-Number (NaN) value of type
         * {@code float}.  It is equivalent to the value returned by
         * {@code Float.intBitsToFloat(0x7fc00000)}.
         */
        public static final float NaN = 0.0f / 0.0f;
    ```

- 所以最大指数值为$$[1111 1110] = 254 - 127 = 127$$ 

    ```java
        /**
         * Maximum exponent a finite {@code float} variable may have.  It
         * is equal to the value returned by {@code
         * Math.getExponent(Float.MAX_VALUE)}.
         *
         * @since 1.6
         */
        public static final int MAX_EXPONENT = 127;
    ```

    

##### 当指数位为0时，即$[00000000]_2$

- 真值表示为
    $$
    x=(-1)^S*(0.M)*2^{-126}
    $$

    例：
    $$
    [1 0000 0000 000 0000 0000 0000 0000 0001]_2 = (-1)^1*(0.000 0000 0000 0000 0000 0001)_2*2^{-126}=-2^{-149}
    $$

    ```java
        /**
         * A constant holding the smallest positive nonzero value of type
         * {@code float}, 2<sup>-149</sup>. It is equal to the
         * hexadecimal floating-point literal {@code 0x0.000002P-126f}
         * and also equal to {@code Float.intBitsToFloat(0x1)}.
         */
        public static final float MIN_VALUE = 0x0.000002P-126f; // 1.4e-45f
    ```

    正常浮点值下，正数最小值为
    $$
    0X00800000 = [0 0000 0001 000 0000 0000 0000 0000 0000] = (-1)^0*(1.0)*2^{1-127} = 2^{-126}
    $$

    ```java
        /**
         * A constant holding the smallest positive normal value of type
         * {@code float}, 2<sup>-126</sup>.  It is equal to the
         * hexadecimal floating-point literal {@code 0x1.0p-126f} and also
         * equal to {@code Float.intBitsToFloat(0x00800000)}.
         *
         * @since 1.6
         */
        public static final float MIN_NORMAL = 0x1.0p-126f; // 1.17549435E-38f
    ```

    指数位全为0时，指数值为$$-126$$，而不是$$0-127 = -127$$，以下为个人猜测：

    当$$M$$取最大值[111 1111 1111 1111 1111 1111]时，若$$e = -127$$。则
    $$
    x = (-1)^0*(0.M)*2^{-127} ≈ 2^{-127}
    $$
    而，正常最小值为$$2^{-126}$$，这会导致$$2^{-126}$$与$$2^{-127}$$之间的值无法表示。

    ```
        /**
         * Minimum exponent a normalized {@code float} variable may have.
         * It is equal to the value returned by {@code
         * Math.getExponent(Float.MIN_NORMAL)}.
         *
         * @since 1.6
         */
        public static final int MIN_EXPONENT = -126;
    ```

    ------

    参考：
    
    1.[IEEE 754 - 维基百科，自由的百科全书](https://zh.wikipedia.org/wiki/IEEE_754)
    
    