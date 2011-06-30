package ise.mace.inputs;

import ise.mace.models.HuntingTeam;

/**
 * Used to send hunt order instructions for the leader
 * of the current group to each member in that group
 */
public class HuntOrder extends GenericInput
{
	private static final long serialVersionUID = 1L;
	/**
	 * Team ordered to hunt food specified
	 */
	private HuntingTeam team;

	/**
	 * Lots of lovely instantiations
	 * @param time
	 * @param team
	 */
	public HuntOrder(long time, HuntingTeam team)
	{
		super(time, "hunt_order");
		this.team = team;
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
