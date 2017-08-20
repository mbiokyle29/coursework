/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 * 
 */

import java.util.*;


public class NNImpl{
	public ArrayList<Node> inputNodes=null;//list of the output layer nodes.
	public ArrayList<Node> hiddenNodes=null;//list of the hidden layer nodes
	public ArrayList<Node> nonBiasInputNodes=null;// *list of the output layer nodes minus the bias nodes
	public ArrayList<Node> nonBiasHiddenNodes=null;// *list of the hidden layer nodes minus the bias nodes
	
	public Node outputNode=null;//the output node
	
	public ArrayList<Instance> trainingSet=null;//the training set
	
	Double learningRate=1.0; // variable to store the learning rate
	int maxEpoch=1; // variable to store the maximum number of epochs
	
	/**
 	* This constructor creates the nodes necessary for the neural network
 	* Also connects the nodes of different layers
 	* After calling the constructor the last node of both inputNodes and  
 	* hiddenNodes will be bias nodes. The other nodes of inputNodes are of type
 	* input. The remaining nodes are of type sigmoid. 
 	*/
	
	public NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount, Double learningRate, int maxEpoch, Double [][]hiddenWeights, Double[] outputWeights)
	{
		this.trainingSet=trainingSet;
		this.learningRate=learningRate;
		this.maxEpoch=maxEpoch;
		
		//input layer nodes
		inputNodes=new ArrayList<Node>();
		int inputNodeCount=trainingSet.get(0).attributes.size();
		for(int i=0;i<inputNodeCount;i++)
		{
			Node node=new Node(0);
			inputNodes.add(node);
		}
		
		//bias node from input layer to output
		Node biasToHidden=new Node(1);
		inputNodes.add(biasToHidden);
		
		//hidden layer nodes
		hiddenNodes=new ArrayList<Node> ();
		for(int i=0;i<hiddenNodeCount;i++)
		{
			Node node=new Node(2);
			//Connecting hidden layer nodes with input layer nodes
			for(int j=0;j<inputNodes.size();j++)
			{
				NodeWeightPair nwp=new NodeWeightPair(inputNodes.get(j),hiddenWeights[i][j]);
				node.parents.add(nwp);
			}
			
			hiddenNodes.add(node);
		}
		
		//bias node from hidden layer to output
		Node biasToOutput=new Node(3);
		hiddenNodes.add(biasToOutput);
				
		//Output node
		outputNode=new Node(4);
		
		//Connecting hidden layer nodes with output node
		for(int i=0;i<hiddenNodes.size();i++)
		{
			NodeWeightPair nwp=new NodeWeightPair(hiddenNodes.get(i),outputWeights[i]);
			outputNode.parents.add(nwp);
		}
		
		// Keep a list without the bias nodes for simplicity
		this.nonBiasHiddenNodes = new ArrayList<Node>(this.hiddenNodes);
		this.nonBiasInputNodes = new ArrayList<Node>(this.inputNodes);
		this.nonBiasHiddenNodes.remove(this.nonBiasHiddenNodes.size()-1);
		this.nonBiasInputNodes.remove(this.nonBiasInputNodes.size()-1);
	}
	
	/**
	 * Get the output from the neural network for a single instance
	 * 
	 * The parameter is a single instance
	 */
	
	public Double calculateOutputForInstance(Instance inst)
	{
		// Make sure inst.attributes will fit in input nodes
		if(this.inputNodes.size() - 1 != inst.attributes.size()) {
			return -1.0;
		}
		
		// update the inputs
		this.updateInputForInstance(inst);
		
		// Iterate through the hidden layer inputs
		// Calculate their outputs
		for(Node hidden : this.hiddenNodes) { hidden.calculateOutput(); }
		
		this.outputNode.calculateOutput();

		return outputNode.getOutput();
	}
	
	/**
	 * Train the neural networks with the given parameters
	 * 
	 * The parameters are stored as attributes of this class
	 */
	
	public void train()
	{
		// TODO: add code here
		// Use the maxEpoch to control the outer loop
		for(int i = 0; i < this.maxEpoch; i++) {

			// Iterate through the instances
			for(Instance inst : this.trainingSet) {
				
				// Get the NN output and calculate the error
				double instOutput = this.calculateOutputForInstance(inst);
				
				// Calculate the new weights for the hidden to output layer
				List<Double> hiddenOutputWeights = this.calculateHiddenOutputLayerWeights(inst.classValue, instOutput);
				
				// Calculate the new weights for the input to hidden layer
				Map<Node, List<Double>> inputHiddenWeights = this.calculateInputHiddenLayerWeights(inst.classValue, instOutput);
				
				// Update the weights
				this.updateWeights(inputHiddenWeights, hiddenOutputWeights);
			}
		}
	}
	
	// Set the input nodes to the attr values for the instance
	private void updateInputForInstance(Instance inst) {
		// Iterate through the inst attrs, setting input value for the corresponding input node
		int index = 0;
		for(double attrVal : inst.attributes) {
			this.inputNodes.get(index).setInput(attrVal);
			index++;
		}
	}
	
	// Calculate the new weights for the edges from hidden to output
	private List<Double> calculateHiddenOutputLayerWeights(int teacherValue, double output) {
		
		// List to be returned
		List<Double> updatedWeights = new ArrayList<Double>();
		
		// Iterate through each hidden node
		// We can go through each hidden node since there is only one output node
		// This also simplifies the calculations since there is no kth output
		int index = 0;
		double deltaK = (teacherValue - output) * output * (1 - output);
		for(Node hidden : this.hiddenNodes) {
			double deltaWeight = this.learningRate * hidden.getOutput() * deltaK;
			double newWeight = this.outputNode.parents.get(index).weight + deltaWeight;
			updatedWeights.add(newWeight);
			index++;
		}
		
		return updatedWeights;
	}
	
	// Calculate the new weights for the edges from input to hidden
	// since each output node has an edge from all the input nodes this returns a map
	private Map<Node, List<Double>> calculateInputHiddenLayerWeights(int teacherValue, double output) {
		
		// List to be returned
		Map<Node, List<Double>> updatedWeightMap = new HashMap<Node, List<Double>>();
		
		// For input nodes we have to iterate through each hidden nodes parents
		// Since there are multiple connections
		// Generate a list of updated weights for each hidden node
		// key that list into the map with its corresponding node
		int index = 0;
		for(Node hidden : this.nonBiasHiddenNodes) {
			
			// weight list
			List<Double> updatedWeights = new ArrayList<Double>();
			for(NodeWeightPair inputWP : hidden.parents) {
				
				/// α*ai*aj*(1− a j )*∑ wj,k(Tk − Ok)Ok(1−Ok)
				// No need to sum since only one output
				double errorTerm = (teacherValue - output) * output * (1 - output);
				double hiddenToOutputWeight = this.outputNode.parents.get(index).weight;
				double activationTerm = hidden.getOutput() * (1 - hidden.getOutput());
				double deltaJ = activationTerm * hiddenToOutputWeight * errorTerm;
				double deltaWeight = this.learningRate * inputWP.node.getOutput() * deltaJ;
				double newWeight = inputWP.weight + deltaWeight;
				updatedWeights.add(newWeight);
			}
			
			// Add the list to the map
			updatedWeightMap.put(hidden, updatedWeights);
			index++;
		}
		return updatedWeightMap;
	}
	
	// Actually set the new weights
	// dont add the values to the current, that is already done
	private void updateWeights(Map<Node, List<Double>> inputHiddenWeights, List<Double> hiddenOutputWeights) {
		// Update the input ---> hidden
		for(Node hidden : this.nonBiasHiddenNodes) {
			List<Double> weightsValues = inputHiddenWeights.get(hidden);
			int index = 0;
			for(double weight : weightsValues) {
				hidden.parents.get(index).setWeight(weight);
				index++;
			}
		}
		
		// Update the hidden ---> output
		int index = 0;
		for(double weight : hiddenOutputWeights) {
			this.outputNode.parents.get(index).setWeight(weight);
			index++;
		}
	}
	
	@Override
	// For debugging purposes only
	//  calls other overloaded toString()
	public String toString() 
	{
		String output = "NN To String\n";
		output = output.concat("Epoch: "+this.maxEpoch+"\n");
		output = output.concat("Learn Rate: "+this.learningRate+"\n");
		output = output.concat("Input Nodes:\n");
		for(Node input : this.inputNodes) {
			output = output.concat(input.toString());
		}
		
		output = output.concat("Hidden Nodes:\n");
		for(Node input : this.hiddenNodes) {
			output = output.concat(input.toString());
		}
		
		output = output.concat(this.outputNode.toString());
		return output;
	}
}