package pwr.evolutionaryAlgorithm.individual;

import java.util.ArrayList;
import java.util.Iterator;
import pl.eurekin.coevolution.ClassifyingIndividual;
import pl.eurekin.coevolution.Selector;
import pl.eurekin.util.IterableFilter;
import pwr.evolutionaryAlgorithm.utils.Rand;
import pwr.evolutionaryAlgorithm.data.DataLoader;
import pwr.evolutionaryAlgorithm.data.Evaluation;
import pwr.evolutionaryAlgorithm.Configuration;
import pwr.evolutionaryAlgorithm.Configuration.CrossoverType;
import pwr.evolutionaryAlgorithm.data.DataSet;
import pwr.evolutionaryAlgorithm.data.DataSource;
import pwr.evolutionaryAlgorithm.data.Evaluator;

/**
 *
 * @author pawelm
 */
public class RuleSet extends Individual implements Iterable<Rule> {

    /*
     * TODO: remove this const form here!
     */
    private static boolean fixedLength = false;
    private final int classesNo = Configuration.getClassesNo();
    private ArrayList<Rule> rules;
    private final Configuration config = Configuration.getConfiguration();
    private final CrossoverType crossoverType = config.getCrossoverType();
    private final boolean oneClassActive = config.isOneClassActive();
    private final int activeClass = config.getActiveClass();
    private final int classNo = DataLoader.getClassNumber();
    private Evaluation totalEvaluation;

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public RuleSet(final RuleSet RS) {
        rules = new ArrayList<Rule>();
        for (int i = 0; i < RS.rulesNo(); i++) {
            rules.add(new Rule(RS.rules.get(i)));
        }

        totalEvaluation = new Evaluation(RS.totalEvaluation);
        evaluations = new ArrayList<Evaluation>();
        if (config.isOneClassActive()) {
            evaluations.add(new Evaluation(RS.getEvaluation()));
        } else {
            for (int cl = 0; cl < classesNo; cl++) {
                this.evaluations.add(RS.getEvaluation(cl));
            }
        }
    }

    public RuleSet() {
        rules = new ArrayList<Rule>();
        totalEvaluation = new Evaluation();
        evaluations = new ArrayList<Evaluation>();
        if (config.isOneClassActive()) {
            evaluations.add(new Evaluation());
        } else {
            for (int cl = 0; cl < classesNo; cl++) {
                evaluations.add(new Evaluation());
            }
        }
    }

    @Override
    public void init() {
        this.rules.clear();

        int RULES = Rand.getRandomInt(config.getNumberOfRules()) + 1;

        if (!fixedLength) {
            RULES = config.getNumberOfRules();
        }

        for (int i = 0; i < RULES; i++) {
            Rule r = new Rule();
            this.rules.add(r);
            this.rules.get(i).init();
        }

        this.clearEvaluations();
    }

    @Override
    public Evaluation getEvaluation() {
        return totalEvaluation;
    }

    public Evaluation getTotalEvaluation() {
        return totalEvaluation;
    }

    /**
     * Mogę użyć do koewolucji bez zmian.
     *
     * do average value of all classes -> without unused (or not tested) class
     * @param classess number of classes
     */
    public void doCountTotalEvaluation(int classess) {
        totalEvaluation.clear();
        int usedClasses = classess;
        //for each class
        for (int cl = 0; cl < classess; cl++) {
            if (!evaluations.get(cl).isDone()) {
                usedClasses--;
            } else {
                totalEvaluation.update(evaluations.get(cl));
            }
        }
        totalEvaluation.doAverage(usedClasses);
    }

    @Override
    protected int getGenesInIndividual() {
        return this.rulesNo();
    }

    @Override
    public Individual mutate() {
        RuleSet rs = new RuleSet();
        for (int i = 0; i < this.rules.size(); i++) {
            rs.rules.add(i, (Rule) this.rules.get(i).mutate());
        }
        return rs;
    }

    @Override
    public void clearEvaluations() {
        totalEvaluation.clear();
        super.clearEvaluations();
        for (int i = 0; i < this.rulesNo(); i++) {
            this.rules.get(i).clearEvaluations();
        }
    }

    /**
     *BCX: Best Class crossoverWith
     */
    private static Individual crossoverBCX(Individual ind1, Individual ind2) {

        /**
         * @TODO reczna zmiana Fixed lenght
         */
        fixedLength = true;

        RuleSet p1 = (RuleSet) ind1, p2 = (RuleSet) ind2;
        RuleSet fittest = null, Offspring = new RuleSet();
        float f1, f2;

        for (int cl = 0; cl < DataLoader.getClassNumber(); cl++) {
            f1 = p1.evaluations.get(cl).getFitness();
            f2 = p2.evaluations.get(cl).getFitness();
            fittest = f1 > f2 ? p1 : p2;

            // find
            for (Rule rl : fittest.rules) {
                //if rule is activa and returns such class
                if (rl.isActive() && rl.getClassID() == cl) {
                    Offspring.rules.add(new Rule(rl));
                }
            }
        }
        return Offspring;
    }

    @Override
    public Individual crossoverWith(Individual other) {

        if (crossoverType == CrossoverType.BCX) {
            return crossoverBCX(this, other);
        } else {
            return crossoverSimpleCut(this, other);
        }

    }

    /**
     * Random cut gives one individual as offsping
     * Chromosome FIXED lenght updated 
     * 
     */
    private static Individual crossoverSimpleCut(Individual mother, Individual father) {

        RuleSet rs = new RuleSet();
        RuleSet p1 = (RuleSet) mother;
        RuleSet p2 = (RuleSet) father;

        int MAX = p1.rulesNo();
        int cut;
        boolean biggerP2 = false;


        if (fixedLength && p1.rulesNo() < p2.rulesNo()) {
            cut = Rand.getRandomInt(p1.rulesNo());
            biggerP2 = true;
        } else {
            cut = Rand.getRandomInt(p2.rulesNo());
        }

        Rule d;
        for (int i = 0; i < MAX; i++) {
            d = null;
            if (biggerP2) {
                if (i <= cut) {
                    d = new Rule(p1.rules.get(i));
                } else {
                    d = new Rule(p2.rules.get(i));
                }
            } else {
                if (i <= cut) {
                    d = new Rule(p2.rules.get(i));
                } else {
                    d = new Rule(p1.rules.get(i));
                }
            }
            rs.rules.add(i, d);
        }
        return rs;
    }

    public Rule getRule(int ruleID) {
        return this.rules.get(ruleID);
    }

    public int rulesNo() {
        return this.rules.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n RULESET_EVAL");
        sb.append("\n TOTAL ").append(totalEvaluation.toString());
        ///classes evaluations...
        if (config.isOneClassActive()) {
            sb.append(evaluations.get(0).toString());
        } else {
            sb.append("\n classes ");
            for (int cl = 0; cl < DataLoader.getClassNumber(); cl++) {
                sb.append("\n cl_").append(cl);
                sb.append(" [").append(evaluations.get(cl)).append("]");
            }
        }

        sb.append("\n\n RULES");
        for (Rule rule : rules) {
            sb.append("\n ").append(rule);
        }

        return sb.toString();
    }

    /**
     * returns a positive number of contained rules comparision
     * @param ind
     * @return
     */
    @Override
    public int diversityMeasure(final Individual ind) {
        int diff = 0;
        if (ind instanceof RuleSet) {
            RuleSet RS = (RuleSet) ind;
            for (int i = 0; i < this.rulesNo(); i++) {
                diff += this.rules.get(i).diversityMeasure(RS.rules.get(i));
            }
        } else {
            throw new RuntimeException("Illegal Object is given!");
        }
        return diff;
    }

    @Override
    public Iterator<Rule> iterator() {
        return rules.iterator();
    }

    public Iterable<Rule> forClass(final int c) {
        return new IterableFilter<Rule>(this) {

            @Override
            public boolean passes(Rule rule) {
                return rule.getClassID() == c;
            }
        };
    }

    public void evaluate(DataSource dSrc, Selector sel) {
        clearEvaluations();
        Evaluation eval;
        if (oneClassActive) {
            eval = evaluateSingleClass(this, dSrc, activeClass, sel);
            setEvaluation(eval);
        } else {
            for (int c = 0; c < classNo; c++) {
                eval = evaluateSingleClass(forClass(c), dSrc, c, sel);
                setEvaluation(c, eval);
            }
        }
        doCountTotalEvaluation(classNo);
    }

    // // // //
    @Override
    public void evaluate(DataSource dSrc) {
        evaluate(dSrc, null);

    }

    private Evaluation evaluateSingleClass(Iterable<Rule> rules,
            DataSource dSrc, int c, Selector sel) {
        DataSet result = new DataSet();
        for (Rule rule : onlyActive(rules)) {
            result.addAll(rule.getCoveredDataSet(dSrc));
        }
        return evaluateDataSet(result, dSrc, c, sel);
    }

    /**
     * <p>The root evaluation method. The plumbing methods go over
     * each class, every rule set and so on and uses this method
     * in the end.
     *
     * <p>Override to provide specialized evaluation capabilities.
     *
     * @param toAccept data set
     * @param dSrc data source
     * @param c class
     * @return Evaluation of the data set for class c using dSrc
     */
    private Evaluation evaluateDataSet(DataSet toAccept,
            DataSource dSrc, int c, Selector sel) {
        if (sel != null) {
            DataSet accepted = sel.filter(toAccept);
            return accepted.evaluate(dSrc, c, sel.count(dSrc, c));
        } else {
            return toAccept.evaluate(dSrc, c);
        }

    }

    // Helpers
    @Override
    public Individual getACopy() {
        return new RuleSet(this);
    }

    private static Iterable<Rule> onlyActive(Iterable<Rule> rules) {
        return new IterableFilter<Rule>(rules) {

            @Override
            public boolean passes(Rule object) {
                return object.isActive();
            }
        };
    }

    ////// JUNK goes here
    /**
     * evaluates RuleSet using specified subset of DataSource
     * @param dSrc datasource (train/test)
     * @param sl
     * @param ci
     * @return DataSet of all covered data
     * @deprecated keeping only for historical reasons
     */
    @Deprecated
    private DataSet evaluateRuleSetUsingSelector(
            DataSource dSrc,
            Selector si,
            ClassifyingIndividual ci) {

        DataSet DSetResult = new DataSet(), DSetPart;
        Evaluator evl = Evaluator.getEvaluator();

        ci.clearEvaluations();
        ///////////////////////////////////////////
        /// only one class active!
        if (config.isOneClassActive()) {
            DSetResult.clear();

            /// for each rule....
            for (Rule rule : ci.getRules()) {
                //if rule is active and returns such class
                if (rule.isActive()) {
                    DSetPart = evl.getCoveredDataSet(dSrc, rule);
                    DSetResult = DataSet.operatorPlus(DSetResult, DSetPart);
                }
            } ///// end: for each rule
            /////// CLASS Summary ///////////////
            Evaluation evl2 = DSetResult.evaluate(dSrc, activeClass);
            ci.setEvaluation(evl2);
        } else {
            // all classes are active
            //for each class....
            ////////////////// CLASSESS ////////////////////////////////////////
            for (int c = 0; c < DataLoader.getClassNumber(); c++) {
                DSetResult.clear();

                // Z punktu widzenia implementacji koewolucji najważniejszy
                // jest moment kiedy mogę odfiltrować rekordy. Zostawić tylko
                // te, które wybrane są przez osobnika wybierającego. Problem
                // w tym, żeby zachować poprawność oceniania populacji. Gdzieś
                // trzeba zmodyfikować ocenę, gdyż w koewolucji osobnik klasy-
                // fikujący widzi tylko podzbiór danych.

                // Fill DSetResult with appropriate racords related to class c
                for (Rule rule : ci.getRules()) {
                    //if rule is active and returns such class
                    if (rule.isActive() && rule.getClassID() == c) {
                        // niejawnie (poprzez regułę) zawarta jest
                        // też informacja o wybranej klasie (c)
                        DSetPart = evl.getCoveredDataSet(dSrc, rule);
                        DSetResult = DataSet.operatorPlus(DSetResult, DSetPart);
                    }
                }
                ////////////////END: RULES /////////////////////////////////////

                // Po poprzednich krokach posiadamy DataSet (DSetResult),
                // który zawiera rekordy zebrane dla klasy c.

                // Należy odfiltrować te, których nie wybrał osobnik
                // wybierający.
                // DSetResult = filterUsingSelectingIndividual(si, DSetResult);

                Evaluation evl2 = DSetResult.evaluate(dSrc, c);
                ci.setEvaluation(c, evl2);
            }//////////////////END:CLASSESS ////////////////////////////////////
        }

        // do average value of all classes -> without unused (or not tested)
        // class. Pozostało jedynie policzenie średniej wartości, ze
        // wszystkich klas.
        ci.doCountTotalEvaluation(DataLoader.getClassNumber());
        return DSetResult;
    }
}
