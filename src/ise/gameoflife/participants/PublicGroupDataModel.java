package ise.gameoflife.participants;

import ise.gameoflife.UnmodifableHistory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import presage.PlayerDataModel;

/**
 * FIXME: Add useful functions to this
 * Used by objects to access data about a desired group
 * @author Benedict
 */
public class PublicGroupDataModel implements PlayerDataModel, Serializable
{

	private static final long serialVersionUID = 1L;

	private GroupDataModel source;

	PublicGroupDataModel(GroupDataModel source)
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
	 * Gets the number of cycles which have passed
	 * @return the number of cycles passed
	 */
	@Override
	public long getTime()
	{
		return source.getTime();
	}

	/**
	 * Sets the number of cycles passed
	 * @param time 
	 */
	@Override
	public void setTime(long time)
	{
		source.setTime(time);
	}

	/**
	 * Gets a list of all roles this group has
	 * @return a list of all roles in the group
	 */
	@Override
	public ArrayList<String> getRoles()
	{
		return source.getRoles();
	}

	/**
	 * Sets the list of all roles in this group
	 * @param roles 
	 */
	@Override
	public void setRoles(ArrayList<String> roles)
	{
		source.setRoles(roles);
	}

	/**
	 * Defined in presage, has no purpose
	 * @return Unused value
	 */
	@Override
	public String getPlayerClass()
	{
		return source.getPlayerClass();
	}

	/**
	 * Gets a list of all the members in the group
	 * @return a list of all members in the group
	 */
	public List<String> getMemberList()
	{
		return source.getMemberList();
	}

	/**
	 * Gets the current economic position of the group
	 * @return The current economic position of the group
	 */
	public double getCurrentEconomicPoisition()
	{
		return source.getCurrentEconomicPoisition();
	}

	/**
	 * Gets the historical economic positions of the group
	 * @return The historical economic positions of the group
	 */
	public UnmodifableHistory<Double> getEconomicPoisition()
	{
		return source.getEconomicPoisition();
	}

}
