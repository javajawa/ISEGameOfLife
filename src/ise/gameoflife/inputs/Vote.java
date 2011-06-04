package ise.gameoflife.inputs;

import ise.gameoflife.actions.Vote.VoteType;

/**
 *
 * @author benedict
 */
public class Vote extends GenericInput
{
	private static final long serialVersionUID = 1L;

	private Proposition proposition;
	private String agent;
	private VoteType vote;

	public Vote(long timestamp, Proposition proposition, VoteType vote, String agent)
	{
		super(timestamp, "vote");
		this.proposition = proposition;
		this.vote = vote;
		this.agent = agent;
	}

	public Vote(ise.gameoflife.actions.Vote v, long timesamp, String agent)
	{
		super(timesamp, "vote");
		this.vote = v.getVote();
		this.proposition = v.getProposition();
		this.agent = agent;
	}

	public Proposition getProposition()
	{
		return proposition;
	}

	public VoteType getVote()
	{
		return vote;
	}

	public String getAgent()
	{
		return agent;
	}

}
