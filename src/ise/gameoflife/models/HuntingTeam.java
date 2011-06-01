package ise.gameoflife.models;

import ise.gameoflife.enviroment.EnvConnector;
import java.util.Collections;
import java.util.List;

/**
 * Class dealing with Hunting Teams - ie, sub-groups of Agents who play the 
 * Stag Hunt game
 * @author Benedict Harcourt
 */
public class HuntingTeam
{
	private List<String> members;
	private Food orderedTarget;

	/**
	 * Creates a new Hunting team
	 * Note that such teams are a subset of a group, and that agents cannot be in
	 * more than one team at once.
	 * @param ec
	 * @param members
	 * @param orderedTarget
	 */
	public HuntingTeam(EnvConnector ec, List<String> members, Food orderedTarget)
	{
		// TODO: Using the EnvConnector, check that all members of memebers are 
		// hunters before committing it to the object
		this.members = Collections.unmodifiableList(members);
		this.orderedTarget = orderedTarget;
	}

	/**
	 * Returns the target the hunter was told to hunt
	 * @return
	 */
	public Food getOrderedTarget()
	{
		return orderedTarget;
	}

	/**
	 * Returns list of members in a team
	 * @return
	 */
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public List<String> getMembers()
	{
		return members;
	}
	
}
