package ise.mace.tokens;

import ise.mace.participants.PublicAgentDataModel;
import java.util.ArrayList;
import presage.environment.messages.ENVRegisterRequest;

/**
 *
 */
public final class RegistrationRequest extends ENVRegisterRequest
{
	private static final long serialVersionUID = 1L;

	private final PublicAgentDataModel model;

	public RegistrationRequest(String id, ArrayList<String> roles, PublicAgentDataModel state)
	{
		super(id, roles);
		this.model = state;
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

	public PublicAgentDataModel getModel()
	{
		return model;
	}
}
