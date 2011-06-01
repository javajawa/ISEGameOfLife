package ise.gameoflife.participants;

import ise.gameoflife.actions.ApplyToGroup;
import ise.gameoflife.models.AgentDataModel;
import ise.gameoflife.actions.Death;
import ise.gameoflife.actions.Hunt;
import ise.gameoflife.enviroment.EnvConnector;
import ise.gameoflife.enviroment.PublicEnvironmentConnection;
import ise.gameoflife.inputs.ApplicationResponse;
import ise.gameoflife.inputs.ConsumeFood;
import ise.gameoflife.inputs.HuntOrder;
import ise.gameoflife.inputs.HuntResult;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.PublicAgentDataModel;
import ise.gameoflife.tokens.RegistrationRequest;
import ise.gameoflife.tokens.RegistrationResponse;
import ise.gameoflife.tokens.TurnType;
import ise.gameoflife.tokens.UnregisterRequest;
import java.util.ArrayList;
import java.util.UUID;
import org.simpleframework.xml.Element;
import presage.EnvironmentConnector;
import presage.Input;
import presage.Participant;
import presage.PlayerDataModel;
import presage.environment.messages.ENVRegistrationResponse;
import presage.util.InputQueue;

/**
 * Defines the methods that an Agent may use to interact with the modelling
 * system. Some of the interactions with the model are declared as final to
 * control the overall interface.
 *
 * All agents which extend this class should be in the package
 * {@link ise.gameoflife.agents}
 * @author Benedict Harcourt
 */
abstract public class AbstractAgent implements Participant
{

	private class ConsumeFoodHandler implements InputHandler
	{
		@Override
		public boolean canHandle(Input input){
			return (input instanceof ConsumeFood);
		}

		@Override
		public void handle(Input input)
		{
			if (dm.getFoodInPossesion() < dm.getFoodConsumption())
			{
				System.out.println("I, agent, " + getId() + ", am starving to death!");
				ec.act(new Death(), dm.getId(), authCode);
				dm.foodConsumed(dm.getFoodConsumption() + 1);
			}
			else
			{
				System.out.println("I, agent " + getId() + ", consumed " + dm.getFoodConsumption() + " units of food");
				dm.foodConsumed(dm.getFoodConsumption());
			}
		}
	}

	private class HuntResultHandler implements InputHandler
	{

		@Override
		public boolean canHandle(Input input)
		{
			return (input instanceof HuntResult);
		}

		@Override
		public void handle(Input input)
		{
			final HuntResult in = (HuntResult)input;
			dm.foodAquired(in.getNutritionValue());
		}
	}

	private class HuntOrderHandler implements InputHandler
	{

		@Override
		public boolean canHandle(Input input)
		{
			return (input instanceof HuntOrder);
		}

		@Override
		public void handle(Input input)
		{
			final HuntOrder in = (HuntOrder)input;
			dm.setOrder(in.getOrder());
			dm.setHuntingTeam(in.getTeam());
			System.out.println("I, Agent " + getId() + " have been told to hunt " + in.getOrder().getName() + " with Team " + in.getTeam().hashCode());
		}
	}

	private class ApplicationResponseHandler implements InputHandler
	{

		@Override
		public boolean canHandle(Input input)
		{
			return (input instanceof ApplicationResponse);
		}

		@Override
		public void handle(Input input)
		{
			final ApplicationResponse in = (ApplicationResponse)input;
			if (in.wasAccepted())
			{
				dm.setGroup(in.getGroup());
			}
			groupApplicationResponse(in.wasAccepted());	
			System.out.println("I, agent " + getId() + " was " + (in.wasAccepted() ? "" : "not ") + "accepted into group " + in.getGroup());
		}
		
	}

	/**
	 * The DataModel used by this agent.
	 */
	@Element
	private AgentDataModel dm;
	/**
	 * The authorisation code for use with sexy things like the environment
	 */
	private UUID authCode;
	/**
	 * Reference to the environment connector, that allows the agent to interact
	 * with the environment
	 */
	protected PublicEnvironmentConnection conn;
	private EnvConnector ec;
	private EnvironmentConnector tmp_ec;

	private InputQueue msgQ = new InputQueue("inputs");
	private ArrayList<InputHandler> handlers = new ArrayList<InputHandler>();

	/**
	 * Serialisation requires a public no-argument constructor to be present.
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
	 * Creates a new agent with the two primary properties
	 * @param myroles The roles that this agent can perform
	 * @param randomseed the random seed for this agent
	 * @param initialFood The initial amount of food
	 * @param consumption The amount consumed per turn
	 */
	public AbstractAgent(String myroles, long randomseed, double initialFood, double consumption)
	{
		UUID myid = UUID.randomUUID();
		this.dm = new AgentDataModel(myid.toString(), myroles, this.getClass().getCanonicalName(), randomseed, initialFood, consumption);
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
	public final void initialise(EnvironmentConnector environmentConnector)
	{
		System.out.println(environmentConnector.getClass().getCanonicalName());
		tmp_ec = environmentConnector;
		dm.initialise(environmentConnector);

		this.handlers.add(new ConsumeFoodHandler());
		this.handlers.add(new HuntResultHandler());
		this.handlers.add(new HuntOrderHandler());
		this.handlers.add(new ApplicationResponseHandler());

		onInit();
	}

	@Override
	public final void onActivation()
	{
		RegistrationRequest request = new RegistrationRequest(dm.getId(), dm.getRoles(), dm.getPublicVersion());
		ENVRegistrationResponse r = tmp_ec.register(request);
		this.authCode = r.getAuthCode();
		this.ec = ((RegistrationResponse)r).getEc();
		conn = PublicEnvironmentConnection.getInstance();
		tmp_ec = null;
		onActivate();
	}

	@Override
	public final void onDeActivation()
	{
		ec.deregister(new UnregisterRequest(dm.getId(), authCode));
	}

	@Override
	public final void execute()
	{
		// Handle Queued Messages
		while (!msgQ.isEmpty())	handleInput(msgQ.dequeue());
		// Check to see if we died due to a message in the queue
		if (this.dm.getFoodInPossesion() < 0) return;

		TurnType turn = ec.getCurrentTurnType();

		if (TurnType.firstTurn.equals(turn))
		{
			beforeNewRound();
			clearRoundData();
		}

		switch (turn)
		{
			case GroupSelect:
				doGroupSelect();
				break;
			case TeamSelect:
				// This is the group's move
				break;
			case GoHunt:
				doHuntTurn();
				break;
			case HuntResults:
				// This is the group's move
				break;
		}
	}

	private void handleInput(Input i)
	{
		for (InputHandler inputHandler : handlers)
		{
			if (inputHandler.canHandle(i))
			{
				inputHandler.handle(i);
				return;
			}
		}
		ec.logToErrorLog("AbstractAgent can not handle inputs of type " + i.getClass().getCanonicalName());
	}

	private void clearRoundData()
	{
		dm.setLastHunted(null);
		dm.setHuntingTeam(null);
		dm.setOrder(null);
	}

	private void doGroupSelect()
	{
		// TODO: chec that we're not joingin the group we're alreayd part of
		String gid = chooseGroup();
		if (conn.isGroupId(gid)) ec.act(new ApplyToGroup(gid), getId(), authCode);
	}

	private void doHuntTurn()
	{
		Food toHunt = chooseFood();

		if (toHunt == null)
		{
			ec.logToErrorLog("Agent " + this.getId() + " did not pick a food to hunt");
		}
		else
		{
			ec.act(new Hunt(toHunt), this.getId(), authCode);
		}
		dm.setLastHunted(toHunt);
	}

	/**
	 * 
	 * @param cycle
	 */
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
	public final PlayerDataModel getInternalDataModel()
	{
		return dm.getPublicVersion();
	}

	public final PublicAgentDataModel getDataModel()
	{
		return dm.getPublicVersion();
	}

	@Override
	public final void enqueueInput(Input input)
	{
		this.msgQ.enqueue(input);
	}

	/**
	 * 
	 * @param input
	 */
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
		// Nothing to see here. Move along, citizen!
	}

	/**
	 * Get the next random number in the sequence as a double uniformly
	 * distributed between 0 and 1
	 * @return Next random number
	 */
	public final double uniformRand()
	{
		return this.dm.random.nextDouble();
	}

	/**
	 * Called after the initialising the agent, allowing subclasses to initialise
	 * any more data.
	 */
	abstract protected void onInit();
	/**
	 * Called when the agent has been activated, similar to init, but with access
	 * to the environment connector
	 */
	abstract protected void onActivate();
	/**
	 * Used to implement any code necessary before all properties of the current
	 * round are deleted to make way for a newer, fresher, more flexible
	 * round to begin.
	 */
	abstract protected void beforeNewRound();
	/**
	 * Magic heuristic to select which Group the agent wishes to be a part of
	 * for the next round.
	 * @return string titling the desired group
	 */
	abstract protected String chooseGroup();
	/**
	 * Called once the environment has issued a response to the application
	 * to join a group. Also states whether or not the application has been
	 * successful. Does not say if the agent has a backup group, with lower
	 * entry requirements.
	 */
	abstract protected void groupApplicationResponse(boolean accepted);
	/**
	 * Function called to get the Agent to select what kind of food it would like
	 * to hunt. It should use all the other information it has received to inform
	 * this decision.
	 * You can get the types of food from {@link #ec this.ec}, which has various
	 * functions related to determining food properties
	 * @return The type of food they have decided to hunt
	 */
	abstract protected Food chooseFood();
}
