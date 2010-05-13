package pwr.evolutionaryAlgorithm.individual;

import pwr.evolutionaryAlgorithm.Configuration;
import pwr.evolutionaryAlgorithm.data.Condition;
import pwr.evolutionaryAlgorithm.utils.*;
import java.util.*;

/**
 * Class containing Rules <>---- RuleGene
 * @author bred
 */
public class RuleGene {

    private final Configuration configuration = Configuration.getConfiguration();
    private final int ruleGeneValueBits = configuration.getRuleGeneValueBits();
    private final int bitsForOperator = configuration.getBitsForOperator();
    private final float mutationValue = configuration.getMutationValue();
    private final int numberOfValues = configuration.getNumberOfValues();
    private final int ruleGeneNoBits = configuration.getRuleGeneNoBits();
    private final int maxValue = configuration.getMaxValue();
    /////// Binary
    private final boolean normalized = true;
    private Condition Cond = null;
    private BitSet genes;
    /////// Precomputed for optimization
    final int off1 = 1 + bitsForOperator;
    final int off2 = off1 + ruleGeneValueBits;
    final int off3 = off1 + ruleGeneValueBits * numberOfValues;

    public RuleGene() {
        genes = new BitSet(ruleGeneNoBits);
        for (int i = 0; i < ruleGeneNoBits; i++) {
            boolean v = Rand.getRandomBoolean();
            genes.set(i, v);
        }
        Cond = null;
    }

    public RuleGene(final RuleGene g) {
        genes = new BitSet(ruleGeneNoBits);
        for (int i = 0; i < ruleGeneNoBits; i++) {
            boolean v = g.genes.get(i);
            genes.set(i, v);
        }
        Cond = null;
    }

    public void Mutation() {
        for (int i = 0; i < ruleGeneNoBits; i++) {
            if (Rand.getRandomBooleanFlip(mutationValue)) {
                genes.flip(i);
            }
            Cond = null;
        }
    }

    /**
     * random initalisation
     */
    public void initialization() {
        for (int i = 0; i < genes.length(); i++) {
            genes.set(i, Rand.getRandomBooleanFlip(0.5f));
        }
        Cond = null;
    }

    public boolean isOff() {
        return (genes.get(0));
    }

    /**
     * (TMP) method in simple form (only in/not in)
     * @return type of relation
     */
    private Condition.RelationType getRelation() {
        if (genes.get(1)) {
            return Condition.RelationType.IN;
        } else {
            return Condition.RelationType.NOT_IN;
        }
    }

    /**
     * Interpetation as condition a given Rule
     * @param attribID takes atribute id
     * @return Condition object as interpretation of given atribute
     */
    public Condition getCondition(int attribID) {
        //lazy build
        if (Cond != null) {
            return Cond;
        }

        float value1 = BinaryCode.GrayToFloat(genes.get(off1, off2));
        float value2 = BinaryCode.GrayToFloat(genes.get(off2, off3));

        if (normalized) {
            value1 /= maxValue;
            value2 /= maxValue;
        }

        if (value1 > value2) {
            float tym = value1;
            value1 = value2;
            value2 = tym;
        }
        Cond = new Condition(attribID, getRelation(), value1, value2);

        return Cond;
    }

    public String toString(int atrib) {
        StringBuilder s = new StringBuilder();
        if (this.isOff()) {
            s.append(" OFF ");
        } else {
            s.append(getCondition(atrib).toString());
        }
        return s.toString();
    }

    public int Diverse(final RuleGene g) {
        int diff = 0;
        for (int i = 0; i < ruleGeneNoBits; i++) {
            if (genes.get(i) != g.genes.get(i)) {
                diff++;
            }
        }
        return diff;
    }
}
