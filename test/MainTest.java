
import java.util.Iterator;
import java.io.PrintStream;
import java.util.Arrays;
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

    public static final String TEMPLATE_FILE = "reference.xorshiftrng.output.template2.txt";

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
        System.setOut(new PrintStream(oldOut) {

            private final Iterator<String> proper =
                    properOutput.iterator();

            @Override
            public void print(String s) {
                test(s);
                //super.print(s);
            }

            @Override
            public void println(String s) {
                test(s);
                //super.println(s);
            }

            private void test(String s) {
                String[] lines = s.split("\n");
                String expected;
                for (String line : Arrays.asList(lines)) {
                    if (line.trim().length() == 0) {
                        continue;
                    }
                    expected = proper.next().trim();
                    String got = removeTimes(line).trim();
                    expected = removeTimes(expected).trim();
                    assertEquals("line mismatch", expected, got);
                }
            }

            private String removeTimes(String expected) {
                if (expected.matches(".*time=.....s")) {
                    return expected.substring(0, expected.length() - 11);
                } else if (expected.contains(", czas : ")
                        || expected.contains("iegu głównego: ")) {
                    return "";
                } else {
                    return expected;

                }

            }
        });
        Main.main(null);
    }
}
