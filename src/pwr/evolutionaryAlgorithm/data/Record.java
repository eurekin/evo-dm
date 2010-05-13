package pwr.evolutionaryAlgorithm.data;

/**
 *
 * @author pawelm
 */
public class Record {

    private int id;
    private float[] values;
    private int className;

    public int getId() {
        return id;
    }

    public Record() {
    }

    Record(int id, float[] values, int className) {
        this.id = id;
        this.values = values;
        this.className = className;
    }

    /**
     * returns value of given arguemnt ID
     * @param argId a given ID
     * @return value of selected argument
     */
    public float getArgumentValue(int argId) {
        return this.values[argId];
    }

    public int getClassName() {
        return this.className;
    }

    public int getClassNameNext() {
        return -1;
    }

    public boolean hasClass(int class_id) {
        return class_id == className;
    }

    public boolean isSatisfy(Condition c) {
        final float atr = this.values[c.getAttrib()];
        switch (c.getRelation()) {
            case IN:
                return atr >= c.getValue1() && atr <= c.getValue2();

            case NOT_IN:
                return atr < c.getValue1() || atr > c.getValue2();
        }
        return false;
    }

    public float getMaxAttribValue(int attrib) {
        return this.values[attrib];
    }

    public float getMinAttribValue(int attrib) {
        return this.values[attrib];
    }

    @Override
    public String toString() {
        StringBuilder SB = new StringBuilder();

        for (float v : values) {
            SB.append(v).append(";");
        }
        return SB.toString().replace(".", ",");
    }
}
