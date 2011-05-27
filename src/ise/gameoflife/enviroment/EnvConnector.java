package ise.gameoflife.enviroment;

import ise.gameoflife.AbstractAgent;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.Group;
import presage.EnvironmentConnector;

/**
 *
 * @author Benedict
 */
public class EnvConnector extends EnvironmentConnector
{
	private final Environment e;
	private final EnvironmentDataModel dm;
	
	public EnvConnector(Environment e)
	{
		super(e);
		this.e = e;
		this.dm = (EnvironmentDataModel)e.getDataModel();
	}

	public Group getGroupById(String id)
	{
		return dm.getGroupById(id);
	}
	
	public Food getFoodById(String id)
	{
		return dm.getFoodById(id);
	}

	public AbstractAgent getAgentById(String id)
	{
		return dm.getAgentById(id);
	}
}
