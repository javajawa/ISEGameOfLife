package ise.gameoflife.actions;

import presage.Action;

/**
 * Distributes food amongst agents
 * @author Benedict
 */
public class DistributeFood implements Action
{
	/**
	 * the Agent receiving food
	 */
	private String agent;
	/**
	 * Amount of food agent receives
	 */
	private double amount;

	/**
	 * Distributes food to agent
	 * @param agent agent receiving food
	 * @param amount amount of food received
	 */
	public DistributeFood(String agent, double amount)
	{
		this.agent = agent;
		this.amount = amount;
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
	public double getAmount()
	{
		return amount;
	}

}
