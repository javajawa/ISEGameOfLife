package ise.gameoflife.enviroment;

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

	@Deprecated
	public Environment()
	{
		super();
	}
	
	@Override
	public boolean deregister(ENVDeRegisterRequest deregistrationObject)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ENVRegistrationResponse onRegister(ENVRegisterRequest registrationObject)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public EnvDataModel getDataModel()
	{
		throw new UnsupportedOperationException("Not supported yet.");
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
