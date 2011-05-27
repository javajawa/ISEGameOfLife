package ise.gameoflife.models;

import ise.gameoflife.AbstractAgent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

/**
 * Stub for groups
 * @author Olly
 */
public class Group implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 *Unique group identifier
	 */
	@Element
	protected UUID groupID;
	/**
	 * Identity of Group Leader
	 */
	@Element
	protected String leader;
	/**
	 * Array list of Group members
	 */
	@ElementList
	protected ArrayList<String>  memberList;

	/**
	 * Adds a specific Agent to a Group
	 * @param agent The agent to add to the group
	 */
	protected	void addAgentToGroup(AbstractAgent agent){
		memberList.add(agent.getId());
	}
	/**
	 * Removes specific Agent from a Group if Agent is member of that group
	 * Returns false if Agent was not member of the group.
	 * @param agent The agent to remove from the group
	 * @return Whether an agent was removed
	 */
	protected boolean removeAgentFromGroup(AbstractAgent agent){
		return memberList.remove(agent.getId());
	}

	/**
	 * Get the group id that this group has
	 * @return The UUID of this group
	 */
	public UUID getId()
	{
		return groupID;
	}
}
