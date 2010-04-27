/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author bred
 */
public class Clock {

    private long totalTime;
    private long hrs;
    private long start;
    private long mils;
    private long mins;
    private long secs;

    public Clock() {
        Reset();
    }

    public void Pause() {
        long pause = System.currentTimeMillis();
        totalTime += (pause - start);
    }

    public void Start() {
        totalTime = 0;
        start = System.currentTimeMillis();
    }

    public void Continue() {
        start = System.currentTimeMillis();
    }

    public long GetTotalTime() {
        return totalTime;
    }

    public void calculateTime() {
        hrs = totalTime / 3600000;
        mins = (totalTime - hrs * 3600000L) / 60000L;
        secs = (totalTime - hrs * 3600000L - mins * 60000L) / 1000L;
        mils = totalTime % 1000L;
    }

    public void Reset() {
        totalTime = 0;
        hrs = 0;
        mins = 0;
        secs = 0;
    }

    /**
     * @return String that represents measured time in format hh:mm:ss.mils
     */
    @Override
    public String toString() {
        calculateTime();
        return String.format("%d:%d:%d.%03d", hrs, mins, secs, mils);
    }
}
