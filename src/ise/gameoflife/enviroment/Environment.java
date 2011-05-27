package ise.gameoflife.enviroment;

import ise.gameoflife.tokens.RegistrationRequest;
import ise.gameoflife.tokens.RegistrationResponse;
import java.util.UUID;
import org.simpleframework.xml.Element;
import presage.EnvDataModel;
import presage.Simulation;
import presage.environment.AbstractEnvironment;
import presage.environment.messages.ENVDeRegisterRequest;
import presage.environment.messages.ENVRegisterRequest;
import presage.environment.messages.ENVRegistrationResponse;

/**
 * The primary environment code for the GameOfLife that we define. This will
 * contain a list of all the groups, food etc. etc. that exist in
 * @author Benedict Harcourt
 */
public class Environment extends AbstractEnvironment
{

	@Element
	protected EnvironmentDataModel dm;

	@Deprecated
	public Environment()
	{
		super();
	}
	
	@Override
	public boolean deregister(ENVDeRegisterRequest deregistrationObject)
	{
		if (!authenticator.get(deregistrationObject.getParticipantID()).equals(deregistrationObject.getParticipantAuthCode()))
		{
			return false;
		}
		return dm.removeParticipant(deregistrationObject.getParticipantID());
	}

	@Override
	public ENVRegistrationResponse onRegister(ENVRegisterRequest registrationObject)
	{
		final RegistrationRequest obj = (RegistrationRequest)registrationObject;
		if (!dm.registerParticipant(obj)) return null;
		return new RegistrationResponse(obj.getParticipantID(), UUID.randomUUID());
	}

	@Override
	public EnvDataModel getDataModel()
	{
		return dm;
	}

	@Override
	protected void onInitialise(Simulation sim)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected void updatePerceptions()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected void updatePhysicalWorld()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected void updateNetwork()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setTime(long cycle)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
