package ise.gameoflife.inputs;

/**
 * TODO: Documentation
 * @author Benedict
 */
public class ApplicationResponse extends GenericInput
{
	private String group;
	private boolean accepted;

	public ApplicationResponse(long time, String group, boolean accepted)
	{
		super(time, "ApplicationResponse");
		this.group = group;
		this.accepted = accepted;
	}

	public String getGroup()
	{
		return group;
	}

	public boolean wasAccepted()
	{
		return accepted;
	}

}
