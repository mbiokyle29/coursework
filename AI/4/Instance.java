import java.util.*;
/**
 * Holds data for particular instance.
 * Attributes are represented as an ArrayList of Doubles
 * The class value is represented as an int
 * Do not modify
 */
 

public class Instance{
	public ArrayList<Double> attributes;
	public int classValue;
	
	//Create an Instance with a particular class value
	//The attributes are added separately
	public Instance(int classValue)
	{
		attributes=new ArrayList<Double>();
		this.classValue=classValue;
	}
	
}