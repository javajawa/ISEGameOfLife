package ise.gameoflife.participants;

import ise.gameoflife.enviroment.EnvConnector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import org.simpleframework.xml.Element;
import presage.EnvironmentConnector;
import presage.Input;
import presage.Participant;
import presage.PlayerDataModel;

/**
 *
 * @author Benedict
 */
public final class GroupAgent implements Participant
{
	private static final long serialVersionUID = 1L;

	@Element
	private UUID groupId;
	private EnvConnector ec;
	
	@Override
	public String getId()
	{
		return groupId.toString();
	}

	@Override
	public ArrayList<String> getRoles()
	{
		return new ArrayList<String>(Arrays.asList(new String[]{"group"}));
	}

	@Override
	public void initialise(EnvironmentConnector environmentConnector)
	{
		ec = (EnvConnector)environmentConnector;
	}

	@Override
	public void onActivation()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onDeActivation()
	{
		// Nothing to see here. Move along, citizen!
	}

	@Override
	public void execute()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setTime(long cycle)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public PlayerDataModel getInternalDataModel()
	{
		return null;
	}

	@Override
	public void enqueueInput(Input input)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void enqueueInput(ArrayList<Input> input)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onSimulationComplete()
	{
		// TODO: Need anything here?
	}
	
}
