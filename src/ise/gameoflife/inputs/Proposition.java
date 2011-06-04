package ise.gameoflife.inputs;

import ise.gameoflife.actions.Proposal;
import ise.gameoflife.actions.Proposal.ProposalType;

/**
 *
 * @author benedict
 */
public class Proposition extends GenericInput
{
	private Proposal.ProposalType type;
	private String proposer;
	private String ownerGroup;

	public Proposition(ProposalType type, String proposer, String ownerGroup, long timestamp)
	{
		super(timestamp, "proposition");
		this.type = type;
		this.proposer = proposer;
		this.ownerGroup = ownerGroup;
	}

	public String getProposer()
	{
		return proposer;
	}

	public ProposalType getType()
	{
		return type;
	}

	public String getOwnerGroup()
	{
		return ownerGroup;
	}

}
