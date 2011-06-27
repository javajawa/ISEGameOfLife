package ise.mace.actions;

/**
 * Class representing the types of Proposal an Agent can make with respect to
 * changed in Group Policy
 */
public class Proposal extends GenericAction
{

	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Enum that lists the different propositions that can be made in the voting
	 * stage.
	 *
	 * The properties of each value determine how the proposition works; currently
	 * this is limited to a shift in economic location only
	 */
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

		/**
		 * The change in economic position
		 */
		private double movement;

		/**
		 * Creates a instance of the enum
		 * @param movement The change in the economic position
		 */
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

	/**
	 * The type of proposal that is being created
	 */
	private ProposalType type;
	/**
	 * The group in which this proposition is being made
	 */
	private String forGroup;

	/**
	 * Creates a new proposal action
	 * @param type The type of proposal
	 * @param forGroup The group where it's being made
	 */
	public Proposal(ProposalType type, String forGroup)
	{
		this.type = type;
		this.forGroup = forGroup;
	}

	/**
	 * Returns the type of proposal that is being created
	 * @return The type of proposal that is being created
	 */
	public ProposalType getType()
	{
		return type;
	}

	/**
	 * Returns the group in which this proposition is being made
	 * @return The group in which this proposition is being made
	 */
	public String getForGroup()
	{
		return forGroup;
	}

}