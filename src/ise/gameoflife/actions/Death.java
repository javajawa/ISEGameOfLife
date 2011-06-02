package ise.gameoflife.actions;

/**
 * Represents a death action which, when sent, will cause the Environment to
 * remove an agent from the Simulation's processing loop by deactivating it
 * @author Olly Hill
 */
public final class Death extends GenericAction
{

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new death action which, when sent, will cause the Environment to
	 * remove an agent from the Simulation's processing loop by deactivating it
	 */
	public Death()
	{
		super();
	}

	// Stops NEtbeans thinking this is a utility class
	@Override
	public String toString()
	{
		return super.toString();
	}

}
