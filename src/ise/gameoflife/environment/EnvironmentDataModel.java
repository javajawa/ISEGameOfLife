package ise.gameoflife.environment;

import ise.gameoflife.participants.PublicAgentDataModel;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.GroupDataInitialiser;
import ise.gameoflife.participants.AbstractGroupAgent;
import ise.gameoflife.participants.PublicGroupDataModel;
import ise.gameoflife.tokens.GroupRegistration;
import ise.gameoflife.tokens.RegistrationRequest;
import ise.gameoflife.tokens.TurnType;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import presage.environment.AEnvDataModel;

/**
 * TODO: Maybe some documentation here sometime
 * @author Benedict Harcourt
 */
public class EnvironmentDataModel extends AEnvDataModel
{
	private final static long serialVersionUID = 1L;

	/**
	 * A sorted list/map of all the state of all players in the game
	 */
	@ElementMap(keyType = String.class, valueType = PublicAgentDataModel.class)
	private TreeMap<String, PublicAgentDataModel> agents = new TreeMap<String, PublicAgentDataModel>();
	/**
	 * List of all the available food types in the environment
	 */
	@ElementMap
	private HashMap<String, Food> availableFoodTypes;
	/**
	 * List of all the groups in the environment
	 */
	@ElementMap
	private HashMap<String, PublicGroupDataModel> agentGroups;
	/**
	 * 
	 */
	@ElementList(type=Class.class)
	private ArrayList<Class<? extends AbstractGroupAgent>> allowedGroupTypes;
	/**
	 * 
	 */
	@Element
	private TurnType turn;
	@Element
	private int cycles;
	@Element
	private String id;
	@Element
	private double foodConsumedPerAdvice;

	/**
	 * Serialisable no-arg constructor, do not use
	 * @deprecated
	 */
	@Deprecated
	public EnvironmentDataModel()
	{
		super();
	}

	public EnvironmentDataModel(String environmentName, HashMap<String, Food> availableFoodTypes)
	{
		super(environmentName, "ISE Game of Life Enviroment Data Model", 0);
		this.availableFoodTypes = availableFoodTypes;

		this.agentGroups = new HashMap<String, PublicGroupDataModel>();
		this.allowedGroupTypes = new ArrayList<Class<? extends AbstractGroupAgent>>();

		this.turn = TurnType.firstTurn;
		this.cycles = 0;
		this.id = UUID.randomUUID().toString();
	}

	public EnvironmentDataModel(String environmentName,
					HashMap<String, Food> availableFoodTypes,
					ArrayList<Class<? extends AbstractGroupAgent>> allowedGroupTypes, double foodConsumedPerAdvice)
	{
		super(environmentName, "ISE Game of Life Enviroment Data Model", 0);
		this.availableFoodTypes = availableFoodTypes;

		this.agentGroups = new HashMap<String, PublicGroupDataModel>();
		this.allowedGroupTypes = allowedGroupTypes;

		this.turn = TurnType.firstTurn;
		this.cycles = 0;
		this.id = UUID.randomUUID().toString();
	}

	public Set<Food> availableFoods()
	{
		return Collections.unmodifiableSet(new HashSet<Food>(availableFoodTypes.values()));
	}

	public Food getFoodById(UUID id)
	{
		return availableFoodTypes.get(id.toString());
	}

	public PublicGroupDataModel getGroupById(String id)
	{
		return agentGroups.get(id);
	}

	public PublicAgentDataModel getAgentById(String id)
	{
		return agents.get(id);
	}

	public boolean removeParticipant(String id)
	{
		if (agents.remove(id)==null)
		{
			return (agentGroups.remove(id)!=null);
		}
		return true;
	}

	boolean registerParticipant(RegistrationRequest id)
	{
		agents.put(id.getParticipantID(), id.getModel());
		return true;
	}

	boolean registerGroup(GroupRegistration ng)
	{
		agentGroups.put(ng.getParticipantID(), ng.getDm());
		return true;
	}

	public TurnType getTurnType()
	{
		return turn;
	}

	public List<Class<? extends AbstractGroupAgent>> getAllowedGroupTypes()
	{
		return Collections.unmodifiableList(allowedGroupTypes);
	}

	@Override
	public void setTime(long time)
	{
		super.setTime(time);
		if (time == 0)
		{
			turn = TurnType.firstTurn;
			return;
		}

		TurnType[] t = TurnType.values();

		int next = turn.ordinal() + 1;
		if (next == t.length)
		{
			next = 0;
			cycles ++;
		}

		turn = t[next];
	}

	public int getCyclesPassed()
	{
		return cycles;
	}

	AbstractGroupAgent createGroup(Class<? extends AbstractGroupAgent> groupType, GroupDataInitialiser init)
	{
		if (allowedGroupTypes.contains(groupType))
		{
			try
			{
				Constructor<? extends AbstractGroupAgent> cons = groupType.getConstructor(GroupDataInitialiser.class);
				return cons.newInstance(init);
			}
			catch (Throwable ex)
			{
				throw new IllegalArgumentException("Unable to create group - no public constructor with single GroupDataInitialiser argument", ex);
			}
		}
		else
		{
			throw new IllegalArgumentException(groupType.getCanonicalName() + " is not in the list of permissible groups");
		}
	}

	Set<String> getAvailableGroups()
	{
		return Collections.unmodifiableSet(agentGroups.keySet());
	}

	public String getId()
	{
		return id;
	}

	double getFoodConsumedPerAdvice()
	{
		return foodConsumedPerAdvice;
	}

	Set<String> getAgents()
	{
		return agents.keySet();
	}

	List<String> getUngroupedAgents()
	{
		ArrayList<String> ret = new ArrayList<String>();
		for (String agent : agents.keySet())
		{
			if (agents.get(agent).getGroupId() == null)
			{
				ret.add(agent);
			}
		}
		return Collections.unmodifiableList(ret);
	}
}
