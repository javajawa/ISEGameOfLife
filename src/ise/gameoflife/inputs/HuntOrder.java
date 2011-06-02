package ise.gameoflife.inputs;

import ise.gameoflife.models.Food;
import ise.gameoflife.models.HuntingTeam;

/**
 * Used to send hunt order instructions for the leader
 * of the current group to each member in that group
 * @author Benedict
 */
public class HuntOrder extends GenericInput
{
	
	/**
	 * Food ordered to be hunted
	 */
	private Food toHunt;
	
	/**
	 * Team ordered to hunt food specified
	 */
	private HuntingTeam team;

	
	/**
	 * Lots of lovely instantiations
	 * @param time
	 * @param toHunt
	 * @param team 
	 */
	public HuntOrder(long time, Food toHunt, HuntingTeam team)
	{
		super(time, "hunt_order");
		this.toHunt = toHunt;
		this.team = team;
	}

	/**
	 * Getter for hunt order
	 * @return food ordered to be hunted
	 */
	public Food getOrder()
	{
		return toHunt;
	}

	/**
	 * Which team is this? Here's the function you want!
	 * @return team getting the hunt order
	 */
	public HuntingTeam getTeam()
	{
		return team;
	}

}
