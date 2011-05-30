package ise.gameoflife.participants;

import example.participants.ReconAgent.InputHandler;
import ise.gameoflife.enviroment.EnvConnector;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.tokens.GroupRegistration;
import ise.gameoflife.tokens.RegistrationRequest;
import ise.gameoflife.tokens.RegistrationResponse;
import ise.gameoflife.tokens.UnregisterRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.simpleframework.xml.Element;
import presage.EnvironmentConnector;
import presage.Input;
import presage.Participant;
import presage.PlayerDataModel;
import presage.environment.messages.ENVRegistrationResponse;
import presage.util.InputQueue;

/**
 *
 * @author Benedict
 */
public abstract class AbstractGroupAgent implements Participant
{
	private static final long serialVersionUID = 1L;

	/**
	 * Flag to show whether the initialise function has been called
	 */
	private boolean beenInitalised = false;

	/**
	 * The DataModel used by this agent.
	 */
	@Element
	private GroupDataModel dm;
	/**
	 * The authorisation code for use with sexy things like the environment
	 */
	private UUID authCode;
	/**
	 * Reference to the environment connector, that allows the agent to interact
	 * with the environment
	 */
	protected EnvConnector ec;
	private EnvironmentConnector tmp_ec;

	private InputQueue msgQ = new InputQueue("inputs");
	private ArrayList<InputHandler> handlers = new ArrayList<InputHandler>();
	
	@Override
	public String getId()
	{
		return dm.groupID;
	}

	@Override
	public ArrayList<String> getRoles()
	{
		return new ArrayList<String>(Arrays.asList(new String[]{"group"}));
	}

@Override
	public void initialise(EnvironmentConnector environmentConnector)
	{
		if (beenInitalised) throw new IllegalStateException("This object has already been initialised");
		beenInitalised = true;

		System.out.println(environmentConnector.getClass().getCanonicalName());
		tmp_ec = environmentConnector;
		dm.initialise(environmentConnector);

		// TODO: Add input handlers here
		
		onInit(environmentConnector);
	}

	@Override
	public final void onActivation()
	{
		GroupRegistration request = new GroupRegistration(dm.getId());
		ENVRegistrationResponse r = tmp_ec.register(request);
		this.authCode = r.getAuthCode();
		this.ec = ((RegistrationResponse)r).getEc();
		onActivate();
	}

	@Override
	public final void onDeActivation()
	{
		ec.deregister(new UnregisterRequest(dm.getId(), authCode));
	}

	@Override
	public void execute()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private void handleInput(Input i)
	{
		for (InputHandler inputHandler : handlers)
		{
			if (inputHandler.canHandle(i)) inputHandler.handle(i);
		}
	}

	@Override
	public final void setTime(long cycle)
	{
		dm.setTime(cycle);
	}

	/**
	 * Returns the DataModel of this object
	 * @return The DataModel of this object
	 */
	@Override
	public final GroupDataModel getInternalDataModel()
	{
		return dm;
	}

	@Override
	public final void enqueueInput(Input input)
	{
		this.msgQ.enqueue(input);
	}

	@Override
	public final void enqueueInput(ArrayList<Input> input)
	{
		for (Input in : input)
		{
			this.msgQ.enqueue(in);
		}
	}

	@Override
	public void onSimulationComplete()
	{
		// TODO: Need anything here?
	}
	
	
	abstract protected void onInit(EnvironmentConnector ec);
	abstract protected void onActivate();
	
	abstract protected boolean respondToJoinRequest(String playerID);
	abstract protected List<HuntingTeam> selectTeams();
	abstract protected Map<String, Double> distributeFood(Map<String, Double> gains);
}
