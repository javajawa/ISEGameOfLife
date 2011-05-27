package ise.gameoflife;

import java.util.ArrayList;
import presage.EnvironmentConnector;
import presage.Input;
import presage.Participant;
import presage.PlayerDataModel;

/**
 * Defines the methods that an Agent may use to interact with the modelling
 * system. Some of the interaction with the model are declared as final to
 * control the overall interface.
 *
 * All agents which extend this should be in the package
 * {@link ise.gameoflife.agents}
 * @author Benedict Harcourt
 */
abstract public class AbstractAgent implements Participant
{

	@Override
	public String getId()
	{
		// FIXME: Implement this
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ArrayList<String> getRoles()
	{
		// FIXME: Implement this
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void initialise(EnvironmentConnector environmentConnector)
	{
		// FIXME: Implement this
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onActivation()
	{
		// FIXME: Implement this
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onDeActivation()
	{
		// FIXME: Implement this
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void execute()
	{
		// FIXME: Implement this
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setTime(long cycle)
	{
		// FIXME: Implement this
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public PlayerDataModel getInternalDataModel()
	{
		// FIXME: Implement this
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void enqueueInput(Input input)
	{
		// FIXME: Implement this
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void enqueueInput(ArrayList<Input> input)
	{
		// FIXME: Implement this
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onSimulationComplete()
	{
		// FIXME: Implement this
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
