package ise.gameoflife.models;

import java.io.Serializable;
import java.util.UUID;
import org.simpleframework.xml.Element;

/**
 *
 * @author Christopher Fonseka
 */
public class Food implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * The ID for the food item
	 */
	private UUID id;
	
	/**
	 * String that represent the UUID when in storage, as the UUID class is not
	 * storable by the SimpleXML framework (lack of public no-arg constructor)
	 */
	@Element
	private String id_string;
	
	/**
	 * Name of food type
	 */
	@Element
	private String name;
	/**
	 * Nutritional value of the food
	 */
	@Element
	private double nutrition;
	/**
	 * Number of agents required to successfully hunt food
	 * Extends later into probabilities
	 */
	@Element
	private int huntersRequired;

	/**
	 * This is a public no-arg constructor used by SimpleXML's inflection
	 * system to rebuild instances of this class from XML files. Do not use it
	 * @deprecated SimpleXML rebuild constructor
	 */
	@Deprecated
	public Food()
	{
		super();
	}
	
	/**
	 * Generate a new food type
	 * @param name The displayable name of the food
	 * @param nutrition The amount of nutrition each unit of the food provides
	 * @param huntersRequired The number of hunters required to hunt this type of
	 * food
	 */
	public Food(String name, double nutrition, int huntersRequired)
	{
		this.id = UUID.randomUUID();
		this.id_string = this.id.toString();
		this.name = name;
		this.nutrition = nutrition;
		this.huntersRequired = huntersRequired;
	}

	/**
	 * Returns the name of the food
	 * @return Human readable name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the nutritional value of the food
	 * @return Nutritional value
	 */
	public double getNutrition()
	{
		return nutrition;
	}

	/**
	 * Returns the number of hunters required to hunt this kind of food
	 * @return Total hunters required
	 */
	public int getHuntersRequired()
	{
		return huntersRequired;
	}
	
	/**
	 * Returns the universally unique identifier for this food, which is used to
	 * identify it internally
	 * @return Internal identifier
	 */
	public UUID getId()
	{
		if (id == null) id = UUID.fromString(this.id_string);
		return id;
	}

	@Override
	public String toString()
	{
		return "Food [" + getName() + ']';
	}

}
