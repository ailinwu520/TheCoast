package a3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Main {

    /**
     * Pause before a web page download, in ms. A web site may ban a program that downloads too
     * fast.
     */
    private static final int DOWNLOAD_PAUSE = 1000;

    /**
     * Writes the web page {@code document} with a song chart for {@code year} into a file.
     *
     * For debugging.
     *
     * @param year
     * @param document
     * @throws IOException
     */
    private static void writeWebPage(int year, Document document) throws IOException {
        Files.write(Paths.get("../web-page/" + year + ".txt"), document.html().getBytes());
    }

    /**
     * Downloads the web page with a song chart, parses it, and sends artists collaborations to
     * {@code db}.
     *
     * @param year year of the song chart
     * @param path relative path to the web page on the Top40Weekly.com web site
     * @param db
     * @throws IOException
     */
    private static void parseWebpage(int year, String path, Consumer<List<ArtistName>> db)
            throws IOException {
        try {
            Thread.sleep(DOWNLOAD_PAUSE);
        } catch (InterruptedException ex) {
        }
        final String url = "https://top40weekly.com" + path;
        System.out.println("Downloading " + url);
        Document document = Jsoup.connect(url).get();
//        writeWebPage(year, document);
        Parse.element(document, db);
    }

    /**
     * Executes {@link #parseWebpage(int, java.lang.String, java.util.function.Consumer)} for all
     * years starting from {@code fromYear} inclusive.
     *
     * @param fromYear
     * @param db
     * @throws IOException
     */
    private static void parseAll(int fromYear, Consumer<List<ArtistName>> db) throws IOException {
        for (int year = fromYear; year < 2020; year++)
            parseWebpage(year, "/" + year + "-all-charts/", db);
        for (int year = Math.max(fromYear, 2020); year <= 2021; year++)
            parseWebpage(year, "/all-us-top-40-singles-for-" + year + "/", db);
    }

    /**
     * Menu-driven user interface.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
//        Document document = Jsoup.parse(new File("../web-page/1995.txt"), "UTF-8");
        final Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter starting year: ");
            System.out.flush();
            final int fromYear = scanner.nextInt();
            scanner.nextLine();
            final Db db = new Db();
            parseAll(fromYear, db::addCollaborators);
//            db.dump(System.out);
            while (true) {
                System.out.print("Enter artist name (empty to exit): ");
                System.out.flush();
                final String artistNameS = scanner.nextLine();
                if (artistNameS.isEmpty()) break;
                final Artist artist = db.getArtist(
                        new ArtistName(Arrays.asList(Parse.WHITESPACE.split(artistNameS, -1))));
                if (artist == null)
                    System.out.println("This artist doesn't exist.");
                else {
                    System.out.print("All indirect collaborators of this artist: ");
                    boolean first = true;
                    final Set<Artist> collaborators = artist.indirectCollaborators();
                    collaborators.remove(artist);
                    for (Artist artist1 : collaborators) {
                        if (first) first = false;
                        else System.out.print(", ");
                        System.out.print(artist1.getName().toBareString());
                    }
                    System.out.println();
                }
            }
        } catch (NoSuchElementException ex) {
            System.out.println();
        }
    }

}
