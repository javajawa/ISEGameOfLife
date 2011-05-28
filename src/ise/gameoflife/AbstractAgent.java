package ise.gameoflife;

import ise.gameoflife.actions.Hunt;
import ise.gameoflife.enviroment.EnvConnector;
import ise.gameoflife.inputs.ConsumeFood;
import ise.gameoflife.inputs.HuntResult;
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
import presage.util.InputQueue;

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

	private class ConsumeFoodHandler implements InputHandler
	{
		@Override
		public boolean canHandle(Input input){
			return (input instanceof ConsumeFood);
		}

		@Override
		public boolean handle(Input input)
		{
			dm.foodConsumed(dm.getFoodConsumption());
			return true;
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
		public boolean handle(Input input)
		{
			final HuntResult in = (HuntResult)input;
			dm.foodAquired(in.getNutritionValue());
			return true;
		}
		
	}

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
	/**
	 * Reference to the environment connector, that allows the agent to interact
	 * with the environment
	 */
	protected EnvConnector ec;
	private EnvironmentConnector tmp_ec;

	private InputQueue msgQ = new InputQueue("inputs");
	private ArrayList<InputHandler> handlers = new ArrayList<InputHandler>();

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
	public void initialise(EnvironmentConnector environmentConnector)
	{
		if (beenInitalised) throw new IllegalStateException("This object has already been initialised");
		beenInitalised = true;

		System.out.println(environmentConnector.getClass().getCanonicalName());
		tmp_ec = environmentConnector;
		dm.initialise(environmentConnector);

		this.handlers.add(new ConsumeFoodHandler());
		this.handlers.add(new HuntResultHandler());

		onInit(environmentConnector);
	}

	@Override
	public final void onActivation()
	{
		RegistrationRequest request = new RegistrationRequest(dm.getId(), dm.getRoles(), dm.getPublicVersion());
		ENVRegistrationResponse r = tmp_ec.register(request);
		this.authCode = r.getAuthCode();
		this.ec = ((RegistrationResponse)r).getEc();
		onActivate();
	}

	@Override
	public final void onDeActivation()
	{
		ec.deregister(new UnregisterRequest(dm.getId(), dm.getGroupId()));
	}

	@Override
	public final void execute()
	{
		Input i;
		i = msgQ.dequeue();
		while (i != null)
		{
			handleInput(i);
			i = msgQ.dequeue();
		}

		// Output how much food we have.
		System.out.println("I, " + this.getId() + ", has " + this.dm.getFoodInPossesion() + " units of food remaining");

		if (this.dm.getFoodInPossesion() <= 0)
		{
			System.out.println("I, " + this.getId() + ", am out of food");
			onDeActivation();
		}

		// TODO: Check for turn type
		Food toHunt = chooseFood();

		if (toHunt == null) return;
		ec.act(new Hunt(toHunt), this.getId(), authCode);
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
	public final PlayerDataModel getInternalDataModel()
	{
		return dm.getPublicVersion();
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
	 * any more data. The primary environment connector will not be available at
	 * this point, but rather when the agent is activated
	 * @param ec The <strong>default</strong> environment connector
	 */
	abstract protected void onInit(EnvironmentConnector ec);
	abstract protected void onActivate();
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
