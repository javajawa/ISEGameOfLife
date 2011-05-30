/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ise.gameoflife.tokens;

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

	public GroupRegistration(String id)
	{
		super(id, groupRoles);
	}
}
