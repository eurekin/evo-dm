package pwr.evolutionaryAlgorithm.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import pwr.evolutionaryAlgorithm.utils.CSVFileReader.CSVLine;
import pwr.evolutionaryAlgorithm.utils.FileLineReader.FileLineIterator;

/**
 *
 * <p>Exercising file reading API. This is only a proof of concept.
 * CSV file format is too complicated and requires special parser.
 * Consult <a href=http://snippets.dzone.com/posts/show/4430">this</a>
 * for more capable one.
 *
 * <p>Example usage:
 * <pre>
 * for(CSVLine line : new CSVFileReader("results.csv")){
 *   for(String value : line) {
 *     doSomethingWith(value);
 *   }
 * }
 * </pre>
 * @author Rekin
 */
public class CSVFileReader implements Iterable<CSVLine> {

    final FileLineIterator it;
    final String delimeter;

    class CSVLineIterator implements Iterator<CSVLine> {

        @Override
        public boolean hasNext() {
            return it.hasNext;
        }

        @Override
        public CSVLine next() {
            return new CSVLine(it.next());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    @Override
    public Iterator<CSVLine> iterator() {
        return new CSVLineIterator();
    }

    public class CSVLine implements Iterable<String> {

        public List<String> values;

        private CSVLine(String s) {
            String l[] = s.split(delimeter);
            values = Arrays.asList(l);
        }

        @Override
        public Iterator<String> iterator() {
            return values.iterator();
        }
    }

    public CSVFileReader(String fileName) {
        this(fileName, ";");
    }

    public CSVFileReader(String fileName, String delimeter) {
        it = (FileLineIterator) new FileLineReader(fileName).iterator();
        this.delimeter = delimeter;
    }
}
