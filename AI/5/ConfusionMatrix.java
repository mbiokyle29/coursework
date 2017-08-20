//Class to display the confusion matrix

public class ConfusionMatrix{
	public int truePositives;
	public int trueNegatives;
	public int falsePositives;
	public int falseNegatives;
	
	//The constructor
	public ConfusionMatrix(int truePositives, int trueNegatives, int falsePositives, int falseNegatives)
	{
		this.truePositives=truePositives;
		this.trueNegatives=trueNegatives;
		this.falsePositives=falsePositives;
		this.falseNegatives=falseNegatives;
	}
	
	//Prints the table
	public void print()
	{
		System.out.println("The Confusion Matrix:\n\n");
		System.out.println("                  True Spam | True Ham");
		System.out.print("Classified Spam ");
		System.out.format("%11d | %8d\n",truePositives,falsePositives);
		System.out.print("Classified Ham ");
		System.out.format("%12d | %8d\n",falseNegatives,trueNegatives);
	}
}