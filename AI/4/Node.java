/**
 * Class for internal organization of a Neural Network.
 * There are 5 types of nodes. Check the type attribute of the node for details
 * 
 * Do not modify. 
 * 
 * 
 */


import java.util.*;

public class Node{
	private int type=0; //0=input,1=biasToHidden,2=hidden,3=biasToOutput,4=Output
	public ArrayList<NodeWeightPair> parents=null; //Array List that will contain the parents (including the bias node) with weights if applicable
		 
	private Double inputValue=0.0;
	private Double outputValue=0.0;
	
	//Create a node with a specific type
	public Node(int type)
	{
		if(type>4 || type<0)
		{
			System.out.println("Incorrect value for node type");
			System.exit(1);
		}
		else
		{
			this.type=type;
		}
		
		if (type==2 || type==4)
		{
			parents=new ArrayList<NodeWeightPair>();
		}
	}
	
	//For an input node sets the input value which will be the value of a particular attribute
	public void setInput(Double inputValue)
	{
		if(type==0)//If input node
		{
			this.inputValue=inputValue;
		}
	}
	
	/**
	 * Calculate the output of a sigmoid node.
	 * You can assume that outputs of the parent nodes have already been calculated
	 * You can get this value by using getOutput()
	 * @param train: the training set
	 */
	public void calculateOutput()
	{	
		if(type==2 || type==4)//Not an input or bias node
		{
			
			// Sum the product of parent weights * their outputs
			double weightValueSummation = 0;
			for(NodeWeightPair parentPair : this.parents) {
				double product = parentPair.weight*parentPair.node.getOutput();
				weightValueSummation += product;
			}
			
			// Calculate the sigmoid
			weightValueSummation = -weightValueSummation;
			double sigmoid = 1 / (1 + Math.exp(weightValueSummation));
			this.outputValue = sigmoid;
		}
	}
	
	//Gets the output value
	public double getOutput()
	{
		
		if(type==0)//Input node
		{
			return inputValue;
		}
		else if(type==1 || type==3)//Bias node
		{
			return 1.00;
		}
		else
		{
			return outputValue;
		}
		
	}
	
	@Override
	public String toString() {
		String output = "---- Node ----\n";
		output = output.concat("Type: "+this.type+"\n Output:"+this.getOutput()+"\n");
		if(this.parents != null) {
			output = output.concat("Parents: \n");
			output = output.concat(this.parents.toString()+"\n");
		}
		return output;
	}
}