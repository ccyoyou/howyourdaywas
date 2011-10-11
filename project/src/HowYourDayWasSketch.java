
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import processing.core.PApplet;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.Files;

import edu.umich.eecs498f11.howyourdaywas.DataPoint;
import edu.umich.eecs498f11.howyourdaywas.DataSource;
import edu.umich.eecs498f11.howyourdaywas.FacebookSource;
import edu.umich.eecs498f11.howyourdaywas.TwitterSource;

public class HowYourDayWasSketch extends PApplet {

	private static final long serialVersionUID = -450426086211320391L;

	// Sentiment analysis vars
	private static final String PATH_TO_TRAINING_DATA = "/Users/cdzombak/code/howyourdaywas"; 
    private static final int mNGram = 2;
    private DynamicLMClassifier<NGramProcessLM> mClassifier;
	private File     mPolarityDir;
    private String[] mCategories;
    
    private List<DataPoint> mAllPosts = new ArrayList<DataPoint>();

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "HowYourDayWasSketch" });
	}

	public void setup() {
		mPolarityDir = new File(PATH_TO_TRAINING_DATA, "txt_sentoken");
        mCategories = mPolarityDir.list();
        mClassifier = DynamicLMClassifier.createNGramProcess(mCategories, mNGram);
        try {
        	trainClassifier();
        } catch(IOException e) {
        	System.err.println("IO error reading classification training data.");
        	System.exit(ExitCodes.CLASSIFIER_TRAINING_ERROR);
        }
        
		final DataSource twitter = new TwitterSource();
		final DataSource facebook = new FacebookSource();

		final List<DataPoint> tweets = twitter.getData();
		final List<DataPoint> statuses = facebook.getData();
		
		mAllPosts.addAll(tweets);
		mAllPosts.addAll(statuses);
		Collections.sort(mAllPosts, new DataPoint.DataPointComparator());
		
		// TODO datavis :)
	}

	public void draw() {
		// TODO
	}
	
	private void trainClassifier() throws IOException {
        int numTrainingCases = 0;
        int numTrainingChars = 0;
        
        System.out.println("\nTraining...");
        
        for (final String category : mCategories) {
            final Classification classification = new Classification(category);
            final File file = new File(mPolarityDir, category);
            
            final File[] trainFiles = file.listFiles();
            for (final File trainFile : trainFiles) {
                ++numTrainingCases;
                final String review = Files.readFromFile(trainFile,"ISO-8859-1");
                numTrainingChars += review.length();
                final Classified<CharSequence> classified = new Classified<CharSequence>(review, classification);
                mClassifier.handle(classified);
            }
        }
        
        System.out.println("  # Training Cases = " + numTrainingCases);
        System.out.println("  # Training Chars = " + numTrainingChars);
    }
	
	private Sentiment evalMessage(final String message) {
        final Classification classification = mClassifier.classify(message);
        final String bestCategory = classification.bestCategory();
        
        if (bestCategory.equals("neg")) {
        	return Sentiment.NEGATIVE;
        } else if (bestCategory.equals("pos")) {
        	return Sentiment.POSITIVE;
        } else if (bestCategory.equals("u")) {
        	return Sentiment.NEUTRAL;
        } else {
        	System.err.println("Unknown sentiment found: " + bestCategory);
        	return Sentiment.NEUTRAL;
        }
    }
	
	private enum Sentiment {
		POSITIVE, NEUTRAL, NEGATIVE
	}
	
	private final class ExitCodes {
		public static final int CLASSIFIER_TRAINING_ERROR = 1;
	}
}
