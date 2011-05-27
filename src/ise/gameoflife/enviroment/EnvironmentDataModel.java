package ise.gameoflife.enviroment;

import infection.AgentDataModel;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import presage.environment.AEnvDataModel;

/**
 *
 * @author Benedict Harcourt
 */
public class EnvironmentDataModel extends AEnvDataModel
{

	public final static long serialVersionUID = 1L;

	/**
	 * A sorted list/map of all the players in the game
	 */
	@ElementMap(keyType=String.class,valueType=AgentDataModel.class)
	private TreeMap<String, AgentDataModel> agents;
	
	/**
	 * List of all the available food types in the environment
	 */
	@ElementMap
	private HashMap<String,Food> availableFoodTypes;
	
	/**
	 * List of all the groups in the environment
	 */
	@ElementMap
	private HashMap<String,Group> agentGroups;
	
	/**
	 * Serialisable no-arg constructor, do not use
	 * @deprecated
	 */
	@Deprecated
	public EnvironmentDataModel()
	{
		super();
	}
	
	public Food getFoodById(String id)
	{
		return availableFoodTypes.get(id);
	}

	public Group getGroupById(String id)
	{
		return agentGroups.get(id);
	}
}
