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
	 * TODO: Document Me!
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

	@Deprecated
	public Food()
	{
		super();
	}
	
	
	public Food(String name, double nutrition, int huntersRequired)
	{
		this.id = UUID.randomUUID();
		this.id_string = this.id.toString();
		this.name = name;
		this.nutrition = nutrition;
		this.huntersRequired = huntersRequired;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the nutrition
	 */
	public double getNutrition()
	{
		return nutrition;
	}

	/**
	 * @return the huntersRequired
	 */
	public int getHuntersRequired()
	{
		return huntersRequired;
	}
	
	public UUID getId()
	{
		if (id == null) id = UUID.fromString(this.id_string);
		return id;
	}
						
}
