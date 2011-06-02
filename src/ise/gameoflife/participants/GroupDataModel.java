package ise.gameoflife.participants;

import ise.gameoflife.models.GroupDataInitialiser;
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
class GroupDataModel extends APlayerDataModel
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Array list of GroupDataModel members
	 */
	@ElementList
	private ArrayList<String> memberList;

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
	@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
	public static GroupDataModel createNew(GroupDataInitialiser init)
	{
		GroupDataModel ret = new GroupDataModel();
		ret.myId = UUID.randomUUID().toString();
		ret.memberList = new ArrayList<String>();
		ret.myrolesString = "<group>";
		ret.randomseed = init.getRandomSeed();
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

	List<String> getMemberList()
	{
		return Collections.unmodifiableList(memberList);
	}

	void addMember(String a)
	{
		memberList.add(a);
	}

	void removeMember(String a)
	{
		memberList.remove(a);
	}

	@Override
	public void onInitialise()
	{
		// Nothing to see here. Move along, citizen!
	}
        
	/**
	 * Get a re-distribution safe copy of this object. The returned object is
	 * backed by this one, so their is no need to keep calling this to receive
	 * updated data.
	 * @return
	 */
	public PublicGroupDataModel getPublicVersion()
	{
		return new PublicGroupDataModel(this);
	}
}
