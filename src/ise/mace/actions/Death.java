package ise.mace.actions;

/**
 * Represents a death action which, when sent, will cause the Environment to
 * remove an agent from the Simulation's processing loop by deactivating it
 */
public final class Death extends GenericAction
{
	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new death action which, when sent, will cause the Environment to
	 * remove an agent from the Simulation's processing loop by deactivating it
	 */
	public Death()
	{
		super();
	}

	// Stops Netbeans thinking this is a utility class
	@Override
	public String toString()
	{
		return super.toString();
	}
}
