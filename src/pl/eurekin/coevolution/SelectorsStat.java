package pl.eurekin.coevolution;

import pwr.evolutionaryAlgorithm.Population;
import pwr.evolutionaryAlgorithm.data.DataLoader;
import pwr.evolutionaryAlgorithm.individual.RuleSet;

/**
 *
 * @author Rekin
 */
public class SelectorsStat implements CoevolutionEventListener {

    int[] hits;
    float[] absolute;
    float[] relative;

    public float[] getAbsoluteStats() {
        if (absolute != null) {
            return absolute;
        }

        absolute = new float[hits.length];
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < hits.length; i++) {
            max = hits[i] > max ? hits[i] : max;
        }
        for (int i = 0; i < hits.length; i++) {
            absolute[i] = ((float) hits[i]) / max;
        }

        return absolute;
    }

    public float[] getRelativeStats() {
        if (relative != null) {
            return relative;
        }

        relative = new float[hits.length];
        int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        for (int i = 0; i < hits.length; i++) {
            max = hits[i] > max ? hits[i] : max;
            min = hits[i] < min ? hits[i] : min;
        }
        float range = max - min;
        for (int i = 0; i < hits.length; i++) {
            relative[i] = ((float) (hits[i] - min)) / range;
        }

        return relative;
    }

    public SelectorsStat() {
        this.hits = new int[DataLoader.getTrainData().size()];
    }
    private int gens = 0;
    private long elapsed = System.currentTimeMillis();

    @Override
    public void evolvedAGeneration(Population<Selector> sels, Population<RuleSet> cls) {
        gens++;
        for (Selector selector : sels) {
            for (int i = 0; i < hits.length; i++) {
                if (selector.isSelected(i)) {
                    hits[DataLoader.getAllData().get(i).getTrueID()] += 1;
                }
            }
        }

        if ((System.currentTimeMillis() - elapsed) / 1000 >= 60) {
            System.out.println("gens = " + gens);
            elapsed = System.currentTimeMillis();
        }
    }
}
