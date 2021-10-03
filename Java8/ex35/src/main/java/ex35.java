import transforms.Transform;
import utils.*;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This example shows how to ..
 */
public class ex35 {
    /**
     * Logging tag.
     */
    private static final String TAG = ex35.class.getName();

    /**
     * The {@link List} of {@link Transform} objects to apply to
     * downloaded images.
     */
    protected static List<Transform> sTransforms = List
        .of(Transform.Factory.newTransform(Transform.Type.GRAY_SCALE_TRANSFORM),
            Transform.Factory.newTransform(Transform.Type.TINT_TRANSFORM),
            Transform.Factory.newTransform(Transform.Type.SEPIA_TRANSFORM));

    /**
     * Main entry point into the test program.
     */
    public static void main(String[] args) {
        System.out.println("Entering the program with "
                           + Runtime.getRuntime().availableProcessors()
                           + " cores available");

        // Initializes the Options singleton.
        Options.instance().parseArgs(args);

        // Runs the tests.
        runTest(ex35::downloadImage,
                ex35::transformImage,
                ex35::storeImage,
                "testDefaultDownloadBehavior()");

        // Print the results.
        System.out.println(RunTimer.getTimingResults());

        System.out.println("Leaving the program");
    }

    /**
     * Run the test named {@code testName} by applying the {@code
     * downloadImage}, {@code transformImage}, and {@code storeImage}
     * {@link Function} objects.
     *
     * @param downloadImage A {@link Function} that downloads images
     * @param transformImage A {@link Function} that transforms images
     * @param storeImage A {@link Function} that stores images
     * @param testName Name of the test
     */
    private static void runTest(Function<URL, Image> downloadImage,
                                Function<Image, Stream<Image>> transformImage,
                                Function<Image, File> storeImage,
                                String testName) {

        // Let the system garbage collect.
        System.gc();

        // Record how long the test takes to run.
        RunTimer.timeRun(() ->
                         // Run the test with the designated function.
                         testDownloadBehavior(downloadImage,
                                              transformImage,
                                              storeImage,
                                              testName),
                         testName);


        // Delete any images from the previous run.
        FileAndNetUtils
            .deleteDownloadedImages(Options.instance().getDirectoryPath());
    }

    /**
     * This method runs the tests via the {@code downloadImage}
     * function.
     *
     * @param downloadImage A {@link Function} that downloads images
     * @param transformImage A {@link Function} that transforms images
     * @param storeImage A {@link Function} that stores images
     * @param testName Name of the test
     */
    private static void testDownloadBehavior(Function<URL, Image> downloadImage,
                                             Function<Image, Stream<Image>> transformImage,
                                             Function<Image, File> storeImage,
                                             String testName) {
        List<URL> urlList = Options.instance().getUrlList();

        System.out.println("downloading "
                           + urlList.size()
                           + " images");

        // Store the list of downloaded/tranformed images.
        List<File> images = urlList
            // Convert the URLs in the input list into a stream and
            // process them in parallel.
            .parallelStream()

            // Transform URL to a File by downloading each image via
            // its URL.
            .map(downloadImage)

            // Transform the images.
            .flatMap(transformImage)

            // Store the images.
            .map(storeImage)

            // Terminate the stream and collect the results into list
            // of images.
            .collect(Collectors.toList());

        // Print the statistics for this test run.
        printStats(testName, images.size());
    }

    /**
     * This method blocks while retrieving the image associated with
     * the {@code url} and creates an {@link Image} to encapsulate it.
     *
     * @param url The {@link URL} to an image to download
     * @return The downloaded {@link Image}
     */
    private static Image downloadImage(URL url) {
        return new Image(url,
                         FileAndNetUtils.downloadContent(url));
    }

    /**
     * This method applies a group of {@link Transform} objects to
     * transform the {@link Image}.
     *
     * @param image The {@link Image} to transform
     * @return A {@link Stream} of transformed {@link Image} objects
     */
    private static Stream<Image> transformImage(Image image) {
        return sTransforms
            // Convert the List of transforms to a parallel stream.
            .parallelStream()

            // Apply each transform to the original image to produce a
            // transformed image.
            .map(transform -> transform.transform(image));
    }

    /**
     * This method stores the {@link Image} to the local file system.
     *
     * @return A {@link File} containing the stored {@link Image}
     */
    private static File storeImage(Image image) {
        return image.store();
    }

    /**
     * Display the statistics about the test.
     */
    private static void printStats(String testName, 
                                   int imageCount) {
        System.out.println(TAG 
                           + ": "
                           + testName
                           + " downloaded and stored "
                           + imageCount
                           + " images using "
                           + (ForkJoinPool.commonPool().getPoolSize() + 1)
                           + " threads in the pool");
    }
}
