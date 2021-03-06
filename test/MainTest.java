
import pl.eurekin.coevolution.ComparingPrintStream;
import java.io.PrintStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pwr.evolutionaryAlgorithm.utils.FileLineReader;
import static org.junit.Assert.*;

/**
 *
 * @author Rekin
 */
public class MainTest {

    public static final String TEMPLATE_FILE =
            "reference.xorshiftrng.output.template2.txt";

    public MainTest() {
    }

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
    public void testMain() {
        System.out.println("main");
        final PrintStream oldOut = System.out;
        final FileLineReader properOutput =
                new FileLineReader(TEMPLATE_FILE);
        System.setOut(new ComparingPrintStream(oldOut, properOutput));
        Main.main(null);
    }
}
