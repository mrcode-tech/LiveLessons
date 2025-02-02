package edu.vandy.quoteservices.microservices.zippy;

import edu.vandy.quoteservices.common.BaseController;
import edu.vandy.quoteservices.common.BaseService;
import edu.vandy.quoteservices.common.Quote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * This class defines implementation methods that are called by the
 * {@link BaseController}, which serves as the main "front-end" app
 * gateway entry point for remote clients that want to receive Zippy
 * quotes.
 *
 * This class implements the abstract methods in {@link BaseService}
 * using the Java sequential streams framework.
 *
 * This class is annotated as a Spring {@code @Service}, which enables
 * the automatic detection and wiring of dependent implementation
 * classes via classpath scanning. It also includes its name in the
 * {@code @Service} annotation below so that it can be identified as a
 * service.
 */
@Service
public class ZippyService
       implements BaseService<List<Quote>> {
    /**
     * Spring-injected repository that contains all quotes.
     */
    @Autowired
    private JPAQuoteRepository mRepository;

    /**
     * @return A {@link List} of all {@link Quote} objects
     */
    @Override
    public List<Quote> getAllQuotes() {
        return mRepository
            // Forward to the repository.
            .findAll();
    }

    /**
     * Get a {@link List} that contains the requested quotes.
     *
     * @param quoteIds A {@link List} containing the given random
     *                 {@code quoteIds}
     * @param parallel Run the queries in parallel if true, else run
     *                 sequentially
     * @return A {@link List} of all requested {@link Quote} objects
     */
    @Override
    public List<Quote> postQuotes(List<Integer> quoteIds,
                                  Boolean parallel) {
        System.out.println("ZippyService.getQuotes()");

        return mRepository
            // Forward to the repository.
            .findAllById(quoteIds);
    }

    /**
     * Search for quotes containing the given {@link String} queries
     * and return a {@link List} of matching {@link Quote} objects.
     *
     * @param queries The search queries
     * @param parallel Run the queries in parallel if true, else run
     *                 sequentially
     * @return A {@code List} of quotes containing {@link Quote}
     *         objects matching the given {@code queries}
     */
    @Override
    public List<Quote> search(List<String> queries,
                              Boolean parallel) {
        // Use a Java sequential or parallel stream and the JPA to
        // locate all quotes whose 'id' matches the List of 'queries'
        // and return them as a List of Quote objects.
        return StreamSupport
            // Convert the List to a Stream.
            .stream(queries.spliterator(), parallel)

            // Flatten the Stream of Streams into a Stream.
            .flatMap(query ->  mRepository
                     // Find all Quote rows in the database that match
                     // the 'query'.
                     .findByQuoteContainingIgnoreCase(query)

                     // Convert List to a Stream.
                     .stream())

            // Ensure duplicate Zippy quotes aren't returned.
            .distinct()

            // Convert the Stream to a List.
            .toList();
    }

    /**
     * Search for quotes containing all the given {@link String} and
     * return a {@link List} that contains the matching {@link Quote}
     * objects.
     *
     * @param queries The search queries
     * @return A {@code List} containing {@link Quote} objects
     *         matching the given {@code queries}
     */
    @Override
    public List<Quote> searchEx(List<String> queries,
                                Boolean notUsed) {
         return mRepository
             // Forward to the repository.
             .findAllByQuoteContainingIgnoreCaseAllIn(queries);
     }
}
