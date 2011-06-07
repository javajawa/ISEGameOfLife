package ise.gameoflife.inputs;

/**
 *
 * @author Benedict
 */
public class GroupInvite extends GenericInput
{
	private static final long serialVersionUID = 1L;
	private String group;

	public GroupInvite(long timestamp, String group)
	{
		super(timestamp, "invite");
		this.group = group;
	}

	public String getGroup()
	{
		return group;
	}
}
