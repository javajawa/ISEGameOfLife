package ise.gameoflife.actions;

import presage.Action;

/**
 * Represents a death action which, when sent, will cause the Environment to
 * remove an agent from the Simulation's processing loop by deactivating it
 * @author Olly
 */
public final class Death implements Action
{
	/**
	 * Creates a new death action which, when sent, will cause the Environment to
	 * remove an agent from the Simulation's processing loop by deactivating it
	 */
	public Death(){
		super();
	}
}
