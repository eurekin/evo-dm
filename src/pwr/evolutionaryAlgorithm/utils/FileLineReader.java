/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pwr.evolutionaryAlgorithm.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * <p>File reading boilerplate encapsulator.
 *
 * <p>Example usage:
 * <pre>
 *  for(String line : new FileLineReader("input.txt") {
 *    System.out.println(line);
 *  }
 * </pre>
 *
 * @author Rekin
 */
public class FileLineReader implements Iterable<String> {

    String fName;

    public FileLineReader(String fName) {
        this.fName = fName;
    }

    class FileLineIterator implements Iterator<String> {

        File file;
        BufferedReader br;
        String line;
        boolean hasNext;

        public FileLineIterator() {
            this.file = new File(fName);
            try {
                br = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException ex) {
                notifyOfError(ex, "opening");
            }
            try {
                line = br.readLine();
                hasNext = line != null;
            } catch (IOException ex) {
                notifyOfError(ex, "reading first line");
            }
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public String next() {
            String toReturn = line;
            try {
                line = br.readLine();
            } catch (IOException ex) {
                notifyOfError(ex, "reading");
            } finally {
                boolean lastHasNext = hasNext;
                hasNext = line != null;
                // try to close the file
                if (lastHasNext && !hasNext) {
                    try {
                        br.close();
                    } catch (IOException ex) {
                        notifyOfError(ex, "closing");
                    }
                }
            }
            return toReturn;
        }

        private void notifyOfError(IOException ex, String when) {
            throw new RuntimeException(
                    "Error while " + when + " \'" + file + "\'.", ex);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    @Override
    public Iterator<String> iterator() {
        return new FileLineIterator();
    }
}
