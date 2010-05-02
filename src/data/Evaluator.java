package data;

import java.util.ArrayList;

import EvolutionaryAlgorithm.Configuration;
import EvolutionaryAlgorithm.Individual.*;

/*
 * Singleton class
 */
/////////////////////////////////////////////////////////////////////////////////
public class Evaluator {

    static private Evaluator e = null;
    private ArrayList<DataSet> Datas = null;
    private int IndividualPointer = 0;

//-----------------------------------------------------------------------------
    static public Evaluator getEvaluator() {
        if (e == null) {
            e = new Evaluator();
        }
        return e;
    }

//------------------------------------------------------------------------------
    /**
     * private constructor
     */
    private Evaluator() {
    }

//------------------------------------------------------------------------------
    public void evaluate(DataSource DSc, Individual I) {
        if (I instanceof RuleSet) {
            EvaluateRuleSet(DSc, (RuleSet) I);
        } else if (I instanceof Rule) {
            EvaluateRule(DSc, (Rule) I);
        }
    }

//------------------------------------------------------------------------------
    /**
     * evaluates RuleSet
     * @param DSc datasource (traint/test)
     * @param RuleSet
     * @return DataSet of all covered data
     */
    private DataSet EvaluateRuleSet(DataSource DSc, RuleSet RS) {

        DataSet DSPart = new DataSet();
        DataSet DSResult = new DataSet();

        ///////////////////////////////////////////
        /// only one class active!
        if (Configuration.getConfiguration().isOneClassActive()) {
            DSResult.clear();
            RS.clearEvaluations();

            /// for each rule....
            for (int r = 0; r < RS.RulesNo(); r++) {
                DSPart.clear();
                //if rule is active and returns such class
                if (RS.getRule(r).isActive()) {

                    DSPart = EvaluateRule(DSc, RS.getRule(r));
                    DSResult = DataSet.OperatorPlus(DSResult, DSPart);

                }
            } ///// end: for each rule
            /////// CLASS Summary ///////////////
            EvaluateDataSet(DSc, DSResult, Configuration.getConfiguration().getActiveClass());
            ///
            Evaluation E = new Evaluation(DSResult.getPrecision(), DSResult.getRecall(), DSResult.getFsc(), DSResult.getAccuracy());
            RS.setEvaluation(E);
        } // all classes are active
        //for each class....
        else {
            RS.clearEvaluations();

            ////////////////// CLASSESS ////////////////////////////////////////////////////
            for (int c = 0; c < DataLoader.getClassNumber(); c++) {
                DSPart.clear();
                DSResult.clear();

                //////////////////RULES ////////////////////////////////////////////////////
                for (int r = 0; r < RS.RulesNo(); r++) {
                    //if rule is activa and returns such class
                    if (RS.getRule(r).isActive() && RS.getRule(r).getClassID() == c) {
                        DSPart = EvaluateRule(DSc, RS.getRule(r));
                        DSResult = DataSet.OperatorPlus(DSResult, DSPart);
                    }
                }
                ////////////////END: RULES ////////////////////////////////////////////////////

                /////// CLASS Summary ///////////////
                EvaluateDataSet(DSc, DSResult, c);
                Evaluation E = new Evaluation(DSResult.getPrecision(), DSResult.getRecall(), DSResult.getFsc(), DSResult.getAccuracy());
                RS.setEvaluation(c, E);
            }//////////////////END:CLASSESS ////////////////////////////////////////////////////
        }
        RS.doCountTotalEvaluation(DataLoader.getClassNumber());
        return DSResult;
    }

//------------------------------------------------------------------------------
    private DataSet EvaluateRule(DataSource DSc, Rule R) {

        Configuration Config = Configuration.getConfiguration();
        DataSet DS = this.getCoveredDataSet(DSc, R);  //get DataSet covered by rule

        if (Config.isOneClassActive() == true) {
            EvaluateDataSet(DSc, DS, Config.getActiveClass());
        } else {
            EvaluateDataSet(DSc, DS, R.getClassID());
        }

        Evaluation E = new Evaluation(DS.getPrecision(), DS.getRecall(), DS.getFsc(), DS.getAccuracy());
        R.setEvaluation(E);

        return DS;
    }

//	------------------------------------------------------------------------------
    protected DataSet getCoveredDataSet(DataSource DS, Rule R) {
        Condition c = null;
        DataSet Cand = new DataSet();
        int attribID = 0;
        //for first review -> search for first enabled attribuite
        for (attribID = 0; attribID < Configuration.getConfiguration().getNumberOfAttributes(); attribID++) {
            if (R.isCondition(attribID) == true) {
                c = R.getCondition(attribID);

                Cand = DS.getDataSet(c);

                break;
            }
        }
        //else: there is no data -> first enabled condition and no result
        if (attribID != Configuration.getConfiguration().getNumberOfAttributes() && Cand != null) {
            int a = attribID;
            do {
                if (R.isCondition(a) == true) {
                    c = R.getCondition(a);
                    Cand = DS.getDataSet(Cand, c);
                }
                a++;
            } while (a < Configuration.getConfiguration().getNumberOfAttributes() && !Cand.empty());
        }//else

        return Cand;
    }

//	------------------------------------------------------------------------------
    private DataSet getAllCorrectClassified(DataSource DSc, DataSet DSgenerated, RuleSet RS) {

        DataSet DSpart, DScorrect = new DataSet();

        int rules = RS.RulesNo();

        for (int r = 0; r < rules; r++) {
            DSpart = DSc.getCorrect(DSgenerated, RS.getRule(r).getClassID());
            DScorrect = DataSet.OperatorPlus(DScorrect, DSpart);
        }
        return DScorrect;
    }

//	------------------------------------------------------------------------------
    public void clearBB() {
        int pop_size = Configuration.getConfiguration().getPopSize();
        if (Datas == null) {
            this.Datas = new ArrayList<DataSet>(pop_size);
        }
        this.IndividualPointer = 0;
    }

//	------------------------------------------------------------------------------
    public void EvaluateBB(DataSource DSc, Individual I) {
        DataSet DSgenerated = this.EvaluateRuleSet(DSc, (RuleSet) I);
        DataSet DScorrect = this.getAllCorrectClassified(DSc, DSgenerated, (RuleSet) I);
        this.Datas.set(this.IndividualPointer++, DScorrect);
    }

//	------------------------------------------------------------------------------
    public void CalculateBB() {
        //int pop_size = Configuration.getConfiguration().getPopSize();
        /**
         * TODO BB calculate BB stucture = DataSet [Sum] -> corrected 1/n
         */
    }

//	------------------------------------------------------------------------------
    public void UpdateFitnessBB(Individual I, int IndvNo) {
        /**
         * TODO BB update fitness
         */
    }

//	------------------------------------------------------------------------------
    /**
     * Method that looks for DataSource ang gives information about DataSet (acc, prec, rec and Fsc) in given class
     */
    protected float EvaluateDataSet(DataSource DSc, DataSet DS, int class_id) {

        /**
         * TODO: Beta PARAMETER
         */
        //float Beta = 1f; // 1 - no changes
        //float Alfa = 1/(Beta*Beta+1);
        float Alfa = 0.5f;

        float expected = DSc.getExpected(class_id);
        float correct = DSc.getCorrect(DS, class_id).elements();
        float generated = DS.elements();

        double recall;
        if (expected != 0) {
            recall = correct / expected;
        } else {
            recall = 0;
        }
        double precision;
        if (generated != 0) {
            precision = correct / generated;
        } else {
            precision = 0;
        }

        double Emeasure, ppart, rpart;
        if (precision == 0) {
            ppart = 0;
        } else {
            ppart = Alfa / precision;
        }

        if (recall == 0) {
            rpart = 0;
        } else {
            rpart = (1 - Alfa) / recall;
        }


        float accuracy = 0;
        if (precision != 0 && recall != 0.0) {
            float out = (float) DSc.size() - ((expected - correct) + (generated - correct)) - correct;
            accuracy = (correct + out) / DSc.size();
        }

        if (ppart == 0 && rpart == 0) {
            Emeasure = 1;
        } else {
            Emeasure = 1 - (1 / (ppart + rpart));
        }

        double Fmeasure = 1 - Emeasure;

        /*TODO: experiment PREC*REC*/
        //Fmeasure = precision*recall;

        DS.setEvaluation((float) precision, (float) recall, (float) accuracy, (float) Fmeasure);

        if (Configuration.getConfiguration().isFsc() == true) {
            return (float) Fmeasure;
        } else {
            return accuracy;
        }
        //return accuracy;
    }

//  ------------------------------------------------------------------------------
    /**
     * returns classification report for selectet rule
     * @param DSc datasource
     * @param DS dataset of generated data
     * @param R Selected rule
     * @see FullClassificationReport
     */
    protected String ClassificationReport(DataSource DSc, DataSet DS, Rule R) {
        StringBuilder SB = new StringBuilder();

        SB.append("\n" + R.toString());

        long correct = DSc.getCorrect(DS, R.getClassID()).elements();
        long expected = 0;
        if (Configuration.getConfiguration().isOneClassActive() == true) {
            expected = DSc.getExpected(Configuration.getConfiguration().getActiveClass());
        } else {
            expected = DSc.getExpected(R.getClassID());
        }
        long generated = DS.elements();

        SB.append("\n Generated=" + generated);
        SB.append(" Correct=" + correct);
        SB.append(" Expected=" + expected);
        SB.append(" InCorrect=" + (generated - correct));

        long elements = DS.elements();
        for (int e = 0; e < elements; e++) {
            if (!DS.getRecord(e).hasClass(R.getClassID())) {
                SB.append("\n---" + DS.getRecord(e).toString());
            }
        }
        return SB.toString();
    }

    //------------------------------------------------------------------------------
    /**
     * Return as string Report of classification of selected RuleSet
     * @param DS dataSource (Train or Test) as dataScurce
     * @return string for report
     */
    public String FullClassificationReport(DataSource DSc, RuleSet RS, String Text) {

        StringBuilder SB = new StringBuilder();

        DataSet DSPart = new DataSet();
        DataSet DSResult = new DataSet();

        ///////////////////////////////////////////
        /// only one class active!
        if (Configuration.getConfiguration().isOneClassActive()) {
            DSResult.clear();
            RS.clearEvaluations();
            /// for each rule....
            for (int r = 0; r < RS.RulesNo(); r++) {
                DSPart.clear();
                //if rule is active and returns such class
                if (RS.getRule(r).isActive()) {
                    DSPart = EvaluateRule(DSc, RS.getRule(r));
                    SB.append(this.ClassificationReport(DSc, DSPart, RS.getRule(r)));
                    DSResult = DataSet.OperatorPlus(DSResult, DSPart);
                }
            } ///// end: for each rule
            /////// CLASS Summary ///////////////
            EvaluateDataSet(DSc, DSResult, Configuration.getConfiguration().getActiveClass());
            ///
            Evaluation E = new Evaluation(DSResult.getPrecision(), DSResult.getRecall(), DSResult.getFsc(), DSResult.getAccuracy());
            RS.setEvaluation(E);
        } // all classes are active
        //for each class....
        else {
            RS.clearEvaluations();
            ////////////////// CLASSESS ////////////////////////////////////////////////////
            for (int c = 0; c < DataLoader.getClassNumber(); c++) {
                DSPart.clear();
                DSResult.clear();

                if (DSc.getExpected(c) == 0) {
                    SB.append("=> NO INSTANCES");
                }

                //////////////////RULES ////////////////////////////////////////////////////
                for (int r = 0; r < RS.RulesNo(); r++) {
                    //if rule is activa and returns such class
                    if (RS.getRule(r).isActive() && RS.getRule(r).getClassID() == c) {
                        DSPart = EvaluateRule(DSc, RS.getRule(r));

                        SB.append(this.ClassificationReport(DSc, DSPart, RS.getRule(r)));

                        DSResult = DataSet.OperatorPlus(DSResult, DSPart);
                    }
                }
                ////////////////END: RULES ////////////////////////////////////////////////////

                /////// CLASS Summary ///////////////
                EvaluateDataSet(DSc, DSResult, c);
                Evaluation E = new Evaluation(DSResult.getPrecision(), DSResult.getRecall(), DSResult.getFsc(), DSResult.getAccuracy());
                RS.setEvaluation(c, E);
            }//////////////////END:CLASSESS ////////////////////////////////////////////////////
        }
        RS.doCountTotalEvaluation(DataLoader.getClassNumber());

        SB.append("\n############################ " + Text + "############################\n");
        SB.append("\n" + RS.toString());
        SB.append("\n\n" + Text + "_DATASOURCE  " + DSc.toString());
        SB.append("\n##################################################################\n");

        return SB.toString();
    }
//------------------------------------------------------------------------------
}
