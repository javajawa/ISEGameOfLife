package ise.gameoflife.actions;

import presage.Action;

/**
 * Allows the Environment to respond to Application request inputs
 * @author Benedict
 */
public class RespondToApplication extends GenericAction
{
	/**
	 * Which agent dares send me an application?
	 */
	private String agent;
	
	/**
	 * Is the environment in a good mood today?
	 */
	private boolean accepted;

	/**
	 * Sets everything up
	 * @param agent
	 * @param accepted 
	 */
	public RespondToApplication(String agent, boolean accepted)
	{
		this.agent = agent;
		this.accepted = accepted;
	}

	/**
	 * @return the agent requesting something
	 */
	public String getAgent()
	{
		return agent;
	}

	/**
	 * @return whether or not the application has been accepted
	 */
	public boolean wasAccepted()
	{
		return accepted;
	}

}
