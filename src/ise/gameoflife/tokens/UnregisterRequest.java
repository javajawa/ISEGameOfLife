package ise.gameoflife.tokens;

import java.util.UUID;
import presage.environment.messages.ENVDeRegisterRequest;

/**
 *
 * @author Benedict
 */
public class UnregisterRequest extends ENVDeRegisterRequest
{
	private static final long serialVersionUID = 1L;

	public UnregisterRequest(String id, UUID authCode)
	{
		super(id, authCode);
	}

	@Override
	public UUID getParticipantAuthCode()
	{
		return super.getParticipantAuthCode();
	}

	@Override
	public String getParticipantID()
	{
		return super.getParticipantID();
	}

}
