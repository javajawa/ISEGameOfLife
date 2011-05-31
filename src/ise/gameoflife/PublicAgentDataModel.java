package ise.gameoflife;

import java.util.ArrayList;
import presage.PlayerDataModel;

import java.io.Serializable;

/**
 * The amount of information an Agent can find out about another agent
 * @author Benedict Harcourt
 */
public final class PublicAgentDataModel implements PlayerDataModel, Serializable
{
	private static final long serialVersionUID = 1L;

	private AgentDataModel source;

	PublicAgentDataModel(AgentDataModel source)
	{
		this.source = source;
	}

	/**
	 * Gets the id of the agent
	 * @return the id of the agent
	 */
	public String getId()
	{
		return source.getId();
	}

	/**
	 * Gets the amount of food this agent has available to them
	 * @return the amount of food this agent has available to them
	 */
	public double getFoodAmount()
	{
		return source.getFoodInPossesion();
	}

	@Override
	public long getTime()
	{
		return source.getTime();
	}

	@Override
	public void setTime(long time)
	{
		source.setTime(time);
	}

	@Override
	public ArrayList<String> getRoles()
	{
		return source.getRoles();
	}

	@Override
	public void setRoles(ArrayList<String> roles)
	{
		source.setRoles(roles);
	}

	@Override
	public String getPlayerClass()
	{
		return source.getPlayerClass();
	}
}
