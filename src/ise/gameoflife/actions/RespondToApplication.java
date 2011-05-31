package ise.gameoflife.actions;

import presage.Action;

/**
 * TODO: Document this class
 * @author Benedict
 */
public class RespondToApplication implements Action
{
	private String agent;
	private boolean accepted;

	public RespondToApplication(String agent, boolean accepted)
	{
		this.agent = agent;
		this.accepted = accepted;
	}

	public String getAgent()
	{
		return agent;
	}

	public boolean wasAccepted()
	{
		return accepted;
	}

}
