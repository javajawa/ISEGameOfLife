package ise.gameoflife.inputs;

/**
 * TODO: Document this class
 * @author Benedict
 */
public class LeaveNotification extends GenericInput
{
	/**
	 * Documentation for this class
	 */
	public enum Reasons
	{
		Death,
		Other
	}

	private Reasons reason;
	private String agent;

	public LeaveNotification(long time, Reasons reason, String agent)
	{
		super(time, "leave");
		this.reason = reason;
		this.agent = agent;
	}

	public Reasons getReason()
	{
		return reason;
	}

	public String getAgent()
	{
		return agent;
	}

}
