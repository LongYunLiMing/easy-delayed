package com.bds.easy.delayed.core;

/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2020/12/30 10:00
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2020/12/30
 */
public class SchedulerException extends Exception{
    private static final long serialVersionUID = 174841398690789159L;

    public SchedulerException() {
        super();
    }

    public SchedulerException(String msg) {
        super(msg);
    }

    public SchedulerException(Throwable cause) {
        super(cause);
    }

    public SchedulerException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public Throwable getUnderlyingException() {
        return super.getCause();
    }

    @Override
    public String toString() {
        Throwable cause = getUnderlyingException();
        if (cause == null || cause == this) {
            return super.toString();
        } else {
            return super.toString() + " [See nested exception: " + cause + "]";
        }
    }
}
