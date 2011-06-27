package ise.gameoflife.actions;

import ise.gameoflife.inputs.Proposition;

/**
 *
 * @author benedict
 */
public class Vote extends GenericAction
{

	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Enum to describe the different actions that can be taken in a vote
	 */
	public enum VoteType
	{
		/**
		 * Agent is voting to enact this proposition
		 */
		For(1),
		/**
		 * Agent is voting to <em>not</em> enact this proposition
		 */
		Against(-1),
		/**
		 * Agent is not using it's vote on this proposition
		 */
		Abstain(0);

		/**
		 * The 'movement' of this vote - increase / decrease to the overall vote
		 * value
		 */
		private int value;

		/**
		 * Constructs an instance of the enum
		 * @param value The movement of the vote
		 */
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
