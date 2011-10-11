
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import processing.core.PApplet;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.Files;

import edu.umich.eecs498f11.howyourdaywas.DataPoint;
import edu.umich.eecs498f11.howyourdaywas.DataPointComparator;
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
    
    private int mTempCount = 0;

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
        	System.exit(1);
        }
        
		// TODO
		// for display, we'll likely steal a lot of the fonts and full-screen stuff from cdzombak's NoiseSketch (in processing-exercises)
		
		// Example Twitter usage:
		final DataSource twitter = new TwitterSource();
		final DataSource facebook = new FacebookSource();

		final List<DataPoint> tweets = twitter.getData();
		final List<DataPoint> statuses = facebook.getData();
		final List<DataPoint> collection = facebook.getData();
		collection.addAll(tweets);
		DataPointComparator c = new DataPointComparator();
		Collections.sort(collection, c);
		
		for(final DataPoint message : collection) {
			System.out.println(message.text);
		}
		
	}

	public void draw() {
		// TODO
	}
	
	private String eval(String status_tweet) {
        Classification classification = mClassifier.classify(status_tweet);
        return classification.bestCategory();
    }
	
	private void trainClassifier() throws IOException {
        int numTrainingCases = 0;
        int numTrainingChars = 0;
        
        System.out.println("\nTraining...");
        
        for (int i = 0; i < mCategories.length; ++i) {
            String category = mCategories[i];
            Classification classification
                = new Classification(category);
            File file = new File(mPolarityDir,mCategories[i]);
            File[] trainFiles = file.listFiles();
            for (int j = 0; j < trainFiles.length; ++j) {
                File trainFile = trainFiles[j];
                ++numTrainingCases;
                String review = Files.readFromFile(trainFile,"ISO-8859-1");
                numTrainingChars += review.length();
                Classified<CharSequence> classified
                    = new Classified<CharSequence>(review,classification);
                mClassifier.handle(classified);
            }
        }
        
        System.out.println("  # Training Cases=" + numTrainingCases);
        System.out.println("  # Training Chars=" + numTrainingChars);
    }
	
}
