package com.github.ashulin.algorithms.doc;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * @author Li Zongwen
 * @since 2021/12/13
 */
@Documented
@Retention(CLASS)
@Target({METHOD, TYPE})
public @interface Complexity {
    /**
     * The space complexity of this solution.
     *
     * @return space complexity
     */
    String space();

    /**
     * The time complexity of this solution.
     *
     * @return time complexity
     */
    String time();
}
