package ise.gameoflife.models;

import ise.gameoflife.environment.EnvConnector;
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
	public HuntingTeam(List<String> members)
	{
		this.members = Collections.unmodifiableList(members);
		this.orderedTarget = orderedTarget;
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
