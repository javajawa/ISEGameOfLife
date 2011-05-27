package ise.gameoflife.tokens;

import java.util.UUID;
import presage.environment.messages.ENVRegistrationResponse;

/**
 *
 * @author Benedict
 */
public class RegistrationResponse extends ENVRegistrationResponse
{
	private static final long serialVersionUID = 1L;

	public RegistrationResponse(String id, UUID authCode)
	{
		super(id, authCode);
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

}
