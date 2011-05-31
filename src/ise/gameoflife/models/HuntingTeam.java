package ise.gameoflife.models;

import ise.gameoflife.enviroment.EnvConnector;
import java.util.Collections;
import java.util.List;

/**
 * TODO: Documentation
 * @author Benedict Harcourt
 */
public class HuntingTeam
{
	private List<String> members;
	private Food orderedTarget;

	/**
	 * TODO: Documentation
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
	 * TODO: Documentation
	 * @return
	 */
	public Food getOrderedTarget()
	{
		return orderedTarget;
	}

	/**
	 * TODO: Documentation
	 * @return
	 */
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public List<String> getMembers()
	{
		return members;
	}
	
}
