package ise.mace.environment;

import ise.mace.agents.PoliticalAgentGroup;
import ise.mace.participants.PublicAgentDataModel;
import ise.mace.models.Food;
import ise.mace.models.GroupDataInitialiser;
import ise.mace.models.HuntingTeam;
import ise.mace.participants.AbstractAgent;
import ise.mace.participants.AbstractGroupAgent;
import ise.mace.participants.PublicGroupDataModel;
import ise.mace.tokens.TurnType;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import presage.EnvironmentConnector;

/**
 * The environment connection, which gives access to the {@link Environment}
 * from the framework layer, such as {@link AbstractAgent} and {@link
 * AbstractGroupAgent}
 */
public class EnvConnector extends EnvironmentConnector
{
	/**
	 * The Environment that the connection wraps
	 */
	private final Environment e;

	/**
	 * Creates a new environment connection
	 * @param e The environment on which the connection is backed
	 */
	EnvConnector(Environment e)
	{
		super(e);
		this.e = e;
	}

	/**
	 * Gets the group object associated with a particular id
	 * @param id The id to search for
	 * @return The group object, or null if not found
	 */
	public PublicGroupDataModel getGroupById(String id)
	{
		return e.getGroupById(id);
	}

	/**
	 * Gets the food object associated with a particular id
	 * @param id The id to search for
	 * @return The food object, or null if not found
	 */
	public Food getFoodById(UUID id)
	{
		return e.getFoodById(id);
	}

	/**
	 * Gets the agent data object associated with a particular id, which is safe
	 * for being passed to other agents without giving them too much information
	 * @param id The id to search for
	 * @return The agent object, or null if not found
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
	 * Gets the public Logger
	 * @return A static logger instance
	 */
	public Logger getLogger()
	{
		return e.getMainLogger();
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
	public String createGroupAgent(double food, double economic, double social,
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
	 * @see #createGroup(java.lang.Class, ise.mace.models.GroupDataInitialiser, java.lang.String[])
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
	 * @return The Evnironment's id
	 * @see Environment#getId()
	 */
	public String getId()
	{
		return e.getId();
	}

	/**
	 * Get advice for a calling {@link AbstractAgent agent} from a remote agent
	 * as the best option for hunting
	 * @param agent The source agent
	 * @param authToken The token used to verify the source agent's identification
	 * @param fromAgent The agent that is to advise the source agent
	 * @param agentsTeam The team that the source agent is a member of
	 * @return The food that they are advised to hunt
	 * @see AbstractAgent#advise(java.lang.String, ise.mace.models.HuntingTeam)
	 * @see AbstractAgent#seekAvice(java.lang.String)
	 * @see AbstractAgent#giveAdvice(java.lang.String, ise.mace.models.HuntingTeam)
	 */
	public Food seekAdvice(String agent, UUID authToken, String fromAgent,
					HuntingTeam agentsTeam)
	{
		return e.seekAdvice(agent, authToken, fromAgent, agentsTeam);
	}

	/**
	 * Gets the amount of food consumes by asking someone for advice
	 * @return Amount of food
	 * @see AbstractAgent#seekAvice(java.lang.String)
	 */
	public double getFoodConsumedPerAdvice()
	{
		return e.getFoodConsumedPerAdvice();
	}

	/**
	 * Returns the name of an {@link AbstractAgent agent} or {@link
	 * AbstractGroupAgent group} based on a given id
	 * @param id The id
	 * @return The name matched with that id (or null if not located)
	 * @see PublicAgentDataModel#getName()
	 * @see PublicGroupDataModel#getName()
	 */
	public String nameof(String id)
	{
		return e.nameOf(id);
	}

	/**
	 * Returns the set of all {@link AbstractAgent agents} current alive
	 * @return IDs of all current agents
	 * @see #getAgentById(java.lang.String)
	 * @see #getGroups()
	 */
	public Set<String> getAgents()
	{
		return e.getAgents();
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
