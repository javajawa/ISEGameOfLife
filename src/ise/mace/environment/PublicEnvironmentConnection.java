package ise.mace.environment;

import ise.mace.models.Food;
import ise.mace.models.GroupDataInitialiser;
import ise.mace.participants.PublicAgentDataModel;
import ise.mace.participants.AbstractGroupAgent;
import ise.mace.participants.PublicGroupDataModel;
import ise.mace.tokens.TurnType;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Used to access any information available to objects about the environment
 */
public class PublicEnvironmentConnection
{
	private static PublicEnvironmentConnection inst;
	public final static Logger logger = Logger.getLogger("mace.PublicLogger");
	
	public static PublicEnvironmentConnection getInstance()
	{
		return inst;
	}

	private final EnvConnector ec;
	
	
	/**
	 * instantiates the instance of itself and the environment connector it
	 * uses to access environmental information
	 * @param ec 
	 */
	@SuppressWarnings("LeakingThisInConstructor")
	PublicEnvironmentConnection(EnvConnector ec)
	{
		this.ec = ec;
		inst = this;
	}
	
	/**
	 * Gets the group object associated with a particular id
	 * @param id The id to search for
	 * @return The group object, or null if not found
	 */
	public PublicGroupDataModel getGroupById(String id)
	{
		return ec.getGroupById(id);
	}

	/**
	 * Gets the agent data object associated with a particular id, which is safe
	 * for being passed to other agents without giving them too much information
	 * @param id The id to search for
	 * @return The agent object, or null if not found
	 */
	public PublicAgentDataModel getAgentById(String id)
	{
		return ec.getAgentById(id);
	}

	/**
	 * Finds what food types are available to hunt
	 * @return set of available foods
	 */
	public Set<Food> availableFoods()
	{
		return ec.availableFoods();
	}
	
	/**
	 * All available groups as specified in a list because of limitations with java
	 * {@link Class#forName(java.lang.String, boolean, java.lang.ClassLoader)} 
	 * @return list of all group classes available in this simulation
	 */
	public List<Class<? extends AbstractGroupAgent>> getAllowedGroupTypes()
	{
		return ec.getAllowedGroupTypes();
	}
	
	/**
	 * @return set of all current groups in the simulation
	 */
	public Set<String> getGroups()
	{
		return ec.getGroups();
	}

        //ADDED The0
        /**
         * Function used to create a new agent-group
         * @param average food amount of agents in group
         * @param economic position
         * @param social position
         * @return the ID of the created agent
         */
        public String createAgent(double food,double economic, double social, String name)
        {
                return ec.createAgent(food, economic, social, name);
        }


	/**
	 * Function used to create a new group. 
	 * Group data initialiser is an object that contains all the information used
	 * to create a new group.
	 * @param type The class of group to create
	 * @param init The initialisation parameters for the group
	 * @return The ID of the created group
	 */
	public String createGroup(Class<? extends AbstractGroupAgent> type, GroupDataInitialiser init)
	{
		return ec.createGroup(type, init);
	}

	/**
	 * Function used to create a new group. 
	 * Group data initialiser is an object that contains all the information used
	 * to create a new group.
	 * @param type The class of group to create
	 * @param init The initialisation parameters for the group
	 * @param invitees Any agents you wish to invite to the new group
	 * @return The ID of the created group
	 */
	public String createGroup(Class<? extends AbstractGroupAgent> type, GroupDataInitialiser init, String... invitees)
	{
		return ec.createGroup(type, init, invitees);
	}


	/**
	 * Determines whether a string represents the id of an active agent in this
	 * simulation
	 * @param id The id to check
	 * @return Whether this is the id of an active Agent in the system
	 */
	public boolean isAgentId(String id)
	{
		return ec.isAgentId(id);
	}

	/**
	 * Determines whether a string represents the id of an active group in this
	 * simulation
	 * @param gid The id to check
	 * @return Whether this id represents an active group
	 */
	public boolean isGroupId(String gid)
	{
		return ec.isGroupId(gid);
	}

	/**
	 * Returns the unique id of this simulation
	 * @return The unique id of this simulation
	 */
	public String getId()
	{
		return ec.getId();
	}

	/**
	 * Write a string to the standard output, if debug mode is enabled.
	 * @param s The string to log
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
		return ec.getAgents();
	}

	public TurnType getCurrentTurnType()
	{
		return ec.getCurrentTurnType();
	}

	public int getRoundsPassed()
	{
		return ec.getRoundsPassed();
	}

	public List<String> getUngroupedAgents()
	{
		return ec.getUngroupedAgents();
	}
}
