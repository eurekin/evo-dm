package pwr.evolutionaryAlgorithm.data;

import java.util.*;
import pwr.evolutionaryAlgorithm.utils.Rand;

public class DataSource {

    private ArrayList<Record> data;
    private long dataExpectedByClass[];
    /**
     * INDEX. Could be cleanly abstracted as a list of DataIndex of some sort.
     */
    private ArrayList<ArrayList<Linker>> index;

    public class Linker {

        public Linker(float v_, Record R_) {
            v = v_;
            R = R_;
        }

        public int compareTo(float key) {
            if (this.v > key) {
                return 1;
            } else if (this.v < key) {
                return -1;
            }
            return 0;
        }
        public float v;
        public Record R;
    }

    class comparator implements Comparator<Linker> {

        @Override
        public int compare(Linker o1, Linker o2) {
            if (o1.v > o2.v) {
                return 1;
            } else if (o1.v < o2.v) {
                return -1;
            }
            return 0;
        }
    }

    /**
     * BinarySearch with range modyfications
     * @param attrib number of attribute (0..N)
     * @param key value to search
     * @param left if there is a few record with the same value we return the left (first) or right (last) one
     * @return position in table where key should be placed
     */
    private int binarySearch(int attrib, float key, boolean left) {
        int low = 0;
        final ArrayList<Linker> idx = index.get(attrib);
        int high = idx.size() - 1;
        int mid = 0;

        if (key < idx.get(0).v) {
            return 0;
        }
        if (key > idx.get(high).v) {
            return high;
        }

        while (low <= high) {
            mid = (low + high) / 2;

            Linker L = idx.get(mid);
            if (L.compareTo(key) < 0) {
                low = mid + 1;
            } else if (L.compareTo(key) > 0) {
                high = mid - 1;
            } else {
                break;
            }
            //   return mid;
        }
        //if (mid!=0 || mid!=INDEX.get(attrib).size()-1) return mid;
        //else return -1;     // NOT_FOUND = -1

        if (mid > 1 && idx.get(mid).v == key) {
            if (mid > 0 && left) {
                while (true) {
                    if (mid > 0) {
                        if (idx.get(mid).v == idx.get(mid - 1).v) {
                            mid--;
                        } else {
                            return mid;
                        }
                    } else {
                        return mid;
                    }
                }
            }

            if (!left && mid < idx.size() - 1 && mid > 1) {
                while (true) {
                    if (mid < idx.size() - 1) {
                        if (idx.get(mid).v == idx.get(mid + 1).v) {
                            mid++;
                        } else {
                            return mid;
                        }
                    } else {
                        return mid;
                    }
                }
            }
        }

        return mid;
    }

    /**
     * main constructor of DataSource class
     * @param TrainDataFileName name of file with data for training
     * @param TestDataFileName name of file with data for tests
     */
    public DataSource() {
        data = new ArrayList<Record>();
    }

    public DataSource(DataSource ds) {
        data = ds.data != null ? ds.data : new ArrayList<Record>();

        if (ds.index != null) {
            index = new ArrayList<ArrayList<Linker>>(ds.index);
        }

        if (ds.dataExpectedByClass != null) {
            int classes = DataLoader.getClassNumber();
            dataExpectedByClass = new long[classes];
            for (int i = 0; i < classes; i++) {
                dataExpectedByClass[i] = ds.dataExpectedByClass[i];
            }
        } else {
            dataExpectedByClass = null;
        }
    }

    public boolean addRecord(Record r) {
        return (data.add(r));
    }

    public Record removefirstRecord() {
        if (data.size() < 1) {
            return null;
        } else {
            return data.remove(0);
        }
    }

    public Record removeRandomRecord() {
        return data.remove(Rand.getRandomInt(data.size()));
    }

    public void clear() {
        if (data != null) {
            data.clear();
        }
        dataExpectedByClass = null;
        if (index != null) {
            index.clear();
        }
    }

    public int size() {
        return data.size();
    }

    public Record get(int i) {
        return data.get(i);
    }

    /**
     * Initializes "data expected by class" statistic and creates index.
     *
     * method of data organization into collections
     */
    public void OrganizeData() {

        //////////////////////////////////////////////////////////////////////////////////////////
        //Expected - for each class
        dataExpectedByClass = new long[DataLoader.getClassNumber()];
        for (int i = 0; i < DataLoader.getClassNumber(); i++) {
            dataExpectedByClass[i] = 0;
        }

        int class_id = 0;
        int class_name = -1;
        Record rec;
        for (int r = 0; r < data.size(); r++) { //for each record

            rec = data.get(r);
            if (rec instanceof RecordImage) {
                class_name = rec.getClassName();
                do {
                    class_id = class_name;
                    dataExpectedByClass[class_id]++;
                    class_name = ((RecordImage) rec).getClassNameNext();
                } while (class_name != -1);
            } else {
                class_id = rec.getClassName();
                dataExpectedByClass[class_id]++;
            }
        }

        index = new ArrayList<ArrayList<Linker>>(DataLoader.getArgumentsNo());
        for (int i = 0; i < DataLoader.getArgumentsNo(); i++) { //for each indekser...

            index.add(i, new ArrayList<Linker>(data.size())); //create it

            for (int r = 0; r < data.size(); r++) { //add each record

                rec = data.get(r);
                if (rec instanceof Record) {
                    index.get(i).add(r, new Linker(rec.getArgumentValue(i), rec));
                } else { //add each segment
                    float value = rec.getArgumentValue(i);
                    do {
                        index.get(i).add(r, new Linker(value, rec));
                        value = ((RecordImage) (rec)).getArgumentValueNext(i);
                    } while (value != -1);
                }
            }
            ///sorting
            Collections.sort(index.get(i), new comparator());
        }

    }

    /**
     *
     * @param c condition
     * @return returns size from datasource that condition is succeed
     */
    public DataSet getDataSet(Condition c) {
        DataSet res = new DataSet();

        final float cv1 = c.getValue1();
        final float cv2 = c.getValue2();
        final int attrib = c.getAttrib();
        final int p1 = binarySearch(attrib, cv1, true);
        int p2 = binarySearch(attrib, cv2, false);
        if (p2 == -1) {
            p2 = data.size() - 1;
        }
        final ArrayList<Linker> idx = index.get(attrib);
        final Linker l1 = idx.get(p1);
        final Linker l2 = idx.get(p2);

        if (c.getRelation() == Condition.RelationType.IN) {
            if (l1.v >= cv1 && l1.v <= cv2) {
                res.addRecord(l1.R);
            }
            if (p1 < p2) {
                for (int r = p1 + 1; r < p2; r++) {
                    res.addRecord(idx.get(r).R);
                }
            }
            if (l2.v >= cv1 && l2.v <= cv2 && p1 != p2) {
                res.addRecord(l2.R);
            }
        } else { //not_in
            if (p1 > 0) {
                for (int r = 0; r < p1; r++) {
                    if (idx.get(r).v < cv1) {
                        res.addRecord(idx.get(r).R);
                    }
                }
            }
            if (l1.v < cv1) {
                res.addRecord(l1.R);
            }
            Linker rec;
            if (p2 + 1 < data.size()) {
                for (int r = p2 + 1; r < data.size(); r++) {
                    rec = idx.get(r);
                    if (rec.v > cv1 && rec.v > cv2) {
                        res.addRecord(rec.R);
                    }
                }
            }
            if (l2.v > cv2) {
                res.addRecord(l2.R);
            }
        }
        return res;
    }

    public DataSet getDataSet(DataSet ds, Condition c) {
        DataSet result = new DataSet((int) ds.size());
        for (Record rec : ds) {
            if (rec.isSatisfy(c)) {
                result.addRecord(rec);
            }
        }
        return result;
    }

    public long getExpected(int classID) {
        return dataExpectedByClass[classID];
    }

    /**
     * Returns set of records classified correctly in a dataset
     * 
     * @param dSet set of dataset
     * @param classId class of classification
     * @return set of records
     */
    public DataSet getCorrect(DataSet ds, int classId) {
        DataSet correctDS = new DataSet();
        int recordClass;
        for (Record rec : ds) {
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
     * returns report
     * RECORDS_TOTAL
     * RECORDS_IN_CLASS
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n RECORDS=" + data.size() + "  Records_in_class ");
        int classes = DataLoader.getClassNumber();
        for (int i = 0; i < classes; i++) {
            sb.append(" c" + i + " [" + dataExpectedByClass[i] + "] ");
        }
        return sb.toString();
    }
}

