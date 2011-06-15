package ise.gameoflife.actions;

/**
 * Class representing the types of Proposal an Agent can make with respect to
 * changed in Group Policy
 * @author Benedict
 */
public class Proposal extends GenericAction
{
	private static final long serialVersionUID = 1L;
	public enum ProposalType
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

		private ProposalType(double movement)
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

	private ProposalType type;
	private String forGroup;

	public Proposal(ProposalType type, String forGroup)
	{
		this.type = type;
		this.forGroup = forGroup;
	}

	public ProposalType getType()
	{
		return type;
	}

	public String getForGroup()
	{
		return forGroup;
	}

}