package ise.mace.inputs;

/**
 * GenericInput class used to specify the desire to leave the system
 */
public class LeaveNotification extends GenericInput
{

	private static final long serialVersionUID = 1L;

	/**
	 * The only reasons to leave deemed acceptable to leave the system
	 * That's right, missing Top Gear is not a valid excuse!
	 */
	public enum Reasons
	{
		/**
		 * The agent has died
		 */
		Death,
		/**
		 * Non-specific reason
		 */
		Other
	}

	/**
	 * Stores the reason given for the agent wishing to leave
	 */
	private Reasons reason;
	/**
	 * Which agent wishes to go
	 */
	private String agent;

	/**
	 * Sets up the Leave Notification, instantiating all the properties
	 * @param time
	 * @param reason
	 * @param agent 
	 */
	public LeaveNotification(long time, Reasons reason, String agent)
	{
		super(time, "leave");
		this.reason = reason;
		this.agent = agent;
	}

	/**
	 * @return for what reason do you want to go home?
	 */
	public Reasons getReason()
	{
		return reason;
	}

	/**
	 * @return who are you?
	 */
	public String getAgent()
	{
		return agent;
	}

}
