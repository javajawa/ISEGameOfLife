package ise.gameoflife.inputs;

/**
 * TODO: Documentation
 * @author Benedict
 */
public class ApplicationResponse
{
	private String group;
	private boolean accepted;

	public ApplicationResponse(String group, boolean accepted)
	{
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
