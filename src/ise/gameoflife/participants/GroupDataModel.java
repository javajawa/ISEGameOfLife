package ise.gameoflife.participants;

import ise.gameoflife.History;
import ise.gameoflife.UnmodifableHistory;
import ise.gameoflife.models.GroupDataInitialiser;
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
	
	@Element
	private String name;

	/**
	 * Array list of GroupDataModel members
	 */
	@ElementList
	private ArrayList<String> memberList;

	@Element
	private History<Double> econmoicPosition;

	@Deprecated
	GroupDataModel()
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
		ret.name = init.getName();
		ret.econmoicPosition = new History<Double>(50);
		ret.econmoicPosition.setValue(init.getInitialEconomicBelief());
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

	double getCurrentEconomicPoisition()
	{
		return econmoicPosition.getValue();
	}

	void shiftEconomicPosition(double amount)
	{
		double old = econmoicPosition.getValue();
		econmoicPosition.setValue(old + amount);
	}

	UnmodifableHistory<Double> getEconomicPoisition()
	{
		return econmoicPosition.getUnmodifableHistory();
	}

	void clearRoundData()
	{
		econmoicPosition.newEntry(true);
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

	public String getName()
	{
		return name;
	}

}
