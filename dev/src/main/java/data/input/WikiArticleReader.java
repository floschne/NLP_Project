package data.input;

import data.util.PopularWikiArticlesListBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.apache.uima.util.Progress;

import java.io.IOException;
import java.util.List;

/**
 * Reads @{@link WikiArticle}s and adds it to the JCas
 */
public class WikiArticleReader extends JCasCollectionReader_ImplBase {
    public static final String PARAM_ARTICLE_TITLE_FILES = "ListOfArticlesFile";
    @ConfigurationParameter(name = PARAM_ARTICLE_TITLE_FILES, description = "A comma-separated list of files containing a list of titles of Wikipedia Articles that get processed in this reader", mandatory = true, defaultValue = "utf-8")
    private String listOfArticleTitleFiles;


    private Logger logger = null;

    //the WikiArticleLoader that'll be used to provide the articles
    private WikiArticleLoader wikiArticleLoader;
    //will contain the articles that are already loaded
    private List<WikiArticle> wikiArticles;
    //List of tuples of title and language of the articles that should be added to the JCAS (aka processed by the reader)
    private List<Pair<WikiArticle.Language, String>> wikiArticlesToProcess;

    private Integer currentArticleIdx;

    /**
     * This method should be overwritten by subclasses.
     *
     * @param context the UIMA context the component is running in
     * @throws ResourceInitializationException if a failure occurs during initialization.
     */
    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context);
        logger = context.getLogger();
        this.wikiArticleLoader = WikiHttpApiLoader.getInstance();

        try {
            // deserialize the Wikipedia Article Files and initialize wikiArticlesToProcess
            String[] titleFiles = listOfArticleTitleFiles.split(",");
            logger.log(Level.INFO, "Deserializing Wikipedia Article Titles from files: '" + listOfArticleTitleFiles.replace(",", " ") + "' !");
            wikiArticlesToProcess = PopularWikiArticlesListBuilder.deserializeListOfMostPopularWikiArticlesFromCsvFile(titleFiles);

            // download the articles and save them in the wikiArticles list
            logger.log(Level.INFO, "Starting to download the Wikipedia Articles!");
            for (Pair<WikiArticle.Language, String> article : wikiArticlesToProcess) {
                logger.log(Level.INFO, "Downloading Wikipedia Articles with title '" + article.getRight() + "'...");
                wikiArticles.add(wikiArticleLoader.loadArticle(article.getRight(), article.getLeft()));
            }
            logger.log(Level.INFO, "Finished downloading the Wikipedia Articles!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subclasses should implement this method rather than {@link #getNext(CAS)}
     *
     * @param jCas the {@link JCas} to store the read data to
     * @throws IOException         if there was a low-level I/O problem
     * @throws CollectionException if there was another problem
     */
    @Override
    public void getNext(JCas jCas) throws IOException, CollectionException {
        //TODO have to talk about this in next meeting
        //jCas.setDocumentText(wikiArticles.get(currentArticleIdx++).getContentAsString());
    }

    /**
     * Gets whether there are any elements remaining to be read from this
     * <code>CollectionReader</code>.
     *
     * @return true if and only if there are more elements available from this
     * <code>CollectionReader</code>.
     * @throws IOException         if an I/O failure occurs
     * @throws CollectionException if there is some other problem with reading from the Collection
     */
    @Override
    public boolean hasNext() throws IOException, CollectionException {
        //TODO have to talk about this in next meeting
        //return currentArticleIdx < wikiArticles.size();
        return false;
    }

    /**
     * Gets information about the number of entities and/or amount of data that has been read from
     * this <code>CollectionReader</code>, and the total amount that remains (if that information
     * is available).
     * <p>
     * This method returns an array of <code>Progress</code> objects so that results can be reported
     * using different units. For example, the CollectionReader could report progress in terms of the
     * number of documents that have been read and also in terms of the number of bytes that have been
     * read. In many cases, it will be sufficient to return just one <code>Progress</code> object.
     *
     * @return an array of <code>Progress</code> objects. Each object may have different units (for
     * example number of entities or bytes).
     */
    @Override
    public Progress[] getProgress() {
        //TODO have to talk about this in next meeting
        //return new Progress[] {new ProgressImpl(currentArticleIdx, wikiArticles.size(), Progress.ENTITIES)};
        return null;
    }
}
