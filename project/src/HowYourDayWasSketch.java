
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import processing.core.PApplet;
import processing.core.PFont;

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
    
    // data
    private List<DataPoint> mAllPosts = new ArrayList<DataPoint>();

    // visualization
    private static final float  FRAME_RATE       = 0.5f;
    private static final int    MARGIN_X         = 50;
    private static final String FONT_FILENAME    = "MyriadPro-Regular-56.vlw";
	private static final int    FONT_SIZE        = 56;
	private static final int    LINE_HEIGHT      = 60;
	private static final int    INITIAL_BACKGROUND = 30;
    private int          mHeight;
	private int          mWidth;
	
	private int          mCurrentMessageIdx = 0;
	private List<Double> mScores = new ArrayList<Double>();
    
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
		
		mHeight = screen.height;
		mWidth = screen.width;
		
		size(mWidth, mHeight);
		background(INITIAL_BACKGROUND);
		frameRate(FRAME_RATE);
		smooth();
		
		final PFont font = loadFont(FONT_FILENAME);
		textFont(font, FONT_SIZE);
		textAlign(CENTER, CENTER);
	}

	public void draw() {
		if (mScores.size() != 0) {
			double averageScore = calcAverageScore();
			background( 255*Math.abs((float)averageScore-1) , 255*(float)averageScore, 0.0f);
		} else {
			background(INITIAL_BACKGROUND);
		}
		
		if (mCurrentMessageIdx == mAllPosts.size()) {
			final Date now = new Date();
			final DateFormat df = new SimpleDateFormat("EEEE, MMMM d");
			
			fill(INITIAL_BACKGROUND);
			text(df.format(now), mWidth/2, mHeight/2);
			
			return;
		}
		
		final String currentMessage = mAllPosts.get(mCurrentMessageIdx).toString();
		final Sentiment currentSentiment = evalMessage(currentMessage);
		mCurrentMessageIdx++;
		
		switch (currentSentiment) {
			case NEGATIVE:
				fill(255, 0, 0);
				break;
			case NEUTRAL:
				fill(255, 255, 0);
				break;
			case POSITIVE:
				fill(0, 255, 0);
				break;
		}
		
		final List<String> wrappedStrings = wordWrap(currentMessage, mWidth-(MARGIN_X*2));
		final int startY = (int) ((mHeight/2) - LINE_HEIGHT*(((float)wrappedStrings.size())/2));
		int currentY = startY;
		for(int i=0; i<wrappedStrings.size(); ++i) {
			text(wrappedStrings.get(i), mWidth/2, currentY);
			currentY += LINE_HEIGHT;
		}
		
		if (currentSentiment == Sentiment.NEGATIVE) {
			mScores.add(0.0);
		} else if (currentSentiment == Sentiment.POSITIVE) {
			mScores.add(1.0);
		}
	}
	
	private double calcAverageScore() {
		double total = 0.0;
		for (final Double d : mScores) {
			total += d;
		}
		
		return total / mScores.size();
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
	
	/**
	 * wordwrap taken from http://wiki.processing.org/index.php?title=Word_wrap_text
	 * @author Daniel Shiffman
	 */
	List<String> wordWrap(String s, final int maxWidth) {
		final ArrayList<String> a = new ArrayList<String>();
		float w = 0;    // Accumulate width of chars
		int i = 0;      // Count through chars
		int rememberSpace = 0; // Remember where the last space was
		
		// As long as we are not at the end of the String
		while (i < s.length()) {
			// Current char
			final char c = s.charAt(i);
			w += textWidth(c); // accumulate width
			if (c == ' ') rememberSpace = i; // Are we a blank space?
			if (w > maxWidth) {  // Have we reached the end of a line?
				String sub = s.substring(0,rememberSpace); // Make a substring
				// Chop off space at beginning
				if (sub.length() > 0 && sub.charAt(0) == ' ') sub = sub.substring(1,sub.length());
				// Add substring to the list
				a.add(sub);
				// Reset everything
				s = s.substring(rememberSpace,s.length());
				i = 0;
				w = 0;
			} 
			else {
				i++;
			}
		}

		// Take care of the last remaining line
		if (s.length() > 0 && s.charAt(0) == ' ') s = s.substring(1,s.length());
		a.add(s);

		return a;
	}
	
	private enum Sentiment {
		POSITIVE, NEUTRAL, NEGATIVE
	}
	
	private final class ExitCodes {
		public static final int CLASSIFIER_TRAINING_ERROR = 1;
	}
}
