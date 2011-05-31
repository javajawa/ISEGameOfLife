package ise.gameoflife.inputs;

import ise.gameoflife.models.Food;

/**
 * TODO: Document this class
 * @author Benedict
 */
public class HuntOrder extends GenericInput
{
	private Food toHunt;

	public HuntOrder(long time, Food toHunt)
	{
		super(time, "hunt_order");
		this.toHunt = toHunt;
	}

	public Food getOrder()
	{
		return toHunt;
	}
}
