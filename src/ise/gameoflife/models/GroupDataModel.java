package ise.gameoflife.models;

import ise.gameoflife.participants.AbstractAgent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
	 * Create a new instance of the GroupDataModel, automatically generating a new
	 * UUID
	 * @param randomseed The random number seed to use with this class
	 * @return The new GroupDataModel
	 */
	public static GroupDataModel createNew(long randomseed)
	{
		GroupDataModel ret = new GroupDataModel();
		ret.myId = UUID.randomUUID().toString();
		ret.memberList = new ArrayList<String>();
		ret.myrolesString = "<group>";
		ret.randomseed = randomseed;
		return ret;
	}

	/**
	 * Get the group id that this group has
	 * @return The UUID of this group
	 */
	@Override
	public String getId()
	{
		return myId;
	}

	public List<String> getMemberList()
	{
		return Collections.unmodifiableList(memberList);
	}

	@Override
	public void onInitialise()
	{
		// Nothing to see here. Move along, citizen!
	}
}
