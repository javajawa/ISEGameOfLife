package ise.mace.actions;

/**
 * Allows Groups to respond to ApplicationRequest inputs
 * @author Benedict Harcourt
 */
public class RespondToApplication extends GenericAction
{

	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Which agent dares send me an application?
	 */
	private String agent;
	/**
	 * Is the group in a good mood today?
	 */
	private boolean accepted;

	/**
	 * Sets everything up
	 * @param agent The agent that applied
	 * @param accepted Whether they were accepted
	 */
	public RespondToApplication(String agent, boolean accepted)
	{
		this.agent = agent;
		this.accepted = accepted;
	}

	/**
	 * Gets the agent requesting to join
	 * @return the agent requesting to join
	 */
	public String getAgent()
	{
		return agent;
	}

	/**
	 * Returns whether or not the application was accepted
	 * @return whether or not the application was accepted
	 */
	public boolean wasAccepted()
	{
		return accepted;
	}

}
