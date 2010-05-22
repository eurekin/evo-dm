/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.eurekin.coevolution;

import java.io.PrintStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
//import static org.junit.Assert.*;
import pwr.evolutionaryAlgorithm.utils.FileLineReader;

/**
 *
 * @author Rekin
 */
public class CoevolutionnTest {

    public CoevolutionnTest() {
    }

    public static final String TEMPLATE_FILE =
            "reference.xorshiftrng.output.template3.txt";

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of main method, of class Main.
     */
    @Test
    public void testCoevolutionn() {
        System.out.println("coevolutionn");
        final PrintStream oldOut = System.out;
        final FileLineReader properOutput =
                new FileLineReader(TEMPLATE_FILE);
        System.setOut(new ComparingPrintStream(oldOut, properOutput));
        Coevolutionn.main(null);
    }

}