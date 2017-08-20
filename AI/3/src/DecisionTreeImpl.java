import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fill in the implementation details of the class DecisionTree using this file.
 * Any methods or secondary classes that you want are fine but we will only
 * interact with those methods in the DecisionTree framework.
 * 
 * You must add code for the 5 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
	private DecTreeNode root;
	private List<String> labels; // ordered list of class labels
	private List<String> attributes; // ordered list of attributes
	private Map<String, List<String>> attributeValues;

	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// no code necessary
		// this is void purposefully
	}

	/**
	 * Build a decision tree given only a training set.
	 * 
	 * @param train: the training set
	 */
	DecisionTreeImpl(DataSet train) {

		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		
		// Check to make sure we have different labels
		// and get a count of each label
		int[] labelCounts = new int[this.labels.size()];
		for(Instance instance : train.instances) {
			labelCounts[instance.label]++;
		}
		
		// Make sure more then one label was added
		int maxValue = 0;
		int maxLabel = -1;
		List<Integer> nonZeroLabels = new ArrayList<Integer>();
		for(int i = 0; i < this.labels.size(); i++) {
			
			// Make sure that label has members
			if(labelCounts[i] > 0) {
				nonZeroLabels.add(i);
				
				// Check if it is the majority
				if(labelCounts[i] > maxValue) {
					maxValue = labelCounts[i];
					maxLabel = i;
				}
			}
		}
		this.root = this.buildTreeRec(this.attributes, train.instances, maxLabel, -1);
	}
	
	private DecTreeNode buildTreeRec(List<String> attrs, List<Instance> instances, int parentMaxLabel, int parentAttrValue) {
		// Make sure training set is not empty
		if(instances.isEmpty()) {
			DecTreeNode returnNode = new DecTreeNode(parentMaxLabel,-1,parentAttrValue,true);
			return returnNode;
		}
		
		// Check to make sure we have different labels
		// and get a count of each label
		int[] labelCounts = new int[this.labels.size()];
		for(Instance instance : instances) {
			labelCounts[instance.label]++;
		}
		
		// Make sure more then one label was added
		int maxValue = 0;
		int maxLabel = -1;
		List<Integer> nonZeroLabels = new ArrayList<Integer>();
		for(int i = 0; i < this.labels.size(); i++) {
			
			// Make sure that label has members
			if(labelCounts[i] > 0) {
				nonZeroLabels.add(i);
				
				// Check if it is the majority
				if(labelCounts[i] > maxValue) {
					maxValue = labelCounts[i];
					maxLabel = i;
				}
			}
		}
		
		// Check if attributes is empty
		//  return majority label as root/leaf
		if(attrs.isEmpty()) {
			DecTreeNode returnNode = new DecTreeNode(maxLabel,-1,parentAttrValue,true);
			return returnNode;
		}
		
		// all examples have same label
		if(nonZeroLabels.size() == 1) {
			// return a tree with that label as a leaf
			DecTreeNode returnNode = new DecTreeNode(nonZeroLabels.get(0),-1,parentAttrValue,true);
			return returnNode;
		} 

		// Otherwise actually build the tree
		// get the best attr to use at this point
		int bestAttr = this.bestAttribute(attrs, instances);
		DecTreeNode subRoot = new DecTreeNode(maxLabel,bestAttr,parentAttrValue,false);
		List<String> attrVals = this.attributeValues.get(this.attributes.get(bestAttr));
		for(String attrValString : attrVals) {
			int attrInt = Integer.parseInt(attrValString)-1;
			List<Instance> withValue = this.subsetWithAttrValue(instances, bestAttr, attrInt);
			
			List<String> attrsNew = new ArrayList<String>(attrs);
			attrsNew.remove(this.attributes.get(bestAttr));

			DecTreeNode kid = this.buildTreeRec(attrsNew, withValue, maxLabel, attrInt);
			subRoot.children.add(kid);
		}
		return subRoot;	
	}
	
	/**
	 * Build a decision tree given a training set then prune it using a tuning
	 * set.
	 * 
	 * @param train: the training set
	 * @param tune: the tuning set
	 */
	DecisionTreeImpl(DataSet train, DataSet tune) {

		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
	}

	@Override
	public String classify(Instance instance) {
		DecTreeNode classified
	}
	
	private DecTreeNode classifyRec(Instance instance, DecTreeNode parent) {
		return null;
	}

	@Override
	/**
	 * Print the decision tree in the specified format
	 */
	public void print() {

		printTreeNode(root, null, 0);
	}
	
	/**
	 * Prints the subtree of the node
	 * with each line prefixed by 4 * k spaces.
	 */
	public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < k; i++) {
			sb.append("    ");
		}
		String value;
		if (parent == null) {
			value = "ROOT";
		} else{
			String parentAttribute = attributes.get(parent.attribute);
			value = attributeValues.get(parentAttribute).get(p.parentAttributeValue);
		}
		sb.append(value);
		if (p.terminal) {
			sb.append(" (" + labels.get(p.label) + ")");
			System.out.println(sb.toString());
		} else {
			sb.append(" {" + attributes.get(p.attribute) + "?}");
			System.out.println(sb.toString());
			for(DecTreeNode child: p.children) {
				printTreeNode(child, p, k+1);
			}
		}
	}

	@Override
	public void rootInfoGain(DataSet train) {
		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		for(String attr : this.attributes) {
			double infoGain = this.entropy(train.instances, this.labels.size());
			infoGain -= this.conditionalEntropy(train.instances, this.attributes.indexOf(attr));
			System.out.format("%s %.5f\n",attr, infoGain);
		}
	}
	
	// Choose the attribute to use
	private int bestAttribute(List<String> attrs, List<Instance> instances) {
		double maxGain = -1;
		int maxAttr = -1;
		double gain = this.entropy(instances, this.labels.size());
		for(String attr : attrs) {
			int attrInt = this.attributes.indexOf(attr);
			double result = this.conditionalEntropy(instances, attrInt);
			result = gain - result;
			
			// Tie breaker chose the lowest attr
			if(result == maxGain && attrInt < maxAttr) {
				maxAttr = attrInt;
			}
			
			else if(result > maxGain) {
				maxAttr = attrInt;
				maxGain = result;
			}
		}
		return maxAttr;
	}
	
	// calculate the entropy given an attr
	private double conditionalEntropy(List<Instance> instances, int attr) {
		//Need to bin instances with matching values for attr
		// get values for attr
		List<String> attrVals = this.attributeValues.get(this.attributes.get(attr));
		
		// Attr values are just ints so we need an array
		// each index stores the count of instances with that value
		// for attr
		int[] attrValCount = new int[attrVals.size()];
		
		// Store instances of each attr type in seperate lists for spec. cond. entropy
		Map<Integer, List<Instance>> instanceGroups = new HashMap<Integer, List<Instance>>();
		for(int i = 0; i < attrVals.size(); i++) {
			instanceGroups.put(i, new ArrayList<Instance>());
		}
		
		// Iterate through the instances
		// 1. summing attr values
		// 2. putting instances into specific mapped list
		for(Instance inst : instances) {
			attrValCount[inst.attributes.get(attr)]++;
			instanceGroups.get(inst.attributes.get(attr)).add(inst);
		}
		
		double conditionalEntropy = 0;
		for(int i = 0; i < attrVals.size(); i++) {
			List<Instance> group = instanceGroups.get(i);
			// Skip it if group is empty
			if(group.isEmpty()) {
				continue;
			}
			
			// cond entropy SUM(probability of value * specific Entropy given value)
			double probability = ((double)attrValCount[i] / (double)instances.size());
			double specEntropy = this.entropy(group, this.labels.size());
			conditionalEntropy += (probability*specEntropy);
		}
		return conditionalEntropy;
	}
	
	// Calculate the entropy of the labels in the data set
	// do so by:
	//	determining the probability of each different label
	// 	sum -(prob)log(prob) of each label
	private double entropy(List<Instance> instances, int numLabels) {
		int numberOfInstances = instances.size();
		// Get the counts for each label
		double[] labelProbabilities = new double[numLabels];
		for(Instance inst : instances) {
			labelProbabilities[inst.label]++;
		}

		// Divide through by the num of labels
		// then calculate the -p*log*p
		double entropyValue = 0;
		for(int i = 0; i < numLabels; i++) {
			double prob = labelProbabilities[i];
			if(prob == 0) {
				continue;
			}
			prob = prob/numberOfInstances;
			prob = (-prob)*(Math.log(prob)/Math.log(2));
			entropyValue += prob;
		}
		return entropyValue;
	}
	
	
	private List<Instance> subsetWithAttrValue(List<Instance> instances, int attr, int attrValue) {
		List<Instance> subset = new ArrayList<Instance>();
		for(Instance inst : instances) {
			if(inst.attributes.get(attr) == attrValue) {
				subset.add(inst);
			}
		}
		return subset;
	}
}
