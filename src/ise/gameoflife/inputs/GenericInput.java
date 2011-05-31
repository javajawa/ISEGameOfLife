package ise.gameoflife.inputs;

import presage.Input;

/**
 * Base class for dealing with inputs, which implements all the required 
 * functions
 * @author Benedict Harcourt
 */
abstract class GenericInput implements Input
{
	/**
	 * Timestamp: the time at which the input event occurred, in terms of 
	 * simulation time
	 * Performative: A String describing the current state/action of the input
	 * eg: when an ActionError occurs, the performative is set to "ActionError"
	 */
	protected long timestamp;
	protected String performative;
	
/**
	 * Creates a new instance of GenericInput
	 * @param timestamp
	 * @param performative 
	 */
	public GenericInput(long timestamp, String performative)
	{
		this.timestamp = timestamp;
		this.performative = performative;
	}
/**
	 * Returns the Timestamp of the input event
	 * @return 
	 */
	@Override
	public long getTimestamp()
	{
		return timestamp;
	}
/**
	 * Set Timestamp
	 * @param timestamp 
	 */
	@Override
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
/**
	 * Returns the performative
	 * @return 
	 */
	@Override
	public String getPerformative()
	{
		return performative;
	}
	
}
