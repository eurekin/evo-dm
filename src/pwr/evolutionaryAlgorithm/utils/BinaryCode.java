/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pwr.evolutionaryAlgorithm.utils;

import java.util.BitSet;

/**
 *
 * @author pawelm
 */
public class BinaryCode {

    static public int getFloatFromBinary(BitSet b) {
        int value = 0;
        for (int i = 0; i < b.length(); i++) {
            if (b.get(i)) {
                value += 1 << i;
            }
//             Math.pow(2.0f, i );
        }
        return value;
    }

    //----------------------------------------------------------------------------------------------------------
    public static BitSet FloatToGray(float f, int bits) {

        //int max_value = Math.pow(bits, 2);

        BitSet B = new BitSet(bits);
        String s = Float.toString(f);
        s.replace(",", ".");
        //String value = Integer.toBinaryString(Float.floatToRawIntBits(Float.parseFloat(s)));

        /**
         * todo insert code here
         */
        return B;
    }

    //----------------------------------------------------------------------------------------------------------
    public static int GrayToFloat(BitSet B) {
        BitSet Bresult = new BitSet(B.length());
        Bresult.set(0, B.get(0));
        for (int i = 1; i < B.length(); i++) {
            boolean v = Bresult.get(i - 1) ^ B.get(i);
            Bresult.set(i, v);
        }
        return getFloatFromBinary(B);
    }

    private BinaryCode() {
    }
    //----------------------------------------------------------------------------------------------------------
}
