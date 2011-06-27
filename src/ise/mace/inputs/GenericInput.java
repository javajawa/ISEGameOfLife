package ise.mace.inputs;

import java.io.Serializable;
import presage.Input;

/**
 * Generic class for inputs
 * @author Benedict Harcourt
 */
abstract public class GenericInput implements Input, Serializable
{

	/**
	 * The time at which the input event occurred, in terms of 
	 * simulation time
	 */
	private long timestamp;
	/**
	 * A String describing the current state/action of the input
	 * eg: when an ActionError occurs, the performative is set to "ActionError"
	 */
	private String performative;

	/**
	 * Creates a new instance of GenericInput
	 * @param timestamp what turn it is
	 * @param performative the current state
	 */
	GenericInput(long timestamp, String performative)
	{
		this.timestamp = timestamp;
		this.performative = performative;
	}

	/**
	 * Returns the Timestamp of the input event
	 * @return the timestamp
	 */
	@Override
	public long getTimestamp()
	{
		return timestamp;
	}

	/**
	 * Set Timestamp
	 * @param timestamp time at which event occurred.
	 */
	@Override
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	/**
	 * Returns the performative
	 * @return the current value of the Performative
	 */
	@Override
	public String getPerformative()
	{
		return performative;
	}

}
