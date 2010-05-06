package pwr.evolutionaryAlgorithm.data;

import pwr.evolutionaryAlgorithm.Configuration;

/**
 *
 * @author pawelm
 */
public class Condition {

    private float value1 = 0.0f;
    private float value2 = 0.0f;
    private int atribID = 0;

    /**
     * @todo code in simple form -> only for Relation in {IN, NOT_IN}
     */
    public enum RelationType {
        /*MORE ,                           // x > float 0
        LESS ,                          //x < float 1
        MORE_OR_EQUAL,        			 // x >= float 2
        LESS_OR_EQUAL,          		 // x <= float 3
        NOT_EQUAL,                 		 // x != float 4
        EQUAL,                           // x == float 5*/

        IN, // x in <float, float> 6
        NOT_IN;                          //x not in <float, float> 7
    };
    private RelationType relation;

    public int getAttrib() {
        return this.atribID;
    }

    public RelationType getRelation() {
        return relation;
    }

    public float getValue2() {
        return value2;
    }

    public float getValue1() {
        return value1;
    }

    /**
     * Builds Condition from
     * @param atribID
     * @param r relation based on enum type form RelationType
     * @param value1 float value <0.0 , 1.0 >
     * @param value2 float value <0.0 , 1.0 >
     */
    public Condition(int atribID, RelationType r, float value1, float value2) throws Exception {

        if (atribID < 0 || atribID > Configuration.getConfiguration().getNumberOfAttributes()) {
            throw new Exception("Illegal attributes number! " + Integer.toString(atribID) + " ?");
        }
        this.atribID = atribID;
        this.relation = r;
        if (value1 < 0.0f || value1 > 1.0f || value2 < 0.0f || value2 > 1.0f) {
            throw new Exception("Illegal types! ?" + Float.toString(value1) + " ?" + Float.toString(value1));
        }

        //rescale from <0.0 , 1.0> to <min, max>
        this.value1 = value1 * (DataLoader.getArgMax(atribID) - DataLoader.getArgMin(atribID)) + DataLoader.getArgMin(atribID);
        this.value2 = value2 * (DataLoader.getArgMax(atribID) - DataLoader.getArgMin(atribID)) + DataLoader.getArgMin(atribID);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("");
        if (this.atribID == -1) {
            s.append(relation + "<" + String.format("%.3f", value1) + ";" + String.format("%.3f", value2) + ">");
        } else {
            s.append("" + Integer.toString(this.atribID) + " " + relation + "<" + String.format("%.3f", value1) + ";" + String.format("%.3f", value2) + ">");
        }
        return s.toString();
    }
}
