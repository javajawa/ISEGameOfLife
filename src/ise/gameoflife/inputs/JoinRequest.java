package ise.gameoflife.inputs;

/**
 * TODO: Documentation for this Class
 * @author Benedict
 */
public class JoinRequest extends GenericInput
{
	private String agent;

	public JoinRequest(long time, String agent)
	{
		super(time, "joinrequest");
		this.agent = agent;
	}

	public String getAgent()
	{
		return agent;
	}

}
