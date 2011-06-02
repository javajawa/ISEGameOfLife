package ise.gameoflife.environment;

import ise.gameoflife.actions.ApplyToGroup;
import ise.gameoflife.actions.Death;
import ise.gameoflife.actions.DistributeFood;
import ise.gameoflife.actions.GroupOrder;
import ise.gameoflife.actions.Hunt;
import ise.gameoflife.actions.RespondToApplication;
import ise.gameoflife.inputs.ApplicationResponse;
import ise.gameoflife.inputs.ConsumeFood;
import ise.gameoflife.inputs.HuntOrder;
import ise.gameoflife.inputs.HuntResult;
import ise.gameoflife.inputs.JoinRequest;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.models.PublicAgentDataModel;
import ise.gameoflife.participants.AbstractAgent;
import ise.gameoflife.participants.AbstractGroupAgent;
import ise.gameoflife.tokens.GroupRegistration;
import ise.gameoflife.tokens.RegistrationRequest;
import ise.gameoflife.tokens.RegistrationResponse;
import ise.gameoflife.tokens.TurnType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.simpleframework.xml.Element;
import presage.Action;
import presage.EnvDataModel;
import presage.EnvironmentConnector;
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

	Set<String> getAvailableGroups()
	{
		return dmodel.getAvailableGroups();
	}
	/**
	 * Passes on group applications
	 */
	private class ApplyToGroupHandler implements AbstractEnvironment.ActionHandler
	{
		@Override
		public boolean canHandle(Action action)
		{
			return (action.getClass().equals(ApplyToGroup.class));
		}
		
		@Override
		public Input handle(Action action, String actorID)
		{
			sim.getPlayer(((ApplyToGroup)action).getGroup()).enqueueInput(new JoinRequest(sim.getTime(), actorID));
			System.out.println("Agent " + actorID + ", has attempted to join group " + ((ApplyToGroup)action).getGroup());
			return null;
		}
		
		public ApplyToGroupHandler(){
			//Nothing to see here, move along citizen.
		}
	}
	
/**
	 * Kills (deActivates) Agents
	 */
	private class DeathHandler implements AbstractEnvironment.ActionHandler
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
			System.out.println("Agent " + actorID + ", has died. So long and thanks for all the fish.");
			return null;
		}
		
			public DeathHandler()
		{
			// Nothing to see here. Move along, citizen.
		}
	}
	/**
	 * Issues instructions of what to hunt from group to environment which then
	 * passes the order onto agents.
	 */
	private class GroupOrderHandler implements AbstractEnvironment.ActionHandler
	{
		@Override
		public boolean canHandle(Action action)
		{
			return (action.getClass().equals(GroupOrder.class));
		}
			
		@Override
		public Input handle(Action action, String actorID){
			final GroupOrder order = (GroupOrder)action;
			sim.getPlayer(order.getAgent()).enqueueInput(new HuntOrder(sim.getTime(), order.getToHunt(), order.getTeam()));
			System.out.println("Group " + actorID + " has told team " + order.getTeam() + " to hunt " + order.getToHunt().getName());
			return null;
		}
			
		public GroupOrderHandler(){
			// Nothing to see here. Move along, citizen.
		}
	}
	/**
	 * Performs Hunt Action, returns new food total, depending on result of Hunt
	 */
	private class HuntHandler implements AbstractEnvironment.ActionHandler
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

			// This line gets the agents datamodel
			// Just trust me on that one :P
			final PublicAgentDataModel am = ((AbstractAgent)sim.getPlayer(actorID)).getDataModel();
			if (am.getHuntingTeam() == null)
			{
				Input result;
				if (food.getHuntersRequired() <= 1)
				{
					result = new HuntResult(actorID, food.getNutrition(), dmodel.getTime());
				}
				else
				{
					result = new HuntResult(actorID, 0, dmodel.getTime());
				}
				sim.getPlayer(actorID).enqueueInput(result);
			}
			else
			{
				if (!storedHuntResults.containsKey(am.getHuntingTeam()))
				{
					storedHuntResults.put(am.getHuntingTeam(), new LinkedList<TeamHuntEvent>());
				}
				storedHuntResults.get(am.getHuntingTeam()).add(new TeamHuntEvent(act, actorID));
			}
			System.out.println("Agent " + actorID + " hunted " + food.getName() + " with team " + am.getHuntingTeam());
			return null;
		}

		public HuntHandler()
		{
			// Nothing to see here. Move along, citizen.
		}
	}
	/**
	 * Responds to a group application, indicating success or failure
	 */
	private class RespondToApplicationHandler implements AbstractEnvironment.ActionHandler
	{
		@Override
		public boolean canHandle(Action action)
		{
			return (action.getClass().equals(RespondToApplication.class));
		}
		
		@Override
		public Input handle(Action action, String actorID){
			RespondToApplication application = (RespondToApplication)action;
			
			sim.getPlayer(application.getAgent()).enqueueInput(new ApplicationResponse(sim.getTime(), actorID, application.wasAccepted()));
			System.out.println("Agent " + application.getAgent() + " has attempted to join group " + actorID + ", and the result was: " + application.wasAccepted());
			return null;
		}
		
		public RespondToApplicationHandler(){
			// Nothing to see here. Move along, citizen.
		}
	}
	/**
	 * Issues instructions of what to hunt from group to environment which then
	 * passes the order onto agents.
	 */
	private class DistributeFoodHandler implements AbstractEnvironment.ActionHandler
	{
		@Override
		public boolean canHandle(Action action)
		{
			return (action.getClass().equals(DistributeFood.class));
		}

		@Override
		public Input handle(Action action, String actorID){
			final DistributeFood result = (DistributeFood)action;
			sim.getPlayer(result.getAgent()).enqueueInput(new HuntResult(actorID, result.getAmount(), sim.getTime()));
			System.out.println("Agent " + actorID + " was allocated " + result.getAmount() + " units of food");
			return null;
		}

		public DistributeFoodHandler(){
			// Nothing to see here. Move along, citizen.
		}
	}

	private class TeamHuntEvent
	{
		private final Food food;
		private final String agent;

		public TeamHuntEvent(Hunt hunt, String actorID)
		{
			this.food = dmodel.getFoodById(hunt.getFoodTypeId());
			this.agent = actorID;
		}

		public String getAgent()
		{
			return agent;
		}

		public Food getFood()
		{
			return food;
		}
	}

	/**
	 * Reference to the data model, of the class which it will actually be.
	 * Hides the {@link AEnvDataModel} in the {@link AbstractEnvironment}
	 */
	@Element
	@SuppressWarnings("FieldNameHidesFieldInSuperclass")
	protected EnvironmentDataModel dmodel;
	
	/**
	 * Reference to the list that backs the ErrorLog view plugin.
	 */
	private List<String> errorLog;
	private Map<HuntingTeam, List<TeamHuntEvent>> storedHuntResults;
	private List<String> defeeredParticipantActivations;

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
		if (registrationObject instanceof RegistrationRequest)
		{
			final RegistrationRequest obj = (RegistrationRequest)registrationObject;
			if (!dmodel.registerParticipant(obj))
			{
				return null;
			}
		}
		else if (registrationObject instanceof GroupRegistration)
		{
			final GroupRegistration obj = (GroupRegistration)registrationObject;
			if (!dmodel.registerGroup(obj))
			{
				return null;
			}
		}
		UUID id = UUID.randomUUID();
		authenticator.put(registrationObject.getParticipantID(), id);
		return new RegistrationResponse(registrationObject.getParticipantID(), id, new EnvConnector(this));
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

		this.actionhandlers.add(new HuntHandler());
		this.actionhandlers.add(new DeathHandler());
		this.actionhandlers.add(new ApplyToGroupHandler());
		this.actionhandlers.add(new GroupOrderHandler());
		this.actionhandlers.add(new RespondToApplicationHandler());
		this.actionhandlers.add(new DistributeFoodHandler());

		new PublicEnvironmentConnection(new EnvConnector(this));

		storedHuntResults = new HashMap<HuntingTeam, List<TeamHuntEvent>>();
		defeeredParticipantActivations = new LinkedList<String>();
	}

	@Override
	protected void updatePerceptions()
	{
	}

	@Override
	protected void updatePhysicalWorld()
	{
		doDefeeredActivations();
		processTeamHunts();
		// Energy used on hunting, so this is when food is consumed
		if (dmodel.getTurnType() == TurnType.GoHunt)
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
		}
	}

	private void doDefeeredActivations()
	{
		for (String id : defeeredParticipantActivations)
		{
			sim.activateParticipant(id);
		}
		defeeredParticipantActivations.clear();
	}

	private void processTeamHunts()
	{
		for (HuntingTeam team : storedHuntResults.keySet())
		{
			// Reorganise each team into what they hunted
			// And who hunted it
			Map<Food,List<String>> hunters = new HashMap<Food, List<String>>();
			for (TeamHuntEvent h : storedHuntResults.get(team))
			{
				if (!hunters.containsKey(h.getFood()))
				{
					hunters.put(h.getFood(), new LinkedList<String>());
				}
				hunters.get(h.getFood()).add(h.getAgent());
			}

			// Now, for each food, see if they got a unit on it
			// TODO: Do we get more than one unit if more people succeed?
			for (Food f : hunters.keySet())
			{
				List<String> agents = hunters.get(f);
				double foodGained;
				if (agents.size() >= f.getHuntersRequired())
				{
					foodGained = f.getNutrition() / agents.size();
				}
				else
				{
					foodGained = 0;
				}

				// Then, for each agent, send the message
				String groupID = dmodel.getAgentById(agents.get(0)).getGroupId();
				Participant g = sim.getPlayer(groupID);
				for (String agent : agents)
				{
					g.enqueueInput(new HuntResult(agent, foodGained, dmodel.time));
				}
			}
		}

		storedHuntResults.clear();
	}

	@Override
	protected void updateNetwork()
	{
	}

	@Override
	public void setTime(long cycle)
	{
		this.dmodel.setTime(cycle);
	}
	
	public void setErrorLog(List<String> loginput)
	{
		if (this.errorLog == null) this.errorLog = loginput;
	}

	void logToErrorLog(String s)
	{
		if (this.errorLog == null)
		{
			System.err.println(s);
			return;
		}

		this.errorLog.add(s);
	}

	void logToErrorLog(Throwable s)
	{
		if (this.errorLog == null)
		{
			System.err.println(s.getMessage());
			return;
		}

		this.errorLog.add(s.getMessage());
	}

	public TurnType getCurrentTurnType()
	{
		return dmodel.getTurnType();
	}

	public int getCyclesPassed()
	{
		return dmodel.getCyclesPassed();
	}

	String createGroup(Class<? extends AbstractGroupAgent> groupType)
	{
		AbstractGroupAgent g = dmodel.createGroup(groupType);
		g.initialise(new EnvironmentConnector(this));
		sim.addParticipant(g.getId(), g);
		sim.activateParticipant(g.getId());
		//defeeredParticipantActivations.add(g.getId());
		return g.getId();
	}

	List<Class<? extends AbstractGroupAgent>> getAllowedGroupTypes()
	{
		return dmodel.getAllowedGroupTypes();
	}

	boolean isAgentId(String id)
	{
		if (id == null) return false;
		if (sim.isParticipantActive(id))
		{
			return (sim.getPlayer(id)) instanceof AbstractAgent;
		}
		return false;
	}

	boolean isGroupId(String gid)
	{
		if (gid == null) return false;
		if (sim.isParticipantActive(gid)){
			if (sim.getPlayer(gid) instanceof AbstractGroupAgent)
			{
				return true;
			}
		} 
		return false;
	}  
		
}
