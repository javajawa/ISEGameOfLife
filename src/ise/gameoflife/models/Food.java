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

	/*
	 * The ID for the food item
	 */
	@Element
	private UUID id;
	
	/*
	 * Name of food type
	 */
	@Element
	private String name;
	/*
	 * Nutritional value of the food
	 */
	@Element
	private double nutrition;
	/*
	 * Number of agents required to obtain food
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
		return id;
	}
						
}
