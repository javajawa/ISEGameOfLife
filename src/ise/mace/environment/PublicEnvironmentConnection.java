package ise.mace.environment;

import ise.mace.models.Food;
import ise.mace.models.GroupDataInitialiser;
import ise.mace.participants.AbstractAgent;
import ise.mace.participants.PublicAgentDataModel;
import ise.mace.participants.AbstractGroupAgent;
import ise.mace.participants.PublicGroupDataModel;
import ise.mace.tokens.TurnType;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import presage.Participant;

/**
 * <p>Used to access any information available to objects about the {@link
 * Environment}</p>
 * <p>This singleton class wraps the {@link EnvironmentConnection Environment 
 * Conneciton} for use in the public security level.</p>
 * <p>The {@link #inst instance} is initialised by the Environment before {@link
 * Participant#initialise(presage.EnvironmentConnector) initialise} is called,
 * so that {@link AbsractAgent agents} and {@link AbstractGroupAgent groups} can
 * always get the instance from the {@link AbstractAgent#getConn() abstract
 * superclass} or {@link #getInstance() directly} form here.</p>
 *
 * <p>The singleton nature of this class means that no two environments and,
 * therefore, two simulations, can operate in the same process, as the instance
 * would only be able to reference one of them</p>
 */
public class PublicEnvironmentConnection
{
	/**
	 * <p>The static singleton instance, which can be publicly be retrieved via the
	 * static {@link #getInstance() getInstance()} method.</p>
	 *
	 * <p>The instance is created by the {@link Environment} by passing itself to
	 * the {@link #PublicEnvironmentConnection(ise.mace.environment.Environment)
	 * constructor} whilst it is performing {@link
	 * Environment#initialise(presage.Simulation) initialisation}.
	 */
	private static PublicEnvironmentConnection inst;
	/**
	 * The logger that is made available for any public security class to use, if
	 * they do not wish to create their own
	 */
	public final static Logger logger = Logger.getLogger("mace.PublicLogger");

	/**
	 * <p>Returns the single {@link #inst instance} of the {@link
	 * PublicEnvironmentConnection}</p>
	 * <p>This function may return null if the instance has yet to be created</p>
	 * @return The instance
	 */
	public static PublicEnvironmentConnection getInstance()
	{
		return inst;
	}
	/**
	 * The Environment which we are connecting to
	 */
	private final Environment e;

	/**
	 * Creates the singleton {@link #inst Instance}, and initialises it to
	 * @param e The environment
	 * @throws IllegalStateException Thrown if an instance has already been
	 * created
	 */
	@SuppressWarnings("LeakingThisInConstructor") // Used to create singleton instance
	PublicEnvironmentConnection(Environment e)
	{
		//if (inst != null) throw new IllegalStateException("Instance already exists");
		this.e = e;
		inst = this;
	}

	/**
	 * Gets the group object associated with a particular id
	 * @param id The id to search for
	 * @return The group object, or null if not found
	 * @see #getGroups()
	 */
	public PublicGroupDataModel getGroupById(String id)
	{
		return e.getGroupById(id);
	}

	/**
	 * Gets the agent data object associated with a particular id, which is safe
	 * for being passed to other agents without giving them too much information
	 * @param id The id to search for
	 * @return The agent object, or null if not found
	 * @see #getAgents()
	 */
	public PublicAgentDataModel getAgentById(String id)
	{
		return e.getAgentById(id);
	}

	/**
	 * Finds what food types are available to hunt
	 * @return set of available food.
	 */
	public Set<Food> availableFoods()
	{
		return e.availableFoods();
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
		return e.getAllowedGroupTypes();
	}

	/**
	 * Returns the set of all {@link AbstractGroupAgent groups} current active
	 * @return IDs of all current groups
	 * @see #getGroupById(java.lang.String)
	 * @see #getAgents()
	 */
	public Set<String> getGroups()
	{
		return e.getGroups();
	}

	/**
	 * Creates an agent that represents a group
	 * @param food Amount of food
	 * @param economic Economic location
	 * @param social Social location
	 * @param name Name
	 * @return The id of the Group-Agent
	 * @see PoliticalAgentGroup
	 */
	public String createAgent(double food, double economic, double social,
					String name)
	{
		return e.createGroupAgent(food, economic, social, name);
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
	public String createGroup(Class<? extends AbstractGroupAgent> type,
					GroupDataInitialiser init)
	{
		return e.createGroup(type, init);
	}

	/**
	 * Create a new {@link AbstractGroupAgent group} of a particular class,
	 * initialise it, and invite some {@link AbstractAgent agents} to the group
	 * @param type The class of group that you wish to create
	 * @param init The initialiser instance to initialise the group with
	 * @param invitees A list of the ids of all agents you want to invite
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
	public String createGroup(Class<? extends AbstractGroupAgent> type,
					GroupDataInitialiser init, String... invitees)
	{
		return e.createGroup(type, init, invitees);
	}

	/**
	 * Determines if the supplied UUID identifies an {@link AbstractAgent agnet}
	 * @param id The UUID (or derivative id)
	 * @return Whether this represents a group
	 * @see #isGroupId(java.lang.String)
	 * @see PublicAgentDataModel#getId()
	 * @see PublicGroupDataModel#getId()
	 */
	public boolean isAgentId(String id)
	{
		return e.isAgentId(id);
	}

	/**
	 * Determines if the supplied UUID identifies a {@link AbstractGroupAgent
	 * group}
	 * @param gid The UUID (or derivative id)
	 * @return Whether this represents a group
	 * @see #isAgentId(java.lang.String)
	 * @see PublicAgentDataModel#getId()
	 * @see PublicGroupDataModel#getId()
	 */
	public boolean isGroupId(String gid)
	{
		return e.isGroupId(gid);
	}

	/**
	 * Returns the {@link Environment} Environment's id
	 * @return The Environment's id
	 * @see Environment#getId()
	 */
	public String getId()
	{
		return e.getId();
	}

	/**
	 * Gets the public Logger
	 * @return A static logger instance for use in the public security layer
	 */
	public Logger getLogger()
	{
		return logger;
	}

	/**
	 * Gets a list of all the active agents in the simulation
	 * @return A list of all the active agents in the simulation
	 */
	public Set<String> getAgents()
	{
		return e.getAgents();
	}

	/**
	 * Returns what {@link TurnType turn} it is in the round
	 * @return The current turn type
	 * @see #getRoundsPassed()
	 */
	public TurnType getCurrentTurnType()
	{
		return e.getCurrentTurnType();
	}

	/**
	 * Gets the total number of rounds past
	 * @return Total completed rounds
	 * @see TurnType
	 */
	public int getRoundsPassed()
	{
		return e.getRoundsPassed();
	}

	/**
	 * Returns a list of all {@link AbstractAgent agents} that are not currently
	 * part of a {@link AbstractGroupAgent group}
	 * @return The IDs of all un-grouped agents
	 * @see #getAgentById(java.lang.String) getAgentById
	 */
	public List<String> getUngroupedAgents()
	{
		return e.getUngroupedAgents();
	}
}
