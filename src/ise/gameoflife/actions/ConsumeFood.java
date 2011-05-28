package ise.gameoflife.actions;

import java.util.UUID;
import presage.Input;

/**
 * Used to initiate an action telling the agent to consume an amount of food
 * specified by the environment controller
 * @author christopherfonseka
 */
public class ConsumeFood implements Input
{
	
	private UUID id;
	
	/**
	 * Creates a consume food action which indicates an agent is to consume a food
	 * of type id, which is determined by the environment controller.
	 * 
	 * @param identification 
	 */
	public ConsumeFood(UUID identification) {
		this.id = identification;
	}
	
	/**
	 * Returns the UUID of of the food to be consumed
	 * @return 
	 */
	public UUID getId()
	{
		return id;
	}

	@Override
	public String getPerformative()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public long getTimestamp()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setTimestamp(long timestamp)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
