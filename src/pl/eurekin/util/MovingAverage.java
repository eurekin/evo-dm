package pl.eurekin.util;

import java.util.LinkedList;

/**
 *
 * @author Rekin
 */
public class MovingAverage {

    private final int windowSize;
    private final LinkedList<Double> list;
    private double sum;
    private int elements;

    public MovingAverage(int windowSize) {
        this.windowSize = windowSize;
        this.list = new LinkedList<Double>();
    }

    public void addNext(double value) {
        list.addLast(value);
        sum += value;
        if (elements < windowSize) {
            elements += 1;
        } else {
            sum -= list.pollFirst();
        }
    }

    public double getAverage() {
        return sum / elements;
    }
}
