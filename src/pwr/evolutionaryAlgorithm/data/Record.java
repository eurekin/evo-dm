package pwr.evolutionaryAlgorithm.data;

/**
 *
 * @author pawelm
 */
public class Record {

    private int size;
    private float[] values;
    private int className;

    public Record() {
    }

    Record(int size, float[] values, int className) {
        this.values = values;
        this.size = size;
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

    public boolean isSatisfy(Condition C) {
        final float atr = this.values[C.getAttrib()];
        switch (C.getRelation()) {
            case IN:
                return atr >= C.getValue1() && atr <= C.getValue2();

            case NOT_IN:
                return atr < C.getValue1() || atr > C.getValue2();
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
            SB.append(v + ";");
        }
        return SB.toString().replace(".", ",");
    }
}
