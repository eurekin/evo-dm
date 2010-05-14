package pl.eurekin.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Szczypta programowania funkcyjnego
 * 
 * @author Rekin
 * @param <T>
 */
public abstract class IterableFilter<T> implements Iterable<T>, Iterator<T> {

    public abstract boolean passes(T object);
    private Iterator<T> iterator;
    private T next;

    public IterableFilter(Iterable<T> iterable) {
        this.iterator = iterable.iterator();
        toNext();
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public T next() {
        if (next == null) {
            throw new NoSuchElementException();
        }
        T returnValue = next;
        toNext();
        return returnValue;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void toNext() {
        next = null;
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (item != null && passes(item)) {
                next = item;
                break;
            }
        }
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }
}
