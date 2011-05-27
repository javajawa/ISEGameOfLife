package ise.gameoflife.models;

import ise.gameoflife.AbstractAgent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import org.simpleframework.xml.Element;
import java.lang.String;
import org.simpleframework.xml.ElementList;

/**
 * Stub for groups
 * @author Olly
 */
public class Group implements Serializable
{
	
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
	 * 
	 */
	protected	void addAgentToGroup(AbstractAgent agent){
		memberList.add(agent.getId());
	}
	/**
	 * Removes specific Agent from a Group if Agent is member of that group
	 * Returns false if Agent was not member of the group.
	 */
	protected boolean removeAgentFromGroup(AbstractAgent agent){
		return memberList.remove(agent.getId());
	}
}
