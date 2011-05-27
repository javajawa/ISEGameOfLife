package ise.gameoflife;

import org.simpleframework.xml.Element;

/**
 *
 * @author christopherfonseka
 */
public class FoodDataModel
{

	/*
	 * Name of food type
	 */
	@Element
	private String name;
	/*
	 * Nutritional value of the food
	 */
	@Element
	private int nutrition;
	/*
	 * Number of agents required to obtain food
	 * Extends later into probabilities
	 */
	@Element
	private int huntersRequired;

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
	public int getNutrition()
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
	
					
}
