package ise.gameoflife.enviroment;

import ise.gameoflife.models.PublicAgentDataModel;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.GroupDataModel;
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
	
	public EnvConnector(Environment e)
	{
		super(e);
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

	/*
	 * Gets the agent data object associated with a particular id, which is safe
	 * for being passed to other agents without giving them too much information
	 * @param id The id to search for
	 * @return The agent object, or null if not found
	 */
	public PublicAgentDataModel getAgentById(String id)
	{
		return dm.getAgentById(id);
	}

	public Set<Food> availableFoods()
	{
		return dm.availableFoods();
	}
}
