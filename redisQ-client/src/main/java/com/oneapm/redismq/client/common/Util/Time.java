package com.oneapm.redismq.client.common.Util;

public interface Time {

    /**
     * The current time in milliseconds
     *
     * @return millseconds
     */
    public long milliseconds();

    /**
     * The current time in nanoseconds
     * @return nanoseconds
     */
    public long nanoseconds();

    /**
     * Sleep for the given number of milliseconds
     * @param ms millseconds
     */
    public void sleep(long ms);

}
