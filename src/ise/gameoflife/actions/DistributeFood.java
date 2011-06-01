package ise.gameoflife.actions;

import presage.Action;

/**
 * TODO: Document this class
 * @author Benedict
 */
public class DistributeFood implements Action
{
	private String agent;
	private double amount;

	public DistributeFood(String agent, double amount)
	{
		this.agent = agent;
		this.amount = amount;
	}

	public String getAgent()
	{
		return agent;
	}

	public double getAmount()
	{
		return amount;
	}

}
