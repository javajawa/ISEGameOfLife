/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ise.gameoflife.tokens;

import ise.gameoflife.participants.PublicGroupDataModel;
import java.util.ArrayList;
import java.util.Arrays;
import presage.environment.messages.ENVRegisterRequest;

/**
 *
 * @author benedict
 */
public class GroupRegistration extends ENVRegisterRequest
{
	private static final long serialVersionUID = 1L;
	
	private final static ArrayList<String> groupRoles = new ArrayList<String>(Arrays.asList(new String[]{"group"}));
	
	private PublicGroupDataModel dm;

	public GroupRegistration(String id, PublicGroupDataModel dm)
	{
		super(id, groupRoles);
		this.dm = dm;
	}

	public PublicGroupDataModel getDm()
	{
		return dm;
	}

}
