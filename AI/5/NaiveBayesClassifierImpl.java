import java.util.Map;
import java.util.HashMap;

/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifierImpl implements NaiveBayesClassifier {
	/**
	 * Trains the classifier with the provided training data and vocabulary size
	 */
	private final double delta = 0.00001;
	
	private Map<Label, Integer> labelCount;
	private Map<Label, Map<String, Integer> > wordTypeCount;
	private Map<Label, Integer> labelTokenCount;
	private int numInstance;
	//private int numTotalToken;
	private int vocabularySize;
	
	public NaiveBayesClassifierImpl() {
		this.labelCount = new HashMap<Label, Integer>();
		this.wordTypeCount = new HashMap<Label, Map<String, Integer>>();
		this.labelTokenCount = new HashMap<Label, Integer>();
	}
	
	@Override
	public void train(Instance[] trainingData, int v) {
		
		// set the vocab size
		this.vocabularySize = v;
		this.numInstance = trainingData.length;
		
		// Process the training data
		for(Instance inst : trainingData) {
			
			// increment the label count of the instance
			if(this.labelCount.containsKey(inst.label)) {
				this.labelCount.put(inst.label, (this.labelCount.get(inst.label)+1));
			} else {
				this.labelCount.put(inst.label, 1);
			}

			// increment the token count for the string in the instance
			if(this.labelTokenCount.containsKey(inst.label)) {
				this.labelTokenCount.put(inst.label, (this.labelTokenCount.get(inst.label)+inst.words.length));
			} else {
				this.labelTokenCount.put(inst.label, inst.words.length);
			}

			// update the hash table
			for(String word : inst.words){
				
				// get the current count increment and reload
				// first check if the label has an entry in the outer map
				if(this.wordTypeCount.containsKey(inst.label)) {
					
					// If so check is the word has an entry in the inner key
					if(this.wordTypeCount.get(inst.label).containsKey(word)) {
						
						// Update accordingly
						int currentCount = this.wordTypeCount.get(inst.label).get(word);
						currentCount++;
						this.wordTypeCount.get(inst.label).put(word, currentCount);
					
					// We have to add the word to the inner
					} else {
						this.wordTypeCount.get(inst.label).put(word, 1);
					}
					
				// We have to create everything if we get here
				} else {
					
					// Create a map and store it in the hash
					Map<String, Integer> mapEntry = new HashMap<String, Integer>();
					this.wordTypeCount.put(inst.label, mapEntry);
					
					// Create the word entry
					this.wordTypeCount.get(inst.label).put(word, 1);
				}
			}	
		}
	}

	/**
	 * Returns the prior probability of the label parameter, i.e. P(SPAM) or P(HAM)
	 */
	@Override
	public double p_l(Label label) {				
		double labelCount = this.labelCount.get(label);
		double result = (labelCount / this.numInstance);
		return result;
	}

	/**
	 * Returns the smoothed conditional probability of the word given the label,
	 * i.e. P(word|SPAM) or P(word|HAM)
	 */
	@Override
	public double p_w_given_l(String word, Label label) {
		// Implement
		// count(w) + something
		// /
		// n * something + sum of counts for all words with label
		double wordCount = 0;
		if(this.wordTypeCount.get(label).containsKey(word)){
			wordCount = this.wordTypeCount.get(label).get(word);
		}
		double numerator = wordCount + this.delta;

		double dictSize = this.vocabularySize * this.delta;
		double sumOfWordsWithLabel = this.labelTokenCount.get(label);
		
		double denominator = dictSize + sumOfWordsWithLabel;
		double result = (numerator / denominator);
		return result;
	}
	
	/**
	 * Classifies an array of words as either SPAM or HAM. 
	 */
	@Override
	public ClassifyResult classify(String[] words) {
		Label spam = Label.SPAM;
		Label ham = Label.HAM;
		
		// Get inital label log probs
		double logProbSpam = Math.log(this.p_l(spam));
		double logProbHam  = Math.log(this.p_l(ham));
		
		// To store the counts
		double spamSum = 0;
		double hamSum = 0;
		
		for(String word : words) {
			spamSum += Math.log(this.p_w_given_l(word,spam));
			hamSum  += Math.log(this.p_w_given_l(word,ham));
		}
		
		spamSum += logProbSpam;
		hamSum += logProbHam;
		
		// Get max
		Label max = (spamSum > hamSum) ? Label.SPAM : Label.HAM;
		ClassifyResult res = new ClassifyResult();
		res.label = max;
		res.log_prob_ham = hamSum;
		res.log_prob_spam = spamSum;
		return res;
	}
	
	/**
	 * Gets the confusion matrix for a test set. 
	 */
	@Override
	public ConfusionMatrix calculateConfusionMatrix(Instance[] testData)
	{
		int spamCalledSpam = 0;
		int hamCalledSpam  = 0;
		int spamCalledHam  = 0;
		int hamCalledHam   = 0;
		
		// For comparison
		Label spam = Label.SPAM;
		Label ham  = Label.HAM;
		
		for (Instance inst : testData){
			ClassifyResult res = this.classify(inst.words);
			switch(inst.label) {
				case SPAM:
					if(res.label.equals(spam)) {
						spamCalledSpam++;
					} else {
						spamCalledHam++;
					}
					break;
				case HAM:
					if(res.label.equals(spam)) {
						hamCalledSpam++;
					} else {
						hamCalledHam++;
					}
					break;
			}
		}
		return new ConfusionMatrix(spamCalledSpam, hamCalledHam, hamCalledSpam, spamCalledHam);
	}

	private String instanceStringify(Instance inst) {
		String words = "";
		for(String word : inst.words) {
			words += word;
			words += " ";
		}
		return inst.label+" - "+words;
	}
}
