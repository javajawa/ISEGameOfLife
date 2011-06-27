package ise.mace.actions;

/**
 * Send the food that an agent is allocated by a group to the agent
 * @author Benedict
 */
public class DistributeFood extends GenericAction
{

	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the Agent receiving food
	 */
	private String agent;
	/**
	 * The amount of food the agent succeeded in hunting
	 */
	private double amountHunted;
	/**
	 * Amount of food agent receives
	 */
	private double amountRecieved;

	/**
	 * Distributes food to agent
	 * @param agent agent receiving food
	 * @param amountHunted 
	 * @param amountReceived amount of food received
	 */
	public DistributeFood(String agent, double amountHunted, double amountReceived)
	{
		this.agent = agent;
		this.amountHunted = amountHunted;
		this.amountRecieved = amountReceived;
	}

	/**
	 * Gets the Agent receiving food
	 * @return agent receiving food
	 */
	public String getAgent()
	{
		return agent;
	}

	/**
	 * Gets amount of food agent will receive
	 * @return amount of food agent will receive
	 */
	public double getAmountHunted()
	{
		return amountHunted;
	}

	/**
	 * Returns the amount of food the agent succeeded in hunting
	 * @return The amount of food the agent succeeded in hunting
	 */
	public double getAmountRecieved()
	{
		return amountRecieved;
	}

}
