package ise.gameoflife.enviroment;

import infection.AgentDataModel;
import ise.gameoflife.FoodDataModel;
import ise.gameoflife.Group;
import java.util.ArrayList;
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
	@ElementList(type=FoodDataModel.class)
	private ArrayList<FoodDataModel> availableFoodTypes;
	
	/**
	 * List of all the groups in the environment
	 */
	@ElementList(type=Group.class)
	private ArrayList<Group> agentGroups;
	
	/**
	 * Serialisable no-arg constructor, do not use
	 * @deprecated
	 */
	@Deprecated
	public EnvironmentDataModel()
	{
		super();
	}
	
}
