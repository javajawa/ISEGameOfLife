package ise.mace.actions;

import ise.mace.models.Food;
import java.util.UUID;

/**
 * Creates an action that represents an agent going off on a hunting trip
 */
public final class Hunt extends GenericAction
{

	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * ID of the type of food to hunt
	 */
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
