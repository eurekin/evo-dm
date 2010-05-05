package pwr.evolutionaryAlgorithm.individual;

import pwr.evolutionaryAlgorithm.*;
import pwr.evolutionaryAlgorithm.Configuration.MutationType;
import pwr.evolutionaryAlgorithm.utils.*;

import java.util.ArrayList;
import java.util.BitSet;
import pwr.evolutionaryAlgorithm.data.Condition;
import pwr.evolutionaryAlgorithm.data.Evaluation;

public class Rule extends Individual {

    private boolean Active; //a bit inbforms if rule is active
    public ArrayList<RuleGene> ConditionOnAttribute;
    protected BitSet ClassID;

    //------------------------------------------------------------------------------
    /**
     * main constructor
     * @param Genes number of bits in gene
     */
    public Rule() {
        this.Evaluations = new ArrayList<Evaluation>();
        Evaluations.add(new Evaluation());
        this.Active = true;
        this.ConditionOnAttribute = new ArrayList<RuleGene>(Configuration.getConfiguration().getNumberOfAttributes());
        for (int i = 0; i < Configuration.getConfiguration().getNumberOfAttributes(); i++) {
            RuleGene dg = new RuleGene();
            ConditionOnAttribute.add(dg);
        }
        this.ClassID = new BitSet(Configuration.getConfiguration().getClassBits());
    }

    //------------------------------------------------------------------------------
    public Rule(final Rule R) {
        this.Active = R.Active;
        this.Evaluations = new ArrayList<Evaluation>();
        Evaluations.add(new Evaluation(R.getEvaluation()));

        this.ConditionOnAttribute = new ArrayList<RuleGene>(Configuration.getConfiguration().getNumberOfAttributes());
        for (int i = 0; i < Configuration.getConfiguration().getNumberOfAttributes(); i++) {
            RuleGene dg = new RuleGene(R.getGene(i));
            ConditionOnAttribute.add(dg);
        }

        this.ClassID = new BitSet(Configuration.getConfiguration().getClassBits());
        for (int i = 0; i < Configuration.getConfiguration().getClassBits(); i++) {
            boolean b = R.ClassID.get(i);
            this.ClassID.set(i, b);
        }
    }

    //------------------------------------------------------------------------------
    @Override
    public void init() {
        this.Active = Rand.GetRandomBoolean();
        for (int i = 0; i < Configuration.getConfiguration().getNumberOfAttributes(); i++) {
            ConditionOnAttribute.get(i).Initialisation();
        }
        for (int i = 0; i < Configuration.getConfiguration().getClassBits(); i++) {
            this.ClassID.set(i, Rand.GetRandomBoolean());
        }

        this.clearEvaluations();
    }

    //------------------------------------------------------------------------------
    public boolean isCondition(int attribID) {
        return !(this.ConditionOnAttribute.get(attribID).isOff());
    }

    //------------------------------------------------------------------------------\
    public Condition getCondition(int attribID) {
        return this.ConditionOnAttribute.get(attribID).getCondition(attribID);
    }

    //------------------------------------------------------------------------------
    @Override
    public Evaluation getEvaluation(int cl) {
        return this.Evaluations.get(0);
    }

//  ------------------------------------------------------------------------------
    @Override
    protected int getGenesInIndividual() {
        return Configuration.getConfiguration().getNumberOfAttributes();
    }

    //------------------------------------------------------------------------------
  /*  
    private String toStringTechnical(){
    StringBuilder s = new StringBuilder("");

    if (!this.Active) s.append("[X]");
    else s.append("[ ]");

    boolean first = true;

    for (int i=0;i<Configuration.getConfiguration().getNumberOfAttributes();i++){
    if (!first) s.append(" AND ");
    s.append( " a"+this.ConditionOnAttribute.get(i).toString(i) );
    first = false;
    }


    s.append(" class ");
    s.append(this.getClassID());
    //s.append(this.ClassID.toString());

    if (this.Fitness!=Configuration.getConfiguration().getFINTESSDEFAULT() && this.Fitness!=0.0){
    s.append( " rec="+String.format("%.3f",this.Recall)
    +" prec="+String.format("%.3f",this.Precision)
    +" acc="+String.format("%.3f",this.Accuracy)
    +" Fsc="+String.format("%.3f",this.Fsc));
    }

    else s.append(" not used (0.0)");
    return s.toString();
    }
     */
    //------------------------------------------------------------------------------
    private String toStringBeauty() {
        StringBuilder s = new StringBuilder("");

        if (!this.Active) {
            s.append("---");
        } else {
            s.append("IF ");

            boolean first = true;

            for (int i = 0; i < Configuration.getConfiguration().getNumberOfAttributes(); i++) {
                if (this.ConditionOnAttribute.get(i).isOff() == false) {
                    if (!first) {
                        s.append(" AND ");
                    }
                    s.append(" a" + this.ConditionOnAttribute.get(i).toString(i));
                    first = false;
                }
            }

            s.append(" THEN class = ");
            s.append(this.getClassID());

            this.getEvaluation().toString();
        }
        return s.toString();
    }

    //------------------------------------------------------------------------------
    @Override
    public String toString() {
        //return this.toStringTechnical();
        return this.toStringBeauty();
    }

    //------------------------------------------------------------------------------
    @Override
    public Individual Mutation() {

        if (Configuration.getConfiguration().getMutationType() == MutationType.FAM) {
            return MutationFAM();
        } else {
            return MutationSimple();
        }
    }

    //------------------------------------------------------------------------------
    /**
     * Directed: F-score Aided [Mutation]
     * Pm' := Pm + (1.0-Er)*Pm
     * @TODO Pm' := Pm + (1-Er*Eclass)*Pm
     */
    private Individual MutationFAM() {

        float Pm = Configuration.getConfiguration().getMutationValue();
        float Fsc = this.getEvaluation(0).getFsc();
        float Pmutation = (float) (Pm + (1.0 - Fsc) * Pm);

        Rule tym = new Rule();
        //copying -> new
        for (int i = 0; i < Configuration.getConfiguration().getNumberOfAttributes(); i++) {
            tym.setGene(i, new RuleGene(this.getGene(i)));
        }

        for (int i = 0; i < Configuration.getConfiguration().getClassBits(); i++) {
            tym.ClassID.set(i, this.ClassID.get(i));
        }
        //end: copying

        //active flag
        if (Rand.getRandomBooleanFlip(Pmutation) == true) {
            this.Active = !(this.Active);
        }

        //body
        for (int i = 0; i < Configuration.getConfiguration().getNumberOfAttributes(); i++) {
            tym.ConditionOnAttribute.get(i).Mutation();
        }

        //class
        if (Configuration.getConfiguration().isMutationOfClass() == true) {
            for (int i = 0; i < Configuration.getConfiguration().getClassBits(); i++) {
                if (Rand.getRandomBooleanFlip(Pmutation) == true) {
                    tym.ClassID.set(i, !tym.ClassID.get(i));
                }
            }
        }

        tym.clearEvaluations();
        return tym;
    }

    //------------------------------------------------------------------------------
    private Individual MutationSimple() {

        float Pmutation = Configuration.getConfiguration().getMutationValue();

        Rule tym = new Rule();
        //copying
        for (int i = 0; i < Configuration.getConfiguration().getNumberOfAttributes(); i++) {
            tym.setGene(i, new RuleGene(this.getGene(i)));
        }

        for (int i = 0; i < Configuration.getConfiguration().getClassBits(); i++) {
            tym.ClassID.set(i, this.ClassID.get(i));
        }
        tym.clearEvaluations();
        //end: copying

        //active flag
        /*TODO wylaczona mutacja on/off reguly 
        if ( Rand.GetRandomBooleanFlip(Pmutation)==true )    {
        this.Active = !(this.Active);

        //TODO REMOVE IT!
        //if (this.Active==false) System.out.print("M-");
        //else System.out.print("M+");

        }*/

        //body
        for (int i = 0; i < Configuration.getConfiguration().getNumberOfAttributes(); i++) {
            tym.ConditionOnAttribute.get(i).Mutation();
        }

        //class
        if (Configuration.getConfiguration().isMutationOfClass() == true) {
            for (int i = 0; i < Configuration.getConfiguration().getClassBits(); i++) {
                if (Rand.getRandomBooleanFlip(Pmutation) == true) {
                    tym.ClassID.set(i, !this.ClassID.get(i));
                }
            }
        }
        return tym;
    }

    //------------------------------------------------------------------------------
    /**
     * todo: this method should be protected
     */
    public RuleGene getGene(int no) {
        return this.ConditionOnAttribute.get(no);
    }

    //------------------------------------------------------------------------------
    public int getClassID() {
        int class_id = (int) BinaryCode.getFloatFromBinary(ClassID);
        /*DOMYSLNA KLASA 0*/
        if (class_id > Configuration.getClassesNo()) {
            return 0;
        } else {
            return class_id;
        }
    }

    //------------------------------------------------------------------------------
    protected void setGene(int no, RuleGene g) {
        this.clearEvaluations();
        this.ConditionOnAttribute.set(no, g);
    }

    /**
     * Two point crossover     indv1 [cut] indv2 
     * @param Indv1 parent1
     * @param Indv2 parent2
     * @return new individual that is combination of two indoviduals
     *
     */
    @Override
    public Individual crossoverWith(Individual Indv1) {
        Rule tym = new Rule();
        int cut = Rand.getRandomInt(Configuration.getConfiguration().getNumberOfAttributes());
        for (int i = 0; i < Configuration.getConfiguration().getNumberOfAttributes(); i++) {
            RuleGene d = null;
            if (i <= cut) {
                d = new RuleGene(((Rule) Indv1).getGene(i));
            } else {
                d = new RuleGene((this).getGene(i));
            }
            tym.setGene(i, d);
        }

        /*
        boolean classFromOne = Rand.GetRandomBoolean();
        if (classFromOne) tym.ClassID = ((Rule)Indv1).ClassID;
        else tym.ClassID = ((Rule)Indv2).ClassID;
         */

        boolean classFromOne = Rand.GetRandomBoolean();
        Rule R = null;
        if (classFromOne) {
            R = new Rule((Rule) Indv1);
        } else {
            R = new Rule((Rule) this);
        }

        for (int i = 0; i < Configuration.getConfiguration().getClassBits(); i++) {
            tym.ClassID.set(i, R.ClassID.get(i));

        }
        return tym;
    }

    /**
     * informs if Rule in RuleSet is active
     * @return true (active) of false (no active)
     */
    public boolean isActive() {
        return this.Active;
    }

    //------------------------------------------------------------------------------
    public boolean isEmpty() {
        for (int attribID = 0; attribID < Configuration.getConfiguration().getNumberOfAttributes(); attribID++) {
            if (this.ConditionOnAttribute.get(attribID).isOff() == false) {
                return false;
            }
        }
        return true;
    }

    //------------------------------------------------------------------------------
    @Override
    public int diversityMeasure(final Individual I) {
        int diff = 0;
        if (I instanceof Rule) {
            Rule R = (Rule) I;
            for (int i = 0; i < Configuration.getConfiguration().getNumberOfAttributes(); i++) {
                diff = diff + ConditionOnAttribute.get(i).Diverse(R.ConditionOnAttribute.get(i));
            }
        } else {
            throw new RuntimeException("Illegal Object!");
        }
        return diff;
    }
    //------------------------------------------------------------------------------
}

