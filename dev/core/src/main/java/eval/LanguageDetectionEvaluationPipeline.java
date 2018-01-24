package eval;

import de.tudarmstadt.ukp.dkpro.core.tokit.RegexTokenizer;
import knowledgebase.DatabaseUpdatePipeline;
import language_detection.LanguageDetector;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;
import query_generation.QueryGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static knowledgebase.DatabaseHandler.*;
import static language_detection.LanguageDetector.*;

public class LanguageDetectionEvaluationPipeline {

    public static void runPipeline() throws UIMAException, IOException {
        System.out.println("Generate queries from Wikipedia Articles Datasource (0, 1)?");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Boolean wiki = br.readLine().equals("1");

        System.out.println("Generate queries from News Articles Datasource (0, 1)?");
        br = new BufferedReader(new InputStreamReader(System.in));
        Boolean news = br.readLine().equals("1");

        CollectionReader queryGenerator = CollectionReaderFactory.createReader(QueryGenerator.class,
                QueryGenerator.PARAM_NUMBER_OF_QUERIES, 1000,
                QueryGenerator.PARAM_QUERY_LANGUAGES, "DE,EN,ES",
                QueryGenerator.PARAM_GEN_NEWS_QUERIES, news,
                QueryGenerator.PARAM_GEN_WIKI_QUERIES, wiki);

        AnalysisEngine tokenizer = AnalysisEngineFactory.createEngine(RegexTokenizer.class,
                RegexTokenizer.PARAM_WRITE_SENTENCE, false,
                RegexTokenizer.PARAM_TOKEN_BOUNDARY_REGEX, DatabaseUpdatePipeline.TOKEN_BOUNDARY_REGEX);

        AnalysisEngine languageDetector = AnalysisEngineFactory.createEngine(LanguageDetector.class,
                PARAM_DATABASE, DEFAULT_LOCATION,
                PARAM_USER, DEFAULT_USER,
                PARAM_PASSWORD, DEFAULT_PASSWORD);

        AnalysisEngine evaluationWriter = AnalysisEngineFactory.createEngine(LanguageDetectionEvaluator.class);

        SimplePipeline.runPipeline(queryGenerator, tokenizer, languageDetector, evaluationWriter);

        evaluationWriter.destroy();
    }

    public static void main(String[] args) throws IOException, UIMAException {
        runPipeline();
    }

}
