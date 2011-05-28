package ise.gameoflife.inputs;

import ise.gameoflife.inputs.GenericInput;
import java.util.UUID;
import presage.Input;

/**
 * Used to initiate an action telling the agent to consume an amount of food
 * specified by the environment controller
 * @author christopherfonseka
 */
public class ConsumeFood extends GenericInput
{
	
	/**
	 * Creates a consume food action which indicates an agent is to consume a food
	 * of type id, which is determined by the environment controller.
	 * @param identification The authentication token of the target agent, to
	 * verify the senders authenticity
	 * @param time  
	 */
	public ConsumeFood(long time) {
		super(time, "consumefood");
	}

}
