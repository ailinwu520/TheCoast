package a3;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database containing artists and collaborations between them.
 */
public class Db {

    private int id;
    private final Map<ArtistName, Artist> artists = new HashMap<>();

    /**
     *
     * @param name
     * @return the artist with {@code name} or {@code null} if there is no such
     */
    public Artist getArtist(ArtistName name) {
        return artists.get(name);
    }

    /**
     * If there is no artist with {@code name}, inserts it into this database.
     *
     * Inserted artists will have different identifiers {@link Artist#getId()}.
     *
     * @param name
     * @return the artist with {@code name}
     */
    public Artist insertOrGetArtist(ArtistName name) {
        Artist artist = artists.get(name);
        if (artist == null) {
            artist = new Artist(id, name);
            artists.put(name, artist);
        }
        id++;
        return artist;
    }

    /**
     * Adds collaboration links between all pairs ({@code artist0}, {@code artist1}) such that
     * {@code artist0} belongs to {@code artistNames} and {@code artist1} belongs to
     * {@code artistNames}, but doesn't add a collaboration link between an artist and the same
     * artist.
     *
     * @param artistNames
     */
    public void addCollaborators(List<ArtistName> artistNames) {
        final List<Artist> collaboratingArtists = new ArrayList<>();
        for (ArtistName artistName : artistNames)
            collaboratingArtists.add(insertOrGetArtist(artistName));
        int i = 0;
        for (Artist artist0 : collaboratingArtists) {
            if (!artist0.getName().isEmpty()) {
                int j = 0;
                for (Artist artist1 : collaboratingArtists) {
                    if (j >= i) break;
                    if (!artist1.getName().isEmpty()) artist0.addCollaborator(artist1);
                    j++;
                }
            }
            i++;
        }
    }

    /**
     * Writer this database to {@code writer}.
     *
     * For debugging.
     *
     * @param writer
     */
    public void dump(PrintStream writer) {
        for (Map.Entry<ArtistName, Artist> entry : artists.entrySet())
            writer.println(entry.getKey() + "=" + entry.getValue().toStringCollaborators());
    }

}
