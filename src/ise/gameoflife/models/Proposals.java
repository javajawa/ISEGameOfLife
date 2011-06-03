package ise.gameoflife.models;

/**
 * Class representing the types of Proposal an Agent can make with respect to
 * changed in Group Policy
 * @author Benedict
 */
public enum Proposals
{
	/**
	 * Move the group towards the economic left wing
	 */
	moveLeft(-1),
	/**
	 * Stay in the same position
	 */
	staySame(+0),
	/**
	 * Move the group towards the economic right wing
	 */
	moveRight(1);

	private double movement;

	private Proposals(double movement)
	{
		this.movement = movement;
	}

	/**
	 * Get the normalised 1-d movement vector associated with this proposal
	 * @return The normalised 1-d movement vector associated with this proposal
	 */
	public double getMovement()
	{
		return movement;
	}

}
