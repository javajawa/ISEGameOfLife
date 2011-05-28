package ise.gameoflife.inputs;

import java.util.UUID;

/**
 *
 * @author Olly
 */
public class HuntResult extends GenericInput
{
	
	private double nutrition;
	
	/**
	 * Creates a new instance of the HuntResult class, which gives the result of a 
	 * Hunt action.
	 * @param id THe AuthToken of the target, to autheticate that the sender has
	 * the right to send this Input
	 * @param nutrition The amount of food gained in this hunt
	 * @param time The simulation time at which this result occurred 
	 */
	public HuntResult(UUID id, double nutrition, long time){
		super(id, time, "huntresult:" + nutrition);
		this.nutrition = nutrition;
	}
	/**
	 * Returns nutrition value of food hunted
	 * @return 
	 */
	public double getNutritionValue(){
		return nutrition;
	}
}
