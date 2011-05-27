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
	 * Stores amount of food owned by agent
	 */
	@Element
	private double foodInPossesion;
	/**
	 * Stores the amount of the food that the Agent must consume in order to
	 * survive each turn
	 */
	@Element
	private double foodConsumption;

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
	@SuppressWarnings("deprecation")
	public AgentDataModel(double foodInPossesion, double foodConsumption)
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
	public double getFoodInPossesion()
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

	public double getFoodConsumption()
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
