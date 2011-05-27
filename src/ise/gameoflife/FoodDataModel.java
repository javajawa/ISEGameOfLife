/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
	protected String name;
	/*
	 * Nutritional value of the food
	 */
	@Element
	protected int nutrition;
	/*
	 * Number of agents required to obtain food
	 * Extends later into probabilities
	 */
	@Element
	protected int huntersRequired;
	
					
}
