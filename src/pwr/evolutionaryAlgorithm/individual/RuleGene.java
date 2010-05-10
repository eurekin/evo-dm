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

    private BitSet genes;
    static private boolean normalized = true;
    private Condition Cond = null;

    public RuleGene() {
        this.genes = new BitSet(Configuration.getConfiguration().getRuleGeneNoBits());
        for (int i = 0; i < Configuration.getConfiguration().getRuleGeneNoBits(); i++) {
            boolean v = Rand.getRandomBoolean();
            this.genes.set(i, v);
        }
        Cond = null;
    }

    public RuleGene(final RuleGene g) {
        this.genes = new BitSet(Configuration.getConfiguration().getRuleGeneNoBits());
        for (int i = 0; i < Configuration.getConfiguration().getRuleGeneNoBits(); i++) {
            boolean v = g.genes.get(i);
            this.genes.set(i, v);
        }
        Cond = null;
    }

    public void Mutation() {
        for (int i = 0; i < Configuration.getConfiguration().getRuleGeneNoBits(); i++) {
            if (Rand.getRandomBooleanFlip(Configuration.getConfiguration().getMutationValue())) {
                this.genes.flip(i);
            }
            Cond = null;
        }
    }

    /**
     * random initalisation
     */
    public void initialization() {
        for (int i = 0; i < this.genes.length(); i++) {
            this.genes.set(i, Rand.getRandomBooleanFlip(0.5f));
        }
        Cond = null;
    }

    public boolean isOff() {
        return (this.genes.get(0));
    }

    /**
     * (TMP) method in simple form (only in/not in)
     * @return type of relation
     */
    private Condition.RelationType getRelation() {
        if (this.genes.get(1) == true) {
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
        //laezy build
        if (Cond != null) {
            return Cond;
        }

        /////// Binary
        int offset = 1 + Configuration.getConfiguration().getBitsForOperator();
        //float value1 = BinaryCode.getFloatFromBinary( this.Genes.get( offset, offset+Configuration.getRuleGeneValueBits() ) );
        //float value2 = BinaryCode.getFloatFromBinary( this.Genes.get(offset+Configuration.getRuleGeneValueBits(), offset+Configuration.getRuleGeneValueBits()*Configuration.getNumberOfValues()) );

        float value1 = BinaryCode.GrayToFloat(this.genes.get(offset, offset + Configuration.getConfiguration().getRuleGeneValueBits()));
        float value2 = BinaryCode.GrayToFloat(this.genes.get(offset + Configuration.getConfiguration().getRuleGeneValueBits(), offset + Configuration.getConfiguration().getRuleGeneValueBits() * Configuration.getConfiguration().getNumberOfValues()));

        if (normalized) {
            value1 = value1 / Configuration.getConfiguration().getMaxValue();
            value2 = value2 / Configuration.getConfiguration().getMaxValue();
        }

        if (value1 > value2) {
            float tym = value1;
            value1 = value2;
            value2 = tym;
        }
        Condition.RelationType r = this.getRelation();
        try {
            Cond = new Condition(attribID, r, value1, value2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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

    //------------------------------------------------------------------------------
    public int Diverse(final RuleGene g) {
        int diff = 0;
        for (int i = 0; i < Configuration.getConfiguration().getRuleGeneNoBits(); i++) {
            if (genes.get(i) != g.genes.get(i)) {
                diff++;
            }
        }
        return diff;
    }
    //------------------------------------------------------------------------------
}
