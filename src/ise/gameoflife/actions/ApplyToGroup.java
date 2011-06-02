package ise.gameoflife.actions;

import presage.Action;

/**
 * Action to be performed by a Hunter in order to join a group.
 * @author Benedict
 */
public class ApplyToGroup extends GenericAction
{
	/**
	 * The group that is being applied to
	 */
	protected String group;

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
