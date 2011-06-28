package ise.mace.inputs;

/**
 * Input Class that deals with responses to application to groups
 */
public class ApplicationResponse extends GenericInput
{
	private static final long serialVersionUID = 1L;
	/**
	 * Group name
	 */
	private String group;
	/**
	 * Status of application - accepted or not
	 * True if accepted
	 * False if rejected
	 */
	private boolean accepted;

	/**
	 * Handles responses to Group Applications
	 * @param time turn number
	 * @param group Group applied to
	 * @param accepted result of application - accepted or not
	 */
	public ApplicationResponse(long time, String group, boolean accepted)
	{
		super(time, "ApplicationResponse");
		this.group = group;
		this.accepted = accepted;
	}

	/**
	 * Gets the name of Group applied to
	 * @return name of group applied to
	 */
	public String getGroup()
	{
		return group;
	}

	/**
	 * Gives the status of the application - accepted or rejected
	 * @return status of application as a boolean
	 */
	public boolean wasAccepted()
	{
		return accepted;
	}
}
