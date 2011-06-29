package ise.mace.environment;

import ise.mace.actions.Death;
import ise.mace.participants.PublicAgentDataModel;
import ise.mace.models.Food;
import ise.mace.models.GroupDataInitialiser;
import ise.mace.participants.AbstractAgent;
import ise.mace.participants.AbstractGroupAgent;
import ise.mace.participants.PublicGroupDataModel;
import ise.mace.tokens.GroupRegistration;
import ise.mace.tokens.RegistrationRequest;
import ise.mace.tokens.RegistrationResponse;
import ise.mace.tokens.TurnType;
import ise.mace.tokens.UnregisterRequest;
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
import presage.Participant;
import presage.Simulation;
import presage.environment.AEnvDataModel;

/**
 * TODO: Maybe some documentation here sometime
 */
public class EnvironmentDataModel extends AEnvDataModel
{
	/**
	 * Serialisation ID
	 */
	private final static long serialVersionUID = 1L;
	/**
	 * A sorted list/map of all the state of all players in the game
	 * @see #getAgents()
	 */
	@ElementMap(keyType = String.class, valueType = PublicAgentDataModel.class)
	private TreeMap<String, PublicAgentDataModel> agents = new TreeMap<String, PublicAgentDataModel>();
	/**
	 * List of all the available food types in the environment
	 * @see #availableFoods()
	 */
	@ElementMap
	private HashMap<String, Food> availableFoodTypes;
	/**
	 * List of all the groups in the environment
	 * @see #getGroups()
	 */
	@ElementMap
	private HashMap<String, PublicGroupDataModel> agentGroups;
	/**
	 * List of permitted group types
	 * @see #getAllowedGroupTypes()
	 */
	@ElementList(type = Class.class, required = false)
	private ArrayList<Class<? extends AbstractGroupAgent>> allowedGroupTypes;
	/**
	 * The currnet turn type
	 * @see #getTurnType()
	 * @see TurnType
	 */
	@Element
	private TurnType turn;
	/**
	 * <p>The number of completed rounds</p>
	 * <p>A round consists of exactly one of each of the {@link TurnType
	 * Turn Types}. The value is incremented in {@link #setTime(long) setTime}
	 * every time the {@link TurnType#firstTurn first turn} is selected as the
	 * current {@link #turn turn}</p>
	 */
	@Element
	private int rounds;
	/**
	 * UUID for this object
	 * @see #getId()
	 */
	@Element
	private String id;
	/**
	 * <p>The amount of food that is consumed by an {@link AbstractAgent agent}
	 * when they {@link AbstractAgent#seekAvice(java.lang.String) seek advice}
	 * from another agent.</p>
	 * <p>This is designed to model the amount of time and resources spent on
	 * communication and getting the advice, but is only currently taken from the
	 * agent seeking the advice.</p>
	 * @see #getFoodConsumedPerAdvice()
	 */
	@Element
	private double foodConsumedPerAdvice;

	/**
	 * Serialisable no-arg constructor, do not use
	 * @deprecated For serialisation only
	 */
	@Deprecated
	public EnvironmentDataModel()
	{
		super();
	}

	/**
	 * Creates a new environment data model for constructing an {@link
	 * Environment} in a {@link Simulation}
	 * @param environmentName The name to give the environment
	 * @param availableFoodTypes The food that agents can hunt {@link
	 * #availableFoods()}
	 * @param allowedGroupTypes The groups for use. {@link
	 * #getAllowedGroupTypes()}
	 * @param foodConsumedPerAdvice Amount of food consumed when an {@link
	 * AbstractAgent agent} {@link AbstractAgent#seekAvice(java.lang.String) seeks
	 * adive}. See {@link #foodConsumedPerAdvice}
	 */
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

	/**
	 * Finds what food types are available to hunt
	 * @return set of available food.
	 * @see #getFoodById(java.util.UUID)
	 */
	public Set<Food> availableFoods()
	{
		return Collections.unmodifiableSet(new HashSet<Food>(
						availableFoodTypes.values()));
	}

	/**
	 * Gets the food object associated with a particular id
	 * @param id The id to search for
	 * @return The food object, or null if not found
	 * @see #availableFoods()
	 */
	public Food getFoodById(UUID id)
	{
		return availableFoodTypes.get(id.toString());
	}

	/**
	 * Gets the agent data object associated with a particular id, which is safe
	 * for being passed to other agents without giving them too much information
	 * @param id The id to search for
	 * @return The agent object, or null if not found
	 * @see #getAgents()
	 */
	public PublicGroupDataModel getGroupById(String id)
	{
		return agentGroups.get(id);
	}

	/**
	 * Gets the group object associated with a particular id
	 * @param id The id to search for
	 * @return The group object, or null if not found
	 * @see #getGroups()
	 */
	public PublicAgentDataModel getAgentById(String id)
	{
		return agents.get(id);
	}

	/**
	 * Removes an {@link AbstractAgent agents} or {@link AbstractGroupAgent group}
	 * from the simulation
	 * @param id The id of the participant to remove
	 * @return Whether the {@link Participant} could be removed
	 * @see #registerParticipant(ise.mace.tokens.RegistrationRequest)
	 * @see UnregisterRequest
	 * @see Death
	 */
	public boolean removeParticipant(String id)
	{
		if (agents.remove(id) == null)
		{
			return (agentGroups.remove(id) != null);
		}
		return true;
	}
	/**
	 * Registers an {@link AbstractAgent agent} as part of the Environment
	 * @param id the new group
	 * @return Whether the registration was successful
	 * @see #registerGroup(ise.mace.tokens.GroupRegistration)
	 * @see RegistrationRequest
	 * @see RegistrationResponse
	 */
	boolean registerParticipant(RegistrationRequest id)
	{
		agents.put(id.getParticipantID(), id.getModel());
		return true;
	}

	/**
	 * Registers a {@link AbstractGroupAgent group} as part of the Environment
	 * @param ng the new group
	 * @return Whether the registration was successful
	 * @see #registerParticipant(ise.mace.tokens.RegistrationRequest)
	 * @see GroupRegistration
	 * @see RegistrationResponse
	 */
	boolean registerGroup(GroupRegistration ng)
	{
		agentGroups.put(ng.getParticipantID(), ng.getDm());
		return true;
	}

	/**
	 * Returns what {@link TurnType turn} it is in the round
	 * @return The current turn type
	 * @see #getRoundsPassed()
	 */
	public TurnType getTurnType()
	{
		return turn;
	}

	/**
	 * Returns the list of all classes of group that may be used in this
	 * simulation.
	 *
	 * Each of these classes will extend {@link AbstractGroupAgent}.
	 * The validity of this classes is not checked at simulation time - the
	 * framework may not be able to initialise them with a {@link
	 * GroupDataInitialiser}.
	 *
	 * The returned classes are also not guaranteed to be concrete
	 * implementations.
	 *
	 * Groups of these classes can then be created with {@link
	 * #createGroup(java.lang.Class, ise.mace.models.GroupDataInitialiser)
	 * createGroup()} with an appropriate initialiser.
	 * @return List of all permitted Group classes
	 * @see AbstractGroupAgent
	 * @see GroupDataInitialiser
	 * @see #createGroup(java.lang.Class, ise.mace.models.GroupDataInitialiser)
	 * @see #getGroups()
	 */
	public List<Class<? extends AbstractGroupAgent>> getAllowedGroupTypes()
	{
		return Collections.unmodifiableList(allowedGroupTypes);
	}

	/**
	 * Updates the time in the model
	 * @param time New cycle time
	 */
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

	/**
	 * Gets the total number of rounds past
	 * @return Total completed rounds
	 * @see TurnType
	 */
	public int getRoundsPassed()
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
				throw new IllegalArgumentException(
								"Unable to create group "
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
