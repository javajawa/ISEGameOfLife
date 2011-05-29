package ise.gameoflife.actions;

import ise.gameoflife.models.Food;
import java.io.Serializable;
import java.util.UUID;
import presage.Action;

/**
 *
 * @author Benedict
 */
public final class Hunt implements Action, Serializable
{
	private static final long serialVersionUID = 1L;

	private final UUID foodType;
	
	/**
	 * Creates a new hunt action, which indicates that an agent is going to hunt
	 * a particular type of food
	 * @param type 
	 */
	public Hunt(Food type)
	{
		super();
		foodType = type.getId();
	}

	/**
	 * Returns the UUID that represents the type of food being hunted
	 * @return A Food UUID
	 */
	public UUID getFoodTypeId()
	{
		return foodType;
	}
}
