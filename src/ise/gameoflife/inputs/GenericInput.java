package ise.gameoflife.inputs;

import java.util.UUID;
import presage.Input;

/**
 * Base class for dealing with inputs, which implements all the required 
 * functions
 * FIXME: Document this
 * @author Benedict Harcourt
 */
abstract class GenericInput implements Input
{
	protected long timestamp;
	protected String performative;

	public GenericInput(long timestamp, String performative)
	{
		this.timestamp = timestamp;
		this.performative = performative;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getPerformative()
	{
		return performative;
	}
	
}
