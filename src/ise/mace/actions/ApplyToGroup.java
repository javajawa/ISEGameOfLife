package ise.mace.actions;

/**
 * Action to be performed by a Hunter in order to join a group.
 */
public class ApplyToGroup extends GenericAction
{

	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The group that is being applied to
	 */
	private String group;

	/**
	 * Creates a new application, specifying the group that is being applied to
	 * @param group 
	 */
	public ApplyToGroup(String group)
	{
		this.group = group;
	}

	/**
	 * Returns the group that the action's originator wishes to join
	 * @return The targeted group
	 */
	public String getGroup()
	{
		return group;
	}

}
