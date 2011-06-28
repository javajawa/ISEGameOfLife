package ise.mace.participants;

import ise.mace.environment.PublicEnvironmentConnection;
import ise.mace.inputs.Proposition;
import ise.mace.models.History;
import ise.mace.models.UnmodifiableHistory;
import ise.mace.models.GroupDataInitialiser;
import ise.mace.models.ScaledDouble;
import ise.mace.models.Tuple;
import ise.mace.tokens.AgentType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import presage.abstractparticipant.APlayerDataModel;

/**
 * Stub for groups
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
	private History<Double> economicPosition;
	@Element
	private History<HashMap<Proposition, Integer>> propositionHistory;
	private AgentType groupStrategy;
	private List<String> panel = new LinkedList<String>();
	@Element
	private History<Double> reservedFoodHistory;

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
		ret.economicPosition = new History<Double>(50);
		ret.propositionHistory = new History<HashMap<Proposition, Integer>>(50);
		ret.economicPosition.newEntry(init.getInitialEconomicBelief());
		ret.reservedFoodHistory = new History<Double>(50);
		ret.reservedFoodHistory.newEntry(0.0);
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
		return economicPosition.getValue();
	}

	void setEconomicPosition(double pos)
	{
		economicPosition.setValue(pos);
	}

	UnmodifiableHistory<Double> getEconomicPoisition()
	{
		return economicPosition.getUnmodifableHistory();
	}

	void clearRoundData()
	{
		economicPosition.newEntry(true);
		propositionHistory.newEntry(null);
		reservedFoodHistory.newEntry(true);
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

	public Map<Proposition, Integer> getTurnsProposals()
	{
		if (propositionHistory.isEmpty()) return null;
		HashMap<Proposition, Integer> d = propositionHistory.getValue();

		if (d == null) return null;
		return Collections.unmodifiableMap(d);
	}

	void setProposals(HashMap<Proposition, Integer> p)
	{
		propositionHistory.setValue(p);
	}

	double getEstimatedSocialLocation()
	{
		/*
		 * Algorithm:
		 *  - The leaders are those people trust most
		 *  - The social location is ratio(ish) of leaders to group members
		 *  - This can be modelled as the the deviation of the trust values
		 *   - If one agent is trusted more than others, high deviation
		 *   - This represents an autocracy
		 *  - The values used to find standard deviation will be the average of each
		 *    agent's opinion of an agent
		 */
		List<Double> avg_trusts = new ArrayList<Double>(memberList.size());
		PublicEnvironmentConnection ec = PublicEnvironmentConnection.getInstance();
		if (ec == null) return 0.5;

		// Find how trusted each agent is on average
		for (String candidate : memberList)
		{
			int n = 0;
			double sum = 0;
			for (String truster : memberList)
			{
				PublicAgentDataModel dm = ec.getAgentById(truster);
				Double t = (dm == null ? null : dm.getTrust(candidate));
				if (t != null && !candidate.equals(truster))
				{
					sum += t;
					++n;
				}
			}

			if (n > 0) avg_trusts.add(sum / n);
		}

		int n = avg_trusts.size();

		// No agents have any trust values for any other
		if (n == 0) return 0.5;

		// Calculate the average overall trust for an agent
		double sum = 0;
		for (Double v : avg_trusts)
			sum += v;

		double mu = sum / n;

		// Calculate the std deviation of overall trust in agents
		double variance = 0;
		for (Double v : avg_trusts)
			variance += (v - mu) * (v - mu);

		double st_dev = 2 * Math.sqrt(variance / n);
		return 1 - ScaledDouble.scale(st_dev, 1, 0.35);
	}

	List<String> getPanel()
	{
		return this.panel;
	}

	void setPanel(List<String> nPanel)
	{
		this.panel = nPanel;
	}

	AgentType getGroupStrategy()
	{
		return groupStrategy;
	}

	void setGroupStrategy(AgentType strategy)
	{
		this.groupStrategy = strategy;
	}

	void setReservedFoodHistory(double pooledFood)
	{
		reservedFoodHistory.setValue(pooledFood);
	}

	double getCurrentReservedFood()
	{
		return reservedFoodHistory.getValue();
	}

	History<Double> getReservedFoodHistory()
	{
		return reservedFoodHistory.getUnmodifableHistory();
	}

	int size()
	{
		return memberList.size();
	}
}
