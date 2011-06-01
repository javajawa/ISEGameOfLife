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
import ise.gameoflife.models.HuntingTeam;
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
			System.out.println("I, agent " + getId() + ", received " + in.getNutritionValue() + " by hunting " + lastHunted.getName());
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
			lastOrderReceived = in.getOrder();
			huntingTeam = in.getTeam();
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
			groupApplicationResponse(true);
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

	private Food lastHunted = null;
	
	private HuntingTeam huntingTeam = null;
	private Food lastOrderReceived = null;

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

		conn = PublicEnvironmentConnection.getInstance();
		onInit();
	}

	@Override
	public final void onActivation()
	{
		RegistrationRequest request = new RegistrationRequest(dm.getId(), dm.getRoles(), dm.getPublicVersion());
		ENVRegistrationResponse r = tmp_ec.register(request);
		this.authCode = r.getAuthCode();
		this.ec = ((RegistrationResponse)r).getEc();
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
		// TODO: Clear any data that is reound sepecific
		// EG the last order, thing hunted, etc. etc.
	}

	private void doGroupSelect()
	{
		String gid = chooseGroup();
		// TODO: Check string corrosponds to valid group
		ec.act(new ApplyToGroup(gid), getId(), authCode);
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
		lastHunted = toHunt;
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
	 * @return NExt random number
	 */
	public final double uniformRand()
	{
		return this.dm.random.nextDouble();
	}

	/**
	 * Called after the initialising the agent, allowing subclassses to initialise
	 * any more data.
	 */
	abstract protected void onInit();
	/**
	 * TODO: Documentation
	 */
	abstract protected void onActivate();
	/**
	 * TODO: Documentation
	 */
	abstract protected void beforeNewRound();
	/**
	 * TODO: Document
	 * @return 
	 */
	abstract protected String chooseGroup();
	/**
	 * TODO: Document
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

	/**
	 * @return The food the agent decided to hunt on the previous turn
	 */
	public final Food getLastHunted()
	{
		return lastHunted;
	}
	
	public final HuntingTeam getHuntingTeam() {
		return huntingTeam;
	}
	
	// TODO: Add function to get latest order
	// TODO: Work out which of these members shoudl be stored in the datamodel
	// TODO: MAke sure datamodel is private
	// TODO: Maybe migrate some of the functions to the PublicDataModel
	// TODO: Certinaly true of the Group 
	// TODO: Add hnadler for ApplicationResponse
	// TODO: Add abstract callback for responses to group apllications
	// TODO: Add handler for HuntOrder

}
