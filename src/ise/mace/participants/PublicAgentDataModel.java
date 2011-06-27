package ise.mace.participants;

import ise.mace.models.Food;
import ise.mace.models.History;
import ise.mace.models.HuntingTeam;
import ise.mace.tokens.AgentType;
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
	/**
	 * Data Model which backs this public data model
	 */
	private AgentDataModel source;

	/**
	 * Constructor for PublicAgentDataModel
	 * @param source the Data model which backs the public data model
	 */
	PublicAgentDataModel(AgentDataModel source)
	{
		this.source = source;
	}

	/**
	 * Gets the id of the agent
	 * @return the id of the agent
	 */
	@Override
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

	/**
	 * Gets time: the number of cycles (which are the subdivision of rounds) which have passed
	 * @return the time (number of cycles which have passed)
	 */
	@Override
	public long getTime()
	{
		return source.getTime();
	}

	/**
	 * Sets the time
	 * setTime should not used since the simulation deals with setting the time - calling this will have no effect.
	 * @param time the time to set
	 */
	@Override
	public void setTime(long time)
	{
		source.setTime(time);
	}

	/**
	 * Returns the list of roles which the agent has
	 * @return a list of roles
	 */
	@Override
	public ArrayList<String> getRoles()
	{
		return source.getRoles();
	}

	/**
	 * Sets all of the agents roles
	 * @param roles the list of roles to assign to the agent
	 */
	@Override
	public void setRoles(ArrayList<String> roles)
	{
		source.setRoles(roles);
	}

        /**
	 * Implemented in Presage and (currently) serves no purpose
	 * @return a null string, at the moment
	 */
	@Override
	public String getPlayerClass()
	{
		return source.getPlayerClass();
	}

	/**
	 * @return The food the agent decided to hunt on the previous turn
	 */
	public Food getLastHunted()
	{
		return source.getLastHunted();
	}

	/**
	 * @return The hunting history of this agent
	 */
	public History<Food> huntingHistory()
	{
		return source.getHuntingHistory();
	}

	/**
	 * @return which hunting pair this agent belongs to
	 */
	public HuntingTeam getHuntingTeam()
	{
		return source.getHuntingTeam();
	}

	/**
	 * @return The Team history of this agent
	 */
	public History<HuntingTeam> getTeamHistory()
	{
		return source.getTeamHistory();
	}

	/**
	 * Gets the group ID of the agent
	 * @return the group ID of the agent
	 */
	public String getGroupId()
	{
		return source.getGroupId();
	}

	/**
	 * Gets the current happiness of this agent
	 * @return The current happiness of this agent
	 */
	public Double getCurrentHappiness()
	{
		return source.getCurrentHappiness();
	}

	/**
	 * Gets the history of this agent's happiness
	 * @return The history of this agent's happiness
	 */
	public History<Double> getHappinessHistory()
	{
		return source.getHappinessHistory();
	}

	/**
	 * Gets the current loyalty of this agent
	 * @return The current loyalty of this agent
	 */
	public Double getCurrentLoyalty()
	{
		return source.getCurrentLoyalty();
	}

	/**
	 * Gets the history of this agent's loyalty
	 * @return The history of this agent's loyalty
	 */
	public History<Double> getLoyaltyHistory()
	{
		return source.getLoyaltyHistory();
	}
	/**
	 * Gets the current Economic Belief of the agent
	 * @return The agent's current Economic belief
	 */
	public double getEconomicBelief()
	{
		return source.getEconomicBelief();
	}

	/**
	 * Gets the generate person name and class name of this agent
	 * @return The generate person name and class name of this agent
	 */
	public String getName()
	{
		return source.getName();
	}

	/**
	 * Gets the hunting history of this agents
	 * @return The hunting history of this agents
	 */
	public History<Food> getHuntingHistory()
	{
		return source.getHuntingHistory();
	}

	public Double getTrust(String agent)
	{
		return source.getTrust(agent);
	}

	/**
	 * @return the socialBelief
	 */
	public double getSocialBelief()
	{
		return source.getSocialBelief();
	}

	/**
	 * @return the socialBelief
	 */
	public AgentType getAgentType()
	{
		return source.getAgentType();
	}
        //Theo ADDED
        /**
         * Sets the agents type
         * @param enum type to assign to agent
         */
	public void setAgentType(AgentType type)
	{
		source.setAgentType(type);
	}
}
