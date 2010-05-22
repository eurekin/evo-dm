package pl.eurekin.coevolution;


import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import pwr.evolutionaryAlgorithm.utils.FileLineReader;
import static org.junit.Assert.*;

public class ComparingPrintStream extends PrintStream {

    private final Iterator<String> proper;

    public ComparingPrintStream(OutputStream out, FileLineReader properOutput) {
        super(out);
        proper = properOutput.iterator();
    }

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
        } else if (expected.contains(", czas : ") || expected.contains("iegu g\u0142\u00f3wnego: ")) {
            return "";
        } else {
            return expected;
        }
    }
}
