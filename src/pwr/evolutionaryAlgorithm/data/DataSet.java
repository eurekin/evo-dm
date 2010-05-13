package pwr.evolutionaryAlgorithm.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Trzyma listę rekordów oraz informacje o precyzji, dokładności itp.
 * 
 * @author pawelm
 */
public class DataSet implements Iterable<Record> {

    private float FSc = 0f;
    private float recall = 0f;
    private float accuracy = 0f;
    private float precision = 0f;
    private final ArrayList<Record> records;

    public DataSet(int capacity) {
        records = new ArrayList<Record>(capacity);
    }

    public DataSet() {
        this(10);
    }

    public DataSet(DataSet dSet) {
        records = new ArrayList<Record>(dSet.records);
    }

    public void removeRecord(final Record R) {
        records.remove(R);
    }

    public void addRecord(final Record R) {
        records.add(R);
    }

    public long size() {
        return records.size();
    }

    public boolean empty() {
        return records.isEmpty();
    }

    public void clear() {
        records.clear();
    }

    public boolean contains(final Record R) {
        return records.contains(R);
    }

    /**
     * 
     * @param ds1
     * @param ds2
     * @return
     */
    public static DataSet operatorPlus(final DataSet ds1, final DataSet ds2) {
        DataSet result = new DataSet(ds1);
        Collection<Record> resRec = result.records;
        // using list as a set
        for (Record r : ds2.records) {
            if (!resRec.contains(r)) {
                resRec.add(r);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder SB = new StringBuilder();
        for (Record rec : records) {
            SB.append(rec.toString()).append(";;;\n");
        }
        return SB.toString();
    }

    public void setEvaluation(float prec, float rec, float acc, float fsc) {
        this.FSc = fsc;
        this.recall = rec;
        this.accuracy = acc;
        this.precision = prec;
    }

    public float getFsc() {
        return this.FSc;
    }

    public float getPrecision() {
        return precision;
    }

    public float getRecall() {
        return this.recall;
    }

    public float getAccuracy() {
        return this.accuracy;
    }

    @Override
    public Iterator<Record> iterator() {
        return records.iterator();
    }

    /**
     * Returns set of records classified correctly in a dataset
     *
     * @param classId class of classification
     * @return set of records
     */
    public int getCorrectCount(int classId) {
        int recordClass, correct = 0;
        for (Record rec : records) {
            recordClass = rec.getClassName();
            do {
                if (classId == recordClass) {
                    correct++;
                }
                recordClass = rec.getClassNameNext();
            } while (recordClass != -1); // Dla obrazków...
        }
        return correct;
    }

    /**
     * Returns set of records classified correctly in a dataset
     *
     * @param classId class of classification
     * @return set of records
     */
    public DataSet getCorrectSubset(int classId) {
        DataSet correctDS = new DataSet(records.size());
        int recordClass;
        for (Record rec : records) {
            recordClass = rec.getClassName();
            do {
                if (classId == recordClass) {
                    correctDS.addRecord(rec);
                }
                recordClass = rec.getClassNameNext();
            } while (recordClass != -1); // Dla obrazków...
        }
        return correctDS;
    }

    /**
     * Method that looks for DataSource ang gives information about DataSet
     * (acc, prec, rec and Fsc) in given class.
     *
     * @param dSrc
     * @param classId
     */
    public void evaluate(DataSource dSrc, int classId) {
        final float rcl, prc, pPt, rPt, eSc, fSc, out, acc;
        final float alpha, expected, correct, generated;

        // get input data
        alpha = 0.5f;
        generated = size();
        expected = dSrc.getExpected(classId);
        correct = getCorrectCount(classId);

        // recall & precision - corrected to handle division by zero
        rcl = expected == 0f ? 0f : correct / expected;
        prc = generated == 0f ? 0f : correct / generated;

        // E score
        pPt = prc == 0f ? 0f : alpha / prc;
        rPt = rcl == 0f ? 0f : (1f - alpha) / rcl;
        eSc = pPt + rPt == 0f ? 1f : 1f - (1f / (pPt + rPt));

        // F Score
        fSc = 1f - eSc;

        // Accuracy
        out = dSrc.size() - expected - generated + 2f * correct;
        acc = prc == 0f || rcl == 0f ? 0f : out / dSrc.size();

        // update
        setEvaluation(prc, rcl, acc, fSc);
    }

    void filter(Condition c) {
        ArrayList<Record> result = new ArrayList<Record>(records.size());
        for (Record rec : records) {
            if (rec.isSatisfy(c)) {
                result.add(rec);
            }
        }
        records.clear();
        records.addAll(result);
    }
}
