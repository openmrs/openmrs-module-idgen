package org.openmrs.module.idgen.util;

/**
 *
 */
public class ExceptionUtils {

    /**
     * @param e
     * @param className
     * @param methodName
     *
     * @return true if e's stack trace contains an element with className.methodName
     */
    public static boolean isThrownFrom(Exception e, String className, String methodName) {
        if (e.getStackTrace() != null) {
            for (StackTraceElement element : e.getStackTrace()) {
                if (className.equals(element.getClassName()) && (methodName == null || methodName.equals(element.getMethodName()))) {
                    return true;
                }
            }
        }
        return false;
    }

}
