package ise.gameoflife.actions;

import java.util.UUID;
import presage.Action;

/**
 *
 * @author christopherfonseka
 */
public class ConsumeFood implements Action
{
	
	private UUID id;
	
	/**
	 * Creates a consume food action which indicates an agent is to consume a food
	 * of type id, which is determined by the environment controller.
	 */
	public ConsumeFood(UUID identification) {
		this.id = identification;
	}

	public UUID getId()
	{
		return id;
	}
	
}
