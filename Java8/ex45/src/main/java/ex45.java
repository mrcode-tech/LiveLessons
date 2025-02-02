import utils.TestDataFactory;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static utils.RegexUtils.getFirstLine;
import static utils.RegexUtils.makeRegex;
import static java.util.stream.Collectors.joining;

/**
 * This example shows how the Java regular expression methods can be
 * used to search the complete works of Shakespeare ({@code bardWorks})
 * particular phrases.
 */
public class ex45 {
    /**
     * The complete works of William Shakespeare.
     */
    private static final String sSHAKESPEARE_DATA_FILE =
        "completeWorksOfShakespeare.txt";

    /**
     * Main entry point into the program.
     */
    static public void main(String[] argv) {
        // Test the makeRegex() method.
        testRegexList();

        // Create a List of String objects containing the complete
        // works of Shakespeare.
        List<String> bardWorks = TestDataFactory
            .getInput(sSHAKESPEARE_DATA_FILE,
                      // Split input into "works".
                      "@");

        assert bardWorks != null;

        // Search the works of Shakespeare for a certain word/phrase.
        processBardWorks(bardWorks, "be");
    }

    /**
     * Test the makeRegex() method.
     */
    private static void testRegexList() {
        var quote = "The quick fox jumps over \nthe lazy concatentate.";
        var regexString =
            makeRegex(List.of("Cat", "Dog", "Mouse", "sox"));

        if (quote.toLowerCase().matches("(?s)" + regexString))
            System.out.println("matches");
        else
            System.out.println("no matches");
    }

    /**
     * Show how the Java regular expression methods can be used to
     * search the complete works of Shakespeare ({@code bardWorks} for
     * {@code word}.
     */
    private static void processBardWorks(List<String> bardWorks,
                                         String word) {
        // Create a List of Shakespeare works containing 'word'.
        var bardWorksMatchingWord = bardWorks
            // Convert List to a Stream.
            .stream()

            // Only keep works containing 'word'.
            .filter(work -> Stream
                    // Convert String to a Stream.
                    .of(work)

                    // Return true if 'word' appears in 'work'.
                    .anyMatch(string -> string
                              .toLowerCase()
                              .contains(word)))

            // Convert List to a Stream.
            .toList();

        // The regular expression to compile, which matches the phrase
        // "'word' followed by either 'true' or 'false'".
        String regex = "\\b"
            + word
            + "\\b"
            + ".*(true|false)";

        // Compile the regular expression to perform case-insensitive
        // matches.
        Pattern pattern = Pattern
            .compile(regex,
                     Pattern.CASE_INSENSITIVE);

        // Show the portions of the works of Shakespeare that match
        // the pattern.
        showRegexMatches(bardWorksMatchingWord, pattern);
    }

    /**
     * Show the portions of the works of Shakespeare that match the
     * {@link Pattern}.
     *
     * @param bardWorksMatchingWord The Shakespeare works matching
     *                              a search word       
     * @param pattern The compiled regular expression to search for
     */
    private static void showRegexMatches
        (List<String> bardWorksMatchingWord,
         Pattern pattern) {
        bardWorksMatchingWord
            // Process each work in the Stream.
            .forEach(work -> {
                    // Get the title of the work.
                    String title = getFirstLine(work);

                    // Print the title of the work.
                    System.out.println(title);

                    // Create a Matcher that associates the regex with
                    // the quoteString.
                    pattern
                        .matcher(work)

                        // Create a Stream of matches.
                        .results()

                        // Print each match.
                        .forEach(matchResult -> System.out
                                 .println("\""
                                          // Print the match.
                                          + matchResult.group()
                                          + "\" ["
                                          // Print match location.
                                          + matchResult.start()
                                          + "]"));
                });
    }
}
