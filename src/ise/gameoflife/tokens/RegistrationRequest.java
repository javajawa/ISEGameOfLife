/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ise.gameoflife.tokens;

import java.util.ArrayList;
import presage.environment.messages.ENVRegisterRequest;

/**
 *
 * @author Benedict
 */
public class RegistrationRequest extends ENVRegisterRequest
{
	private static final long serialVersionUID = 1L;

	public RegistrationRequest(String id, ArrayList<String> roles)
	{
		super(id, roles);
	}

	@Override
	public String getParticipantID()
	{
		return super.getParticipantID();
	}

	@Override
	public ArrayList<String> getParticipantRoles()
	{
		return super.getParticipantRoles();
	}

}
