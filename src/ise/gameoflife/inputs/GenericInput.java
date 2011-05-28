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
	protected UUID token;

	public GenericInput(UUID token, long timestamp, String performative)
	{
		this.token = token;
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

	/**
	 * Returns UUID of the hunt result
	 * used as token to enable message passing
	 * @return
	 */
	public UUID getToken()
	{
		return token;
	}
	
}
