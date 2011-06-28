package ise.mace.tokens;

import ise.mace.environment.EnvironmentConnection;
import java.util.UUID;
import presage.environment.messages.ENVRegistrationResponse;

/**
 *
 */
public class RegistrationResponse extends ENVRegistrationResponse
{
	private static final long serialVersionUID = 1L;
	protected EnvironmentConnection ec;

	public RegistrationResponse(String id, UUID authCode, EnvironmentConnection ec)
	{
		super(id, authCode);
		this.ec = ec;
	}

	@Override
	public String getParticipantId()
	{
		return super.getParticipantId();
	}

	@Override
	public UUID getAuthCode()
	{
		return super.getAuthCode();
	}

	public EnvironmentConnection getEc()
	{
		return ec;
	}
}
