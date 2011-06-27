package ise.mace.actions;

import ise.mace.inputs.Proposition;

/**
 * Action that allows a Group to return the results of a voting round to an
 * agent.
 *
 * The agent is linked to the embedded {@link Proposition}; they get to know the
 * overall vote on the proposition, and the overall movement of the group after
 * all the votes are counted.
 */
public class VoteResult extends GenericAction
{
	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The proposition that was created by the agent this result sheet is for
	 */
	private Proposition p;
	/**
	 * The total vote score of the proposition
	 */
	private int votes;
	/**
	 * The overall group movement after all the votes are counted
	 */
	private double overallMovement;

	/**
	 * Creates a new result sheet action
	 * @param p The proposition that identifies the agent and group
	 * @param votes The total votes on the proposition
	 * @param overallMovement The total movement of the group after all the votes
	 * are counted
	 */
	public VoteResult(Proposition p, int votes, double overallMovement)
	{
		this.p = p;
		this.votes = votes;
		this.overallMovement = overallMovement;
	}

	/**
	 * Returns the overall group movement after all the votes are counted
	 * @return The overall group movement after all the votes are counted
	 */
	public double getOverallMovement()
	{
		return overallMovement;
	}

	/**
	 * Returns the proposition that this result sheet is for
	 * @return The proposition that this result sheet is for
	 */
	public Proposition getProposition()
	{
		return p;
	}

	/**
	 * The total votes on the {@link #getProposition() proposition}
	 * @return Total votes
	 */
	public int getVotes()
	{
		return votes;
	}
}
