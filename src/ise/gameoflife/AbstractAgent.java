package ise.gameoflife;

import ise.gameoflife.actions.Hunt;
import ise.gameoflife.enviroment.EnvConnector;
import ise.gameoflife.models.Food;
import ise.gameoflife.tokens.RegistrationRequest;
import ise.gameoflife.tokens.RegistrationResponse;
import ise.gameoflife.tokens.UnregisterRequest;
import java.util.ArrayList;
import java.util.UUID;
import org.simpleframework.xml.Element;
import presage.EnvironmentConnector;
import presage.Input;
import presage.Participant;
import presage.PlayerDataModel;
import presage.environment.messages.ENVRegistrationResponse;

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
	/**
	 * Flag to show whether the initialise function has been called
	 */
	private boolean beenInitalised = false;

	/**
	 * The DataModel used by this agent.
	 */
	@Element
	private AgentDataModel dm;
	/**
	 * The authorisation code for use with sexy things like the environment
	 */
	private UUID authCode;
	protected EnvironmentConnector ec;

	/**
	 * Serialisation requires a public no-argument constructor to be present
	 * Using a explictly defined deprecated implementation should stop people
	 * accidentally calling this function
	 * @deprecated Not safe due to serialisation usages
	 */
	@Deprecated
	public AbstractAgent()
	{
		super();
	}

	/**
	 * Returns the String representation of the Agent's UUID, which is used to
	 * identify the agent in all interactions.
	 * @see UUID
	 * @see #id
	 * @return The String representation of the Agent's unique identifier
	 */
	@Override
	public final String getId()
	{
		return dm.getId();
	}

	@Override
	public final ArrayList<String> getRoles()
	{
		return dm.getRoles();
	}

	@Override
	public void initialise(EnvironmentConnector environmentConnector)
	{
		if (beenInitalised) throw new IllegalStateException("This object has already been initialised");
		beenInitalised = true;

		ec = (EnvConnector)environmentConnector;
		dm.initialise(ec);

		init();
	}

	@Override
	public final void onActivation()
	{
		ENVRegistrationResponse r = ec.register(new RegistrationRequest(dm.getId(), dm.getRoles(), dm.getPublicVersion()));
		this.authCode = r.getAuthCode();
	}

	@Override
	public final void onDeActivation()
	{
		ec.deregister(new UnregisterRequest(dm.getId(), dm.getGroupId()));
	}

	@Override
	public final void execute()
	{
		// TODO: Handle any inputs
		// TODO: Check for turn type
		Food toHunt = chooseFood();

		ec.act(new Hunt(toHunt), this.getId(), authCode);
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
	public PlayerDataModel getInternalDataModel()
	{
		return dm.getPublicVersion();
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
		// Nothing to see here. Move along, citizen!
	}

	/**
	 * Called after the initialising the agent, allowing subclassses to initialise
	 * any more data. The environment connector etc. will be available.
	 */
	abstract protected void init();
	/**
	 * Function called to get the Agent to select what king of food it would like
	 * to hunt. It should use all the other information it has received to inform
	 * this decision.
	 * You can get the types of food from {@link #ec this.ec}, which has various
	 * functions related to determining food properties
	 * @return The type of food they have decided to hunt
	 */
	abstract protected Food chooseFood();
}
