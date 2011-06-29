package ise.mace.environment;

import ise.mace.participants.PublicAgentDataModel;
import ise.mace.models.Food;
import ise.mace.models.GroupDataInitialiser;
import ise.mace.participants.AbstractGroupAgent;
import ise.mace.participants.PublicGroupDataModel;
import ise.mace.tokens.GroupRegistration;
import ise.mace.tokens.RegistrationRequest;
import ise.mace.tokens.TurnType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
	@ElementList(type = Class.class, required = false)
	private ArrayList<Class<? extends AbstractGroupAgent>> allowedGroupTypes;
	/**
	 *
	 */
	@Element
	private TurnType turn;
	@Element
	private int rounds;
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

	public EnvironmentDataModel(String environmentName,
															HashMap<String, Food> availableFoodTypes)
	{
		this(environmentName, availableFoodTypes, null, 0);
	}

	public EnvironmentDataModel(String environmentName,
															HashMap<String, Food> availableFoodTypes,
															ArrayList<Class<? extends AbstractGroupAgent>> allowedGroupTypes,
															double foodConsumedPerAdvice)
	{
		super(environmentName, "ISE Game of Life Enviroment Data Model", 0);
		this.availableFoodTypes = availableFoodTypes;

		this.agentGroups = new HashMap<String, PublicGroupDataModel>();
		this.allowedGroupTypes = allowedGroupTypes;

		this.turn = TurnType.firstTurn;
		this.rounds = 0;
		this.id = UUID.randomUUID().toString();
	}

	public Set<Food> availableFoods()
	{
		return Collections.unmodifiableSet(new HashSet<Food>(
						availableFoodTypes.values()));
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
		if (agents.remove(id) == null)
		{
			return (agentGroups.remove(id) != null);
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
			rounds++;
		}

		turn = t[next];
	}

	public int getCyclesPassed()
	{
		return rounds;
	}

	/**
	 * Create a new {@link AbstractGroupAgent group} of a particular class,
	 * initialise it, and invite some {@link AbstractAgent agents} to the group
	 * @param type The class of group that you wish to create
	 * @param init The initialiser instance to initialise the group with
	 * @return The id of the new group, or null if the group could not be created
	 * @throws IllegalArgumentException If the group class is not in the list of
	 * @throws RuntimeException If the the reflection libraries or constructor 
	 * throw an exception
	 * {@link #getAllowedGroupTypes() permissible gorup classes}, or if it can not
	 * be initialised with a {@link GroupDataInitialiser}
	 * @see #createGroup(java.lang.Class, ise.mace.models.GroupDataInitialiser)
	 * @see #getAllowedGroupTypes()
	 * @see AbstractGroupAgent
	 * @see GroupDataInitialiser
	 */
	AbstractGroupAgent createGroup(Class<? extends AbstractGroupAgent> type,
																 GroupDataInitialiser init)
	{
		if (allowedGroupTypes.contains(type))
		{
			Constructor<? extends AbstractGroupAgent> cons;
			try
			{
				cons = type.getConstructor(GroupDataInitialiser.class);
			}
			catch (Throwable ex)
			{
				throw new IllegalArgumentException("Unable to create group "
								+ type.getSimpleName()
								+ " - no public constructor with single GroupDataInitialiser argument",
								ex);
			}
			try
			{
				return cons.newInstance(init);
			}
			catch (InvocationTargetException ex)
			{
				throw new RuntimeException(ex.getTargetException());
			}
			catch (Exception ex)
			{
				throw new RuntimeException(ex);
			}
		}
		else
		{
			throw new IllegalArgumentException(type.getCanonicalName()
							+ " is not in the list of permissible groups");
		}
	}

	/**
	 * Returns the set of all {@link AbstractGroupAgent groups} current active
	 * @return IDs of all current groups
	 * @see #getGroupById(java.lang.String)
	 * @see #getAgents()
	 */
	Set<String> getGroups()
	{
		return Collections.unmodifiableSet(agentGroups.keySet());
	}

	/**
	 * Returns the {@link Environment} Environment's id
	 * @return The Environment's id
	 * @see Environment#getId()
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Gets the amount of food consumes by asking someone for advice
	 * @return Amount of food
	 * @see AbstractAgent#seekAvice(java.lang.String)
	 */
	double getFoodConsumedPerAdvice()
	{
		return foodConsumedPerAdvice;
	}

	/**
	 * Returns the set of all {@link AbstractAgent agents} current alive
	 * @return IDs of all current agents
	 * @see #getAgentById(java.lang.String)
	 * @see #getGroups()
	 */
	Set<String> getAgents()
	{
		return agents.keySet();
	}

	/**
	 * Returns a list of all {@link AbstractAgent agents} that are not currently
	 * part of a {@link AbstractGroupAgent group}
	 * @return The IDs of all un-grouped agents
	 * @see #getAgentById(java.lang.String) getAgentById
	 */
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
