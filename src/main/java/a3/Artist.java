package a3;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Database record for an artist.
 *
 * Warning. {@link Db} produces at most one {@link Artist} for each {@link ArtistName}, so
 * {@link #equals(java.lang.Object)} just compares object references. Don't compare {@link Artist}s
 * produced by different {@link Db}s!
 */
public class Artist {

    private final int id;

    private final ArtistName name;

    private final Set<Artist> collaborators = new HashSet<>();

    public Artist(int id, ArtistName name) {
        this.id = id;
        this.name = name;
    }

    /**
     * 
     * @return unique identifier; see {@link Db#insertOrGetArtist(a3.ArtistName)}
     */
    public int getId() {
        return id;
    }

    public ArtistName getName() {
        return name;
    }

    public Set<Artist> getCollaborators() {
        return Collections.unmodifiableSet(collaborators);
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * Adds collaboration between this artist and {@code artist}. Two reciprocal collaboration links
     * are added, so {@link #getCollaborators()} of this artist will contain {@code artist} and
     * {@link #getCollaborators()} of {@code artist} will contain this artist.
     *
     * @param artist
     */
    public void addCollaborator(Artist artist) {
        this.collaborators.add(artist);
        artist.collaborators.add(this);
    }

    /**
     *
     * @return string representation without collaborators
     */
    @Override
    public String toString() {
        return "Artist{" + "id=" + id + ", name=" + name + '}';
    }

    /**
     *
     * @return string representation with collaborators
     */
    public String toStringCollaborators() {
        return "Artist{" + "id=" + id + ", name=" + name + ", collaborators=" + collaborators + '}';
    }

    /**
     * Adds indirect collaborators of this artist (including this artist) to {@code collaborators}.
     *
     * @param indCollaborators
     */
    private void addCollaboratorsTo(Set<Artist> indCollaborators) {
        if (!indCollaborators.contains(this)) {
            indCollaborators.add(this);
            for (Artist artist1 : getCollaborators()) artist1.addCollaboratorsTo(indCollaborators);
        }
    }

    /**
     * Uses depth-first search.
     *
     * @return indirect collaborators of this artist (including this artist)
     */
    public Set<Artist> indirectCollaborators() {
        final Set<Artist> indCollaborators = new HashSet<>();
        addCollaboratorsTo(indCollaborators);
        return indCollaborators;
    }

}
