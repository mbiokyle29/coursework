import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Do not modify.
 * 
 * This is the class with the main function
 */

public class HW4{
	/**
	 * Runs the tests for HW3
	*/
	public static void main(String[] args)
	{
		final Double threshold=0.5;
		
		//Checking for correct number of arguments
		if (args.length < 7) 
		{
			System.out.println("usage: java HW4 <modeFlag> <noHiddenNode> " +
					"<learningRate> <maxEpoch> <trainFile> <weightFile> <testFile>");
			System.exit(-1);
		}
		 
		/*
		 * mode 0 : print the output of each training instance for the initial neural network
		 *      1 : print the modified weights of the neural network after training it using 
		 *          the first training instance for one epoch
		 *      2 : print the modified weights of the neural network after training it using
		 * 		 all the training instances for maxEpoch
		 *      3 : train the neural network with all the training instances for maxEpoch 
		 *			 and then print output of each test instance
		*/
		int mode = Integer.parseInt(args[0]);
		if (0 > mode || mode > 3) 
		{
			 System.out.println("mode must be between 0 and 3");
			 System.exit(-1);
		}
		
		//Reading the training set 	
		ArrayList<Instance> trainingSet=getData(args[4]);
		
		
		//Reading the weights
		Double[][] hiddenWeights=new Double[Integer.parseInt(args[1])][];
		
		for(int i=0;i<hiddenWeights.length;i++)
		{
			hiddenWeights[i]=new Double[trainingSet.get(0).attributes.size()+1];
		}
		
		Double [] outputWeights=new Double[Integer.parseInt(args[1])+1];
		
		readWeights(hiddenWeights,outputWeights, args[5]);
		
		Double learningRate=Double.parseDouble(args[2]);
		
		if(learningRate>1 || learningRate<=0)
		{
			System.out.println("Incorrect value for learning rate\n");
			System.exit(-1);
		}
		
		//Creating and training the neural network according to the weights.
		if(mode==0)
		{
			NNImpl nn=new NNImpl(trainingSet,Integer.parseInt(args[1]),learningRate,Integer.parseInt(args[3]), hiddenWeights,outputWeights);
			
			Double[] outputs=new Double[trainingSet.size()];
			
			System.out.println("Output ThresholdedOutput");
			
			for(int i=0;i<trainingSet.size();i++)
			{
				//Getting output from network
				outputs[i]=nn.calculateOutputForInstance(trainingSet.get(i));
				
				//Thresholding
				int thresholdedOutput=1;
				
				if(outputs[i]<threshold)
				{
					thresholdedOutput=0;
				}
				
				//The output
				System.out.format("%.5f %d\n",outputs[i], thresholdedOutput);
			}
		}
		else if (mode==1)
		{
			ArrayList<Instance> tSet=new ArrayList<Instance>();
			tSet.add(trainingSet.get(0));
			
			NNImpl nn=new NNImpl(tSet,Integer.parseInt(args[1]),learningRate,1, hiddenWeights,outputWeights);
			nn.train();
			System.out.println("Weights from input to hidden layer");
			ArrayList<Node> hiddenNodes=nn.hiddenNodes;
			
			for(int i=0;i<hiddenNodes.size()-1;i++)
			{
				ArrayList<NodeWeightPair> parents=hiddenNodes.get(i).parents;
				for(int j=0;j<parents.size();j++)
				{
						System.out.format("%.5f\n",parents.get(j).weight);		
				}
			}
			
			System.out.println("Weights from hidden layer to output layer");
			
			for(int i=0;i<nn.outputNode.parents.size();i++)
			{
				System.out.format("%.5f\n",nn.outputNode.parents.get(i).weight);		
			}
			
		}
		else if(mode==2)
		{
			NNImpl nn=new NNImpl(trainingSet,Integer.parseInt(args[1]),Double.parseDouble(args[2]),Integer.parseInt(args[3]), hiddenWeights,outputWeights);
			nn.train();
			System.out.println("Weights from input to hidden layer");
			ArrayList<Node> hiddenNodes=nn.hiddenNodes;
			
			for(int i=0;i<hiddenNodes.size()-1;i++)
			{
				ArrayList<NodeWeightPair> parents=hiddenNodes.get(i).parents;
				for(int j=0;j<parents.size();j++)
				{
						System.out.format("%.5f\n",parents.get(j).weight);		
				}
			}
			
			System.out.println("Weights from hidden layer to output layer");
			
			for(int i=0;i<nn.outputNode.parents.size();i++)
			{
				System.out.format("%.5f\n",nn.outputNode.parents.get(i).weight);		
			}
		}
		else if(mode==3)
		{
			NNImpl nn=new NNImpl(trainingSet,Integer.parseInt(args[1]),Double.parseDouble(args[2]),Integer.parseInt(args[3]), hiddenWeights,outputWeights);
			nn.train();
			
			//Reading the training set 	
			ArrayList<Instance> testSet=getData(args[6]);
			
			Double[] outputs=new Double[testSet.size()];
			
			System.out.println("Output ThresholdedOutput");
			
			int correct=0;
			for(int i=0;i<testSet.size();i++)
			{
				//Getting output from network
				outputs[i]=nn.calculateOutputForInstance(testSet.get(i));
				
				//Thresholding
				int thresholdedOutput=1;
				
				if(outputs[i]<threshold)
				{
					thresholdedOutput=0;
				}
				
				if(thresholdedOutput==testSet.get(i).classValue)
				{
					correct++;
				}
				
				//The output
				System.out.format("%.5f %d\n",outputs[i], thresholdedOutput);
			}
			
			System.out.println("Total instances: " + testSet.size());
			System.out.println("Correctly classified: "+correct);
			
		}
		else
		{
			System.out.println("Incorrect value of mode given\n");
		}
		
	}
	//Reads a file and gets the list of instances
	private static ArrayList<Instance> getData(String file)
	{
		ArrayList<Instance> data=new ArrayList<Instance>();
		BufferedReader in;
		int attributeCount=0;
		
		try{
			in = new BufferedReader(new FileReader(file));
			while (in.ready()) { 
				String line = in.readLine(); 	
				String prefix = line.substring(0, 2);
				if (prefix.equals("//")) {
				} 
				else if (prefix.equals("##")) {
					attributeCount=Integer.parseInt(line.substring(2));
				} else {
					String[] vals=line.split(",");
					Instance inst=new Instance(Integer.parseInt(vals[0]));
					
					for(int i=1;i<vals.length;i++)
					{
						inst.attributes.add(Double.parseDouble(vals[i]));
					}
					
					data.add(inst);
					
				}
				
			}
			in.close();
			return data;
			
		}catch(Exception e)
		{
			System.out.println("Could not read instances: "+e);
		}
		
		return null;
	}
	//Reads a file and gets a list of weights
	public static void readWeights(Double [][]hiddenWeights, Double[]outputWeights, String file)
	{
		BufferedReader in;
		
		try{
			in = new BufferedReader(new FileReader(file));
			
			for(int i=0;i<hiddenWeights.length;i++)
			{
				for(int j=0;j<hiddenWeights[i].length;j++)
				{
					hiddenWeights[i][j]=Double.parseDouble(in.readLine());
				}
			}
				
			for(int i=0;i<outputWeights.length;i++)
			{
				outputWeights[i]=Double.parseDouble(in.readLine());
			}	
			
			in.close();	
		}catch(Exception e)
		{
			System.out.println("Error reading weights: " + e);
		}
	}
}