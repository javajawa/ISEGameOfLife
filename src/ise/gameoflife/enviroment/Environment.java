package ise.gameoflife.enviroment;

import ise.gameoflife.actions.Death;
import ise.gameoflife.actions.Hunt;
import ise.gameoflife.inputs.ConsumeFood;
import ise.gameoflife.inputs.HuntResult;
import ise.gameoflife.models.Food;
import ise.gameoflife.tokens.RegistrationRequest;
import ise.gameoflife.tokens.RegistrationResponse;
import java.util.UUID;
import org.simpleframework.xml.Element;
import presage.Action;
import presage.EnvDataModel;
import presage.Input;
import presage.Participant;
import presage.Simulation;
import presage.environment.AEnvDataModel;
import presage.environment.AbstractEnvironment;
import presage.environment.messages.ENVDeRegisterRequest;
import presage.environment.messages.ENVRegisterRequest;
import presage.environment.messages.ENVRegistrationResponse;

/**
 * The primary environment code for the GameOfLife that we define. This will
 * contain a list of all the groups, food etc. etc. that exist in the world.
 * @author Benedict Harcourt
 */
public class Environment extends AbstractEnvironment
{
/**
	 * Kills (deActivates) Agents
	 */
	public class DeathHandler implements AbstractEnvironment.ActionHandler
	{
		@Override
		public boolean canHandle(Action action)
		{
			return (action.getClass().equals(Death.class));
		}
		
		@Override
		public Input handle(Action action, String actorID)
		{
			sim.deActivateParticipant(actorID);
			System.out.println("I, agent " + actorID + ", have died. So long and thanks for all the fish.");
			return null;
		}
		
			public DeathHandler()
		{
			// Nothing to see here. Move along, citizen.
		}
	}
	
	/**
	 * Performs Hunt Action, returns new food total, depending on result of Hunt
	 */
	public class HuntHandler implements AbstractEnvironment.ActionHandler
	{

		@Override
		public boolean canHandle(Action action)
		{
			return (action.getClass().equals(Hunt.class));
		}

		@Override
		public Input handle(Action action, String actorID)
		{
			final Hunt act = (Hunt)action;
			final Food food = dmodel.getFoodById(act.getFoodTypeId());

			Input result;
			if (food.getHuntersRequired() <= 1)
			{
				result = new HuntResult(food.getNutrition(), dmodel.getTime());
				sim.getPlayer(actorID).enqueueInput(result);
			}
			else
			{
				result = new HuntResult(0, dmodel.getTime());
			}

			return result;
		}

		public HuntHandler()
		{
			// Nothing to see here. Move along, citizen.
		}
	}
	
	/**
	 * Reference to the data model, of the class which it will actually be.
	 * Hides the {@link AEnvDataModel} in the {@link AbstractEnvironment}
	 */
	@Element
	@SuppressWarnings("FieldNameHidesFieldInSuperclass")
	protected EnvironmentDataModel dmodel;
	
	@Deprecated
	public Environment()
	{
		super();
	}
	
	@presage.annotations.EnvironmentConstructor( { "queueactions",
			"randomseed", "dmodel" })
	public Environment(boolean queueactions, long randomseed,
			EnvironmentDataModel dmodel) {
		super(queueactions, randomseed);

		// Separation of data from code!
		this.dmodel = dmodel;
	}

	@Override
	public boolean deregister(ENVDeRegisterRequest deregistrationObject)
	{
		if (!authenticator.get(deregistrationObject.getParticipantID()).equals(deregistrationObject.getParticipantAuthCode()))
		{
			return false;
		}
		return dmodel.removeParticipant(deregistrationObject.getParticipantID());
	}

	@Override
	public ENVRegistrationResponse onRegister(ENVRegisterRequest registrationObject)
	{
		final RegistrationRequest obj = (RegistrationRequest)registrationObject;
		if (!dmodel.registerParticipant(obj))
		{
			return null;
		}
		UUID id = UUID.randomUUID();
		authenticator.put(registrationObject.getParticipantID(), id);
		return new RegistrationResponse(obj.getParticipantID(), id, new EnvConnector(this));
	}

	@Override
	public EnvDataModel getDataModel()
	{
		return dmodel;
	}

	@Override
	protected void onInitialise(Simulation sim)
	{
		this.sim = sim;
		// TODO: Add message handlers
		this.actionhandlers.add(new HuntHandler());
		this.actionhandlers.add(new DeathHandler());
	}

	@Override
	protected void updatePerceptions()
	{
		// FIXME: Write this function
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected void updatePhysicalWorld()
	{
		for (Participant agent : sim.players.values())
		{
			if (sim.isParticipantActive(agent.getId()))
			{
				if (agent instanceof ise.gameoflife.participants.AbstractAgent)
				{
					agent.enqueueInput(new ConsumeFood(dmodel.getTime()));
				}
			}
		}
		// FIXME: Write this function
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected void updateNetwork()
	{
		// FIXME: Write this function
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setTime(long cycle)
	{
		this.dmodel.setTime(cycle);
	}
	
}
