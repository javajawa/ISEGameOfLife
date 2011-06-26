package ise.gameoflife.actions;

import ise.gameoflife.inputs.Proposition;

/**
 *
 * @author Benedict
 */
public class VoteResult extends GenericAction
{

	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 1L;

	private Proposition p;
	private int votes;
	private double overallMovement;

	public VoteResult(Proposition p, int votes, double overallMovement)
	{
		this.p = p;
		this.votes = votes;
		this.overallMovement = overallMovement;
	}

	public double getOverallMovement()
	{
		return overallMovement;
	}

	public Proposition getProposition()
	{
		return p;
	}

	public int getVotes()
	{
		return votes;
	}

}
