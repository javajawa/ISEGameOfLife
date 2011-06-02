package ise.gameoflife.participants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import presage.PlayerDataModel;

/**
 * FIXME: Add useful functions to this
 * TODO: Document this
 * @author Benedict
 */
public class PublicGroupDataModel implements PlayerDataModel, Serializable
{
	private GroupDataModel source;

	PublicGroupDataModel(GroupDataModel source)
	{
		this.source = source;
	}
	
	/**
	 * Gets the id of the agent
	 * @return the id of the agent
	 */
	@Override
	public String getId()
	{
		return source.getId();
	}

	@Override
	public long getTime()
	{
		return source.getTime();
	}

	@Override
	public void setTime(long time)
	{
		source.setTime(time);
	}

	@Override
	public ArrayList<String> getRoles()
	{
		return source.getRoles();
	}

	@Override
	public void setRoles(ArrayList<String> roles)
	{
		source.setRoles(roles);
	}

	@Override
	public String getPlayerClass()
	{
		return source.getPlayerClass();
	}

	public List<String> getMemberList()
	{
		return source.getMemberList();
	}

}
