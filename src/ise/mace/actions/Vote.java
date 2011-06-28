package ise.mace.actions;

import ise.mace.inputs.Proposition;

/**
 * Allows an Agent to vote on a {@link Proposition}
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

		/**
		 * Returns the movement value of this vote
		 * @return The movement value of this vote
		 */
		public int getValue()
		{
			return value;
		}
	}

	/**
	 * The proposition being voted on
	 */
	private Proposition proposition;
	/**
	 * The vote being cast
	 */
	private VoteType vote;

	/**
	 * Creates a new vote action
	 * @param proposition The proposition being voted on
	 * @param vote The vote being cast
	 */
	public Vote(Proposition proposition, VoteType vote)
	{
		this.proposition = proposition;
		this.vote = vote;
	}

	/**
	 * The proposition being voted on
	 * @return The proposition
	 */
	public Proposition getProposition()
	{
		return proposition;
	}

	/**
	 * The vote being cast
	 * @return The vote
	 */
	public VoteType getVote()
	{
		return vote;
	}
}
