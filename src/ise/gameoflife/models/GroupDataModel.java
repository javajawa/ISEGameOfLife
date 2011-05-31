package ise.gameoflife.models;

import ise.gameoflife.participants.AbstractAgent;
import java.util.ArrayList;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import presage.abstractparticipant.APlayerDataModel;

/**
 * Stub for groups
 * @author Olly
 */
public class GroupDataModel extends APlayerDataModel
{
	private static final long serialVersionUID = 1L;
	
	/**
	 *Unique group identifier
	 */
	@Element
	protected String groupID;
	/**
	 * Array list of GroupDataModel members
	 */
	@ElementList
	public ArrayList<String> memberList;

	@Deprecated
	public GroupDataModel()
	{
		super();
	}

	/**
	 * Get the group id that this group has
	 * @return The UUID of this group
	 */
	@Override
	public String getId()
	{
		return groupID;
	}

	@Override
	public void onInitialise()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
