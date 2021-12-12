package a3;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Artist name. Immutable class.
 */
public final class ArtistName {

    private final List<String> a;

    public ArtistName(List<String> a) {
        this.a = a;
    }

    /**
     *
     * @return artist name as a list of words
     */
    public List<String> getWords() {
        return Collections.unmodifiableList(a);
    }
    
    public boolean isEmpty() {
        return a.isEmpty();
    }

    @Override
    public int hashCode() {
        return a.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ArtistName other = (ArtistName) obj;
        if (!Objects.equals(this.a, other.a)) return false;
        return true;
    }

    /**
     *
     * @return artist name as a string
     */
    public String toBareString() {
        return String.join(" ", a);
    }

    /**
     *
     * @return artist name as a string for printing it inside data structures
     */
    @Override
    public String toString() {
        return "ArtistName{" + toBareString() + '}';
    }

}
