package com.ctyfl.sleepdemo.config;

import java.util.Calendar;

/**
 * @Author ctyFL
 * @Date 2020-02-19 16:13
 * @Version 1.0
 */
public class SleepConfig {

    private static SleepConfig sleepConfig = new SleepConfig();
    private static long daySleepTime = 1000 * 10; //10秒
    private static long nightSleepTime = 1000 * 7; //7秒
    private static long noOpsQuitTime = 1000 * 5; //5秒

    private SleepConfig() {

    }

    public static SleepConfig getInstance() {
        return sleepConfig;
    }

    public long getAdTimeByDayAndNight() {
        boolean isDay = isDayTime();
        if(isDay) {
            return daySleepTime;
        }else {
            return nightSleepTime;
        }
    }

    public Boolean isDayTime() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if(hourOfDay >= 8 && hourOfDay <= 22) {
            return true;
        }else {
            return false;
        }
    }

    public long getDaySleepTime() {
        return daySleepTime;
    }

    public void setDaySleepTime(long daySleepTime) {
        this.daySleepTime = daySleepTime;
    }

    public long getNightSleepTime() {
        return nightSleepTime;
    }

    public void setNightSleepTime(long nightSleepTime) {
        this.nightSleepTime = nightSleepTime;
    }

    public long getNoOpsQuitTime() {
        return noOpsQuitTime;
    }

    public void setNoOpsQuitTime(long noOpsQuitTime) {
        this.noOpsQuitTime = noOpsQuitTime;
    }

}
