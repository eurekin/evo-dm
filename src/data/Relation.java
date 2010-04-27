package data;


/**
 *
 * @author pawelm
 */
public interface Relation {
     int
            MORE =0,                            // x > float
             LESS= 1,                           //x < float
            MORE_OR_EQUAL=2,    // x >= float
            LESS_OR_EQUAL=3,        // x <= float
            NOT_EQUAL=4,                // x != float
            EQUAL=5,                        // x == float
            IN=6,                               // x in <float, float>
            NOT_IN=7;                      //x not in <float, float>
}
