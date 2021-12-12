package a3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class Parse {

    /**
     * a separator between words in an artist name
     */
    public static final Pattern WHITESPACE = Pattern.compile("\\s+");

    /**
     * the string after which artist names begin on a song chart line
     */
    private static final String ARTIST_INIT = "–•–";

    /**
     * words separating artist names on a song chart line
     */
    static final Set<String> ARTIST_SEP = new HashSet<>();

    static {
        ARTIST_SEP.add("featuring");
        ARTIST_SEP.add("feat.");
        ARTIST_SEP.add("&");
        ARTIST_SEP.add("with");
        ARTIST_SEP.add("x");
        ARTIST_SEP.add("and");
        ARTIST_SEP.add("+");
        ARTIST_SEP.add(",");

        /* Unfortunately, the artist “Do or Die” will be split into “Do” and “Die”.
        But this is the only artist with such a problem. */
        ARTIST_SEP.add("or");
    }

    /**
     *
     * @param chartLine
     * @return the list of artist names on a {@code chartLine}
     */
    public static List<ArtistName> chartLineToArtists(final String chartLine) {
        final int artistI = chartLine.indexOf(ARTIST_INIT);
        if (artistI < 0) return null;
        else {
            final String artistsString = chartLine
                    /* Extracts artist names from the song chart line. */
                    .substring(artistI + ARTIST_INIT.length())
                    /* Makes commas into words. */
                    .replace(",", " ,");
            final String[] words = WHITESPACE.split(artistsString, -1);
            if (words.length == 0) {
                System.err.print("invalid chart line: ");
                System.err.println(chartLine);
                return null;
            } else {
                final List<ArtistName> artists = new ArrayList<>();
                /* If there is a whitespace between {@link #ARTIST_INIT} and the first artist name,
                {@code words[0]} will be empty. */
                int wordI = words[0].isEmpty() ? 1 : 0;
                /* Processes {@code words} starting from index {@code wordI}. */
                boolean cont = true;
                do {
                    final ArrayList<String> artistName = new ArrayList<>();
                    while (true) {
                        if (wordI >= words.length) {
                            cont = false;
                            break;
                        }
                        final String word = words[wordI];
                        wordI++;
                        if (word.startsWith("(") || word.startsWith(/* EN DASH */"–")) {
                            cont = false;
                            break;
                        }
                        if (ARTIST_SEP.contains(word.toLowerCase())) break;
                        artistName.add(word);
                    }
                    artists.add(new ArtistName(artistName));
                } while (cont);
                return artists;
            }
        }
    }

    /**
     * Sends the lists of artist names in HTML nodes in {@code iterator} to {@code db}, one list for
     * every chart line.
     *
     * @param iterator
     * @param db
     */
    public static void nodes(Iterator<Node> iterator, Consumer<List<ArtistName>> db) {
        boolean cont = true;
        do {
            final StringBuilder chartLine = new StringBuilder();
            while (true) {
                if (!iterator.hasNext()) {
                    cont = false;
                    break;
                }
                final Node node = iterator.next();
                if (node instanceof Element && ((Element) node).normalName().equals("br")) break;
                if (node instanceof TextNode) chartLine.append(((TextNode) node).text());
                else if (node instanceof Element) chartLine.append(((Element) node).text());
            }
            final List<ArtistName> a0 = chartLineToArtists(chartLine.toString());
            if (a0 != null) db.accept(a0);
        } while (cont);
    }

    /**
     * Sends the lists of artist names in HTML {@code element} to {@code db}, one list for every
     * chart line.
     *
     * @param element
     * @param db
     */
    public static void element(Element element, Consumer<List<ArtistName>> db) {
        if (element.tag().normalName().equals("p")) nodes(element.childNodes().iterator(), db);
        for (Element childElement : element.children()) element(childElement, db);
    }

}
