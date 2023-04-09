package pt.tecnico.blockchain;

import java.io.Serializable;
import java.util.Objects;

public class Pair<T1, T2> implements Serializable {
    private final T1 t1;
    private final T2 t2;

    public Pair(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public T1 getFirst() {
        return t1;
    }
    public T2 getSecond() {
        return t2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) obj;
        return Objects.equals(t1, pair.t1) && Objects.equals(t2, pair.t2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t1, t2);
    }

    @Override
    public String toString() {
        return Objects.toString(t1) + ":" + Objects.toString(t2);
    }
}
