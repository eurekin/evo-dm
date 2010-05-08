package pwr.evolutionaryAlgorithm.data;

import java.util.ArrayList;

/**
 * Trzyma listę rekordów oraz informacje o precyzji, dokładności itp.
 * 
 * @author pawelm
 */
public class DataSet {

    private float Precision = 0;
    private float Recall = 0;
    private float Accuracy = 0;
    private float Fsc = 0;
    /**
     * @todo LinkedHashSet na iteratorach!?
     */
    private ArrayList<Record> records;

    public Record getRecord(int i) {
        return this.records.get(i);
    }

    public DataSet() {
        this.records = new ArrayList<Record>();
    }

    public DataSet(DataSet D) {
        this.records = new ArrayList<Record>(D.records);
    }

    public void removeRecord(final Record R) {
        this.records.remove(R);
    }

    public void addRecord(final Record R) {
        this.records.add(R);
    }

    public long size() {
        return this.records.size();
    }

    public boolean empty() {
        if (this.records.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void clear() {
        this.records.clear();
    }

    public boolean contains(final Record R) {
        return this.records.contains(R);
    }

    /**
     * @todo prosty kod! poprawic na linkedshaset (?)
     * @param DS1
     * @param DS2
     * @return
     */
    public static DataSet operatorPlus(final DataSet DS1, final DataSet DS2) {
        DataSet Result = new DataSet(DS1);

        //adding form DS2 if not already in DS1
        for (int e = 0; e < DS2.size(); e++) {
            Record R = DS2.records.get(e);
            if (Result.records.contains(R) == false) {
                Result.records.add(R);
            }
        }
        return Result;
    }

    /**
     * Method that looks for DataSource ang gives information about DataSet
     * (acc, prec, rec and Fsc) in given class.
     *
     * As a side effect {@code DataSet ds}'s evaluation is updated to
     * reflect computated values. Thus it's best candidate for a method
     * of DataSet class.
     */
    public void evaluate(DataSource dSrc, int classId) {
        final float rcl, prc, pPt, rPt, eSc, fSc, out, acc;
        final float alpha, expected, correct, generated;

        // get input data
        alpha = 0.5f;
        generated = size();
        expected = dSrc.getExpected(classId);
        correct = dSrc.getCorrect(this, classId).size();

        // recall & precision - corrected to handle division by zero
        rcl = expected == 0 ? 0 : correct / expected;
        prc = generated == 0 ? 0 : correct / generated;

        // E score
        pPt = prc == 0 ? 0 : alpha / prc;
        rPt = rcl == 0 ? 0 : (1 - alpha) / rcl;
        eSc = pPt + rPt == 0 ? 1 : 1 - (1 / (pPt + rPt));

        // F Score
        fSc = 1 - eSc;

        // Accuracy
        out = dSrc.size() - expected - generated + 2 * correct;
        acc = prc == 0 || rcl == 0.0 ? 0 : out / dSrc.size();

        // return
        setEvaluation(prc, rcl, acc, fSc);
    }

    /**
     * returns DS1 - DS2
     * @param DS1 DataSet1
     * @param DS2 DataSet2
     * @return DS1 - DS2
     */
    public static DataSet OperatorMinus(final DataSet DS1, final DataSet DS2) {
        DataSet Result = new DataSet(DS1);

        for (int e = 0; e < DS1.size(); e++) {
            Record R = DS1.records.get(e);
            if (DS2.records.contains(R) == true) {
                Result.records.remove(R);
            }
        }
        return Result;
    }

    @Override
    public String toString() {
        StringBuilder SB = new StringBuilder();
        for (int r = 0; r < records.size(); r++) {
            SB.append(records.get(r).toString() + ";;;\n");
        }
        return SB.toString();
    }

    public void setEvaluation(float prec, float rec, float acc, float fsc) {
        this.Fsc = fsc;
        this.Recall = rec;
        this.Accuracy = acc;
        this.Precision = prec;
    }

    public float getFsc() {
        return this.Fsc;
    }

    public float getPrecision() {
        return Precision;
    }

    public float getRecall() {
        return this.Recall;
    }

    public float getAccuracy() {
        return this.Accuracy;
    }
}

