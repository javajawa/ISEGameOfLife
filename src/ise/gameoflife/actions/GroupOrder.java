package ise.gameoflife.actions;

import ise.gameoflife.models.HuntingTeam;

/**
 * An Action performed by Group that represents the Group putting an agent in
 * a team, and the order that that team is given
 * @author Benedict Harcourt
 */
public class GroupOrder extends GenericAction
{

	private static final long serialVersionUID = 1L;
	private final HuntingTeam withTeam;
	private final String agent;

	/**
	 * Creates an Order
	 * @param withTeam The team the agent is to be a part of
	 * @param agent The agent that this order is directed at
	 */
	public GroupOrder(HuntingTeam withTeam, String agent)
	{
		this.withTeam = withTeam;
		this.agent = agent;
	}

	/**
	 * Gets the team the agent is to hunt with
	 * @return Gets the team the agent is to hunt with
	 */
	public HuntingTeam getTeam()
	{
		return withTeam;
	}

	/**
	 * Gets the agent this order is sent to
	 * @return The agent this order is sent to
	 */
	public String getAgent()
	{
		return agent;
	}

}
