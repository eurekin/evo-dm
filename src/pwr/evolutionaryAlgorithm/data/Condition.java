package pwr.evolutionaryAlgorithm.data;

/**
 *
 * @author pawelm
 */
public class Condition {

    private final float value1;
    private final float value2;
    private final int atribID;

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
        return atribID;
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
    public Condition(int atribID, RelationType r, float value1, float value2)  {

        /*
        if (atribID < 0 || atribID > Configuration.getConfiguration().getNumberOfAttributes()) {
            throw new RuntimeException("Illegal attributes number! " + Integer.toString(atribID) + " ?");
        }
        if (value1 < 0.0f || value1 > 1.0f || value2 < 0.0f || value2 > 1.0f) {
            throw new RuntimeException("Illegal types! ?" + Float.toString(value1) + " ?" + Float.toString(value1));
        }*/

        this.atribID = atribID;
        this.relation = r;

        final float argMax = DataLoader.getArgMax(atribID);
        final float argMin = DataLoader.getArgMin(atribID);

        //rescale from <0.0 , 1.0> to <min, max>
        this.value1 = value1 * (argMax - argMin) + argMin;
        this.value2 = value2 * (argMax - argMin) + argMin;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("");
        if (this.atribID == -1) {
            s.append(relation).append("<").append(String.format("%.3f", value1)).append(";").append(String.format("%.3f", value2)).append(">");
        } else {
            s.append("").append(Integer.toString(this.atribID)).append(" ").append(relation).append("<").append(String.format("%.3f", value1)).append(";").append(String.format("%.3f", value2)).append(">");
        }
        return s.toString();
    }
}
