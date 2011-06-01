package ise.gameoflife.enviroment;

import ise.gameoflife.models.PublicAgentDataModel;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.GroupDataModel;
import ise.gameoflife.participants.AbstractGroupAgent;
import ise.gameoflife.tokens.TurnType;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import presage.EnvironmentConnector;

/**
 *
 * @author Benedict
 */
public class EnvConnector extends EnvironmentConnector
{
	private final EnvironmentDataModel dm;
	private final Environment e;
	
	public EnvConnector(Environment e)
	{
		super(e);
		this.e = e;
		this.dm = (EnvironmentDataModel)e.getDataModel();
	}

	/**
	 * Gets the group object associated with a particular id
	 * @param id The id to search for
	 * @return The group object, or null if not found
	 */
	public GroupDataModel getGroupById(UUID id)
	{
		return dm.getGroupById(id);
	}
	
	/**
	 * Gets the food object associated with a particular id
	 * @param id The id to search for
	 * @return The food object, or null if not found
	 */
	public Food getFoodById(UUID id)
	{
		return dm.getFoodById(id);
	}

	/**
	 * Gets the agent data object associated with a particular id, which is safe
	 * for being passed to other agents without giving them too much information
	 * @param id The id to search for
	 * @return The agent object, or null if not found
	 */
	public PublicAgentDataModel getAgentById(String id)
	{
		return dm.getAgentById(id);
	}

	/**
	 * Finds what food types are available to hunt
	 * @return set of available food.
	 */
	public Set<Food> availableFoods()
	{
		return dm.availableFoods();
	}

	/**
	 * Logs an error in the ErrorLog
	 * @param s Error string to log
	 */
	public void logToErrorLog(String s)
	{
		e.logToErrorLog(s);
	}

	/**
	 * Logs an error in the ErrorLog
	 * @param s Error to Log
	 */
	public void logToErrorLog(Throwable s)
	{
		e.logToErrorLog(s);
	}

	public TurnType getCurrentTurnType()
	{
		return e.getCurrentTurnType();
	}

	List<Class<? extends AbstractGroupAgent>> getAllowedGroupTypes()
	{
		return e.getAllowedGroupTypes();
	}

	Set<String> getAvailableGroups()
	{
		return e.getAvailableGroups();
	}

	public String createGroup(Class<? extends AbstractGroupAgent> type)
	{
		return e.createGroup(type);
	}

	public boolean isAgentId(String id)
	{
		return e.isAgentId(id);
	}
	
	public boolean isGroupId(String gid)
	{
		return e.isGroupId(gid);
	}
}
