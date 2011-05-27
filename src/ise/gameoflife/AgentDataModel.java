package ise.gameoflife;

import org.simpleframework.xml.Element;
import presage.abstractparticipant.APlayerDataModel;

/**
 *
 * @author Christopher Fonseka
 */
public class AgentDataModel extends APlayerDataModel
{
	private static final long serialVersionUID = 1L;

	/**
	 * stores amount of food owned by agent
	 */
	@Element
	private int foodInPossesion;
	/**
	 * Stores the amount of the food that the Agent must consume in order to
	 * survive each turn
	 */
	@Element
	private int foodConsumption;

	/**
	 * Serialised constructors in the package are implemented as deprecated to
	 * stop warnings being shown
	 * @deprecated Due to serialisation conflicts
	 */
	@Deprecated
	public AgentDataModel()
	{
		super();
	}
	
	/**
	 * Creates a new agent, with a given amount of initial food, 
	 * and the amount of food they consume per turn
	 * @param foodInPossesion Initial amount of food
	 * @param foodConsumption Food consumed per turn
	 */
	public AgentDataModel(int foodInPossesion, int foodConsumption)
	{
		this.foodInPossesion = foodInPossesion;
		this.foodConsumption = foodConsumption;
	}

	@Override
	public void onInitialise()
	{
	}

	/**
	 * @return the foodInPossesion
	 */
	public int getFoodInPossesion()
	{
		return foodInPossesion;
	}

	/**
	 * @param foodInPossesion the foodInPossesion to set
	 */
	public void setFoodInPossesion(int foodInPossesion)
	{
		this.foodInPossesion = foodInPossesion;
	}

	public int getFoodConsumption()
	{
		return foodConsumption;
	}

	/**
	 * @param consumed reduces the foodInPossesion by a given amount
	 */
	public void foodConsumed(int consumed)
	{
		this.foodInPossesion -= consumed;
	}

	/**
	 * @param acquired increases food inventory by given amount
	 */
	public void foodAquired(int acquired)
	{
		this.foodInPossesion += acquired;
	}

}
