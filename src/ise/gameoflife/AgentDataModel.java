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

	//stores amount of food owned by agent
	@Element
	private int foodInPossesion;

	@Deprecated
	public AgentDataModel()
	{
		super();
	}

	@SuppressWarnings("deprecation")
	public AgentDataModel(int food)
	{

		this.foodInPossesion = food;
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
