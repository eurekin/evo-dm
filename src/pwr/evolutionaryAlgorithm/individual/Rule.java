package pwr.evolutionaryAlgorithm.individual;

import java.util.ArrayList;
import java.util.BitSet;
import pwr.evolutionaryAlgorithm.Configuration;
import pwr.evolutionaryAlgorithm.Configuration.MutationType;
import pwr.evolutionaryAlgorithm.data.Condition;
import pwr.evolutionaryAlgorithm.data.Evaluation;
import pwr.evolutionaryAlgorithm.utils.BinaryCode;
import pwr.evolutionaryAlgorithm.utils.Rand;

public class Rule extends Individual {

    private boolean active; //a bit informs if rule is active
    private ArrayList<RuleGene> conditionOnAttribute;
    private BitSet classID;
    private final Configuration configuration = Configuration.getConfiguration();
    private final int classBits = configuration.getClassBits();
    private final MutationType mutationType = configuration.getMutationType();
    private final float mutationValue = configuration.getMutationValue();
    private final int numberOfAttributes = configuration.getNumberOfAttributes();

    /**
     * main constructor
     */
    public Rule() {
        evaluations = new ArrayList<Evaluation>();
        evaluations.add(new Evaluation());
        active = true;
        conditionOnAttribute = new ArrayList<RuleGene>(numberOfAttributes);
        for (int i = 0; i < numberOfAttributes; i++) {
            RuleGene dg = new RuleGene();
            conditionOnAttribute.add(dg);
        }
        classID = new BitSet(classBits);
    }

    public Rule(final Rule R) {
        this.active = R.active;
        this.evaluations = new ArrayList<Evaluation>();
        evaluations.add(new Evaluation(R.getEvaluation()));

        this.conditionOnAttribute = new ArrayList<RuleGene>(numberOfAttributes);
        for (int i = 0; i < numberOfAttributes; i++) {
            RuleGene dg = new RuleGene(R.getGene(i));
            conditionOnAttribute.add(dg);
        }

        this.classID = new BitSet(classBits);
        for (int i = 0; i < classBits; i++) {
            boolean b = R.classID.get(i);
            this.classID.set(i, b);
        }
    }

    @Override
    public void init() {
        this.active = Rand.getRandomBoolean();
        for (int i = 0; i < numberOfAttributes; i++) {
            conditionOnAttribute.get(i).initialization();
        }
        for (int i = 0; i < classBits; i++) {
            this.classID.set(i, Rand.getRandomBoolean());
        }

        this.clearEvaluations();
    }

    public boolean isCondition(int attribID) {
        return !conditionOnAttribute.get(attribID).isOff();
    }

    public Condition getCondition(int attribID) {
        return conditionOnAttribute.get(attribID).getCondition(attribID); // smell
    }

    @Override
    public Evaluation getEvaluation(int cl) {
        return this.evaluations.get(0);
    }

    @Override
    protected int getGenesInIndividual() {
        return numberOfAttributes;
    }

    private String toStringBeauty() {
        StringBuilder s = new StringBuilder("");

        if (!this.active) {
            s.append("---");
        } else {
            s.append("IF ");

            boolean first = true;

            for (int i = 0; i < numberOfAttributes; i++) {
                if (this.conditionOnAttribute.get(i).isOff() == false) {
                    if (!first) {
                        s.append(" AND ");
                    }
                    s.append(" a").append(this.conditionOnAttribute.get(i).toString(i));
                    first = false;
                }
            }

            s.append(" THEN class = ");
            s.append(this.getClassID());

            this.getEvaluation().toString();
        }
        return s.toString();
    }

    @Override
    public String toString() {
        return this.toStringBeauty();
    }

    @Override
    public Individual mutate() {

        if (mutationType == MutationType.FAM) {
            return MutationFAM();
        } else {
            return MutationSimple();
        }
    }

    /**
     * Directed: F-score Aided [mutate]
     * Pm' := Pm + (1.0-Er)*Pm
     * @TODO Pm' := Pm + (1-Er*Eclass)*Pm
     */
    private Individual MutationFAM() {

        float Pm = mutationValue;
        float Fsc = this.getEvaluation(0).getFsc();
        float Pmutation = (float) (Pm + (1.0 - Fsc) * Pm);

        Rule tym = new Rule();
        //copying -> new
        for (int i = 0; i < numberOfAttributes; i++) {
            tym.setGene(i, new RuleGene(this.getGene(i)));
        }

        for (int i = 0; i < classBits; i++) {
            tym.classID.set(i, this.classID.get(i));
        }
        //end: copying

        //active flag
        if (Rand.getRandomBooleanFlip(Pmutation) == true) {
            this.active = !(this.active);
        }

        //body
        for (int i = 0; i < numberOfAttributes; i++) {
            tym.conditionOnAttribute.get(i).Mutation();
        }

        //class
        if (configuration.isMutationOfClass() == true) {
            for (int i = 0; i < classBits; i++) {
                if (Rand.getRandomBooleanFlip(Pmutation) == true) {
                    tym.classID.set(i, !tym.classID.get(i));
                }
            }
        }

        tym.clearEvaluations();
        return tym;
    }

    private Individual MutationSimple() {

        float Pmutation = mutationValue;

        Rule tym = new Rule();
        //copying
        for (int i = 0; i < numberOfAttributes; i++) {
            tym.setGene(i, new RuleGene(this.getGene(i)));
        }

        for (int i = 0; i < classBits; i++) {
            tym.classID.set(i, this.classID.get(i));
        }
        tym.clearEvaluations();
        //end: copying

        //body
        for (int i = 0; i < numberOfAttributes; i++) {
            tym.conditionOnAttribute.get(i).Mutation();
        }

        //class
        if (configuration.isMutationOfClass() == true) {
            for (int i = 0; i < classBits; i++) {
                if (Rand.getRandomBooleanFlip(Pmutation) == true) {
                    tym.classID.set(i, !this.classID.get(i));
                }
            }
        }
        return tym;
    }

    /**
     * todo: this method should be protected
     * @param no 
     * @return
     */
    public RuleGene getGene(int no) {
        return this.conditionOnAttribute.get(no);
    }

    public int getClassID() {
        int class_id = BinaryCode.getFloatFromBinary(classID);
        /*DOMYSLNA KLASA 0*/
        if (class_id > Configuration.getClassesNo()) {
            return 0;
        } else {
            return class_id;
        }
    }

    protected void setGene(int no, RuleGene g) {
        this.clearEvaluations();
        this.conditionOnAttribute.set(no, g);
    }

    /**
     * Two point crossover     indv1 [cut] indv2 
     * @param Indv1 parent1
     * @return new individual that is combination of two indoviduals
     *
     */
    @Override
    public Individual crossoverWith(Individual Indv1) {
        Rule tym = new Rule();
        int cut = Rand.getRandomInt(numberOfAttributes);
        for (int i = 0; i < numberOfAttributes; i++) {
            RuleGene d = null;
            if (i <= cut) {
                d = new RuleGene(((Rule) Indv1).getGene(i));
            } else {
                d = new RuleGene((this).getGene(i));
            }
            tym.setGene(i, d);
        }

        boolean classFromOne = Rand.getRandomBoolean();
        Rule R = null;
        if (classFromOne) {
            R = new Rule((Rule) Indv1);
        } else {
            R = new Rule(this);
        }

        for (int i = 0; i < classBits; i++) {
            tym.classID.set(i, R.classID.get(i));

        }
        return tym;
    }

    /**
     * informs if Rule in RuleSet is active
     * @return true (active) of false (no active)
     */
    public boolean isActive() {
        return this.active;
    }

    public boolean isEmpty() {
        for (int attribID = 0; attribID < numberOfAttributes; attribID++) {
            if (this.conditionOnAttribute.get(attribID).isOff() == false) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int diversityMeasure(final Individual I) {
        int diff = 0;
        if (I instanceof Rule) {
            Rule R = (Rule) I;
            for (int i = 0; i < numberOfAttributes; i++) {
                diff += conditionOnAttribute.get(i).Diverse(R.conditionOnAttribute.get(i));
            }
        } else {
            throw new RuntimeException("Illegal Object!");
        }
        return diff;
    }
}
