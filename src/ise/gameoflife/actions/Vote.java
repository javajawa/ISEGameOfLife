package ise.gameoflife.actions;

import ise.gameoflife.inputs.Proposition;

/**
 *
 * @author benedict
 */
public class Vote extends GenericAction
{
	public enum VoteType
	{
		For(1),
		Against(-1),
		Abstain(0);

		private int value;

		private VoteType(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}
	}

	private Proposition proposition;
	private VoteType vote;

	public Vote(Proposition proposition, VoteType vote)
	{
		this.proposition = proposition;
		this.vote = vote;
	}

	public Proposition getProposition()
	{
		return proposition;
	}

	public VoteType getVote()
	{
		return vote;
	}

}
