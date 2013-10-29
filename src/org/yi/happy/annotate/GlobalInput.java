package org.yi.happy.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * notes a method or class that uses the global standard input (
 * {@link System#in}). This makes it hard to test because of the dependence on
 * shared global resources which can not be intercepted.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR })
public @interface GlobalInput {

}
