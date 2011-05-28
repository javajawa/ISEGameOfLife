package ise.gameoflife.inputs;

/**
 * Used to initiate an action telling the agent to consume an amount of food
 * specified by the environment controller
 * @author christopherfonseka
 */
public final class ConsumeFood extends GenericInput
{
	
	/**
	 * Creates a consume food action which indicates an agent is to consume a food
	 * of type id, which is determined by the environment controller.
	 * @param time The simulation time at which this input occurs
	 */
	public ConsumeFood(long time) {
		super(time, "consumefood");
	}

}
