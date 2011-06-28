package ise.mace.environment;

import ise.mace.actions.ApplyToGroup;
import ise.mace.actions.Death;
import ise.mace.actions.DistributeFood;
import ise.mace.actions.GroupOrder;
import ise.mace.actions.Hunt;
import ise.mace.actions.Proposal;
import ise.mace.actions.RespondToApplication;
import ise.mace.actions.Vote;
import ise.mace.actions.VoteResult;
import ise.mace.agents.PoliticalAgentGroup;
import ise.mace.inputs.ApplicationResponse;
import ise.mace.inputs.ConsumeFood;
import ise.mace.inputs.GroupInvite;
import ise.mace.inputs.HuntOrder;
import ise.mace.inputs.HuntResult;
import ise.mace.inputs.JoinRequest;
import ise.mace.inputs.LeaveNotification;
import ise.mace.inputs.Proposition;
import ise.mace.models.Food;
import ise.mace.models.GroupDataInitialiser;
import ise.mace.models.HuntingTeam;
import ise.mace.participants.PublicAgentDataModel;
import ise.mace.participants.AbstractAgent;
import ise.mace.participants.AbstractFreeAgentGroup;
import ise.mace.participants.AbstractGroupAgent;
import ise.mace.participants.PublicGroupDataModel;
import ise.mace.tokens.AgentType;
import ise.mace.tokens.GroupRegistration;
import ise.mace.tokens.RegistrationRequest;
import ise.mace.tokens.RegistrationResponse;
import ise.mace.tokens.TurnType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Element;
import presage.Action;
import presage.EnvDataModel;
import presage.EnvironmentConnector;
import presage.Input;
import presage.Participant;
import presage.Simulation;
import presage.annotations.EnvironmentConstructor;
import presage.environment.AbstractEnvironment;
import presage.environment.messages.ENVDeRegisterRequest;
import presage.environment.messages.ENVRegisterRequest;
import presage.environment.messages.ENVRegistrationResponse;

/**
 * The primary environment code for the games that we define. This will
 * contain a list of all the groups, food etc. etc. that exist in the world.
 */
@SuppressWarnings("ClassWithMultipleLoggers")
public class Environment extends AbstractEnvironment
{
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
			final ApplyToGroup app = (ApplyToGroup)action;
			if (app.getGroup().equals(AbstractAgent.leaveGroup))
			{
				String old_group = dmodel.getAgentById(actorID).getGroupId();

				if (old_group != null)
				{
					sim.getPlayer(old_group).enqueueInput(new LeaveNotification(
									sim.getTime(), LeaveNotification.Reasons.Other, actorID));
				}
				logger.log(Level.FINE, "Agent {0} has rejoined the free agents group.",
								nameOf(actorID));
			}
			else
			{
				sim.getPlayer(app.getGroup()).enqueueInput(new JoinRequest(sim.getTime(),
								actorID));
				logger.log(Level.FINE, "Agent {0} has attempted to join group {1}",
								new Object[]
								{
									nameOf(actorID),
									nameOf(app.getGroup())
								});
			}
			return null;
		}

		/**
		 * Creates an instance of this action handler
		 */
		ApplyToGroupHandler()
		{
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
			if (isAgentId(actorID))
			{
				String group = dmodel.getAgentById(actorID).getGroupId();
				if (group != null)
				{
					sim.getPlayer(group).enqueueInput(
									new LeaveNotification(dmodel.time,
									LeaveNotification.Reasons.Death, actorID));
				}
				logger.log(Level.FINE,
								"Agent {0} has died. So long, and thanks for all the fish.",
								nameOf(actorID));
			}
			else
			{
				logger.log(Level.FINE,
								"Group {0} is empty. So long, and thanks for all the fish.",
								nameOf(actorID));
			}
			sim.deActivateParticipant(actorID);
			return null;
		}

		/**
		 * Creates an instance of this action handler
		 */
		DeathHandler()
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
		public Input handle(Action action, String actorID)
		{
			final GroupOrder order = (GroupOrder)action;
			sim.getPlayer(order.getAgent()).enqueueInput(new HuntOrder(sim.getTime(),
							order.getTeam()));
			logger.log(Level.FINE, "Group {0} has created team {1} including {2}",
							new Object[]
							{
								nameOf(actorID),
								order.getTeam().hashCode(), nameOf(order.getAgent())
							});
			return null;
		}

		/**
		 * Creates an instance of this action handler
		 */
		GroupOrderHandler()
		{
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
					result = new HuntResult(actorID, food.getNutrition(),
									food.getNutrition(), dmodel.getTime());
				}
				else
				{
					result = new HuntResult(actorID, 0, 0, dmodel.getTime());
				}
				sim.getPlayer(actorID).enqueueInput(result);
			}
			else
			{
				if (!storedHuntResults.containsKey(am.getHuntingTeam()))
				{
					storedHuntResults.put(am.getHuntingTeam(),
									new LinkedList<TeamHuntEvent>());
				}
				storedHuntResults.get(am.getHuntingTeam()).add(new TeamHuntEvent(act,
								actorID));
			}
			logger.log(Level.FINE, "Agent {0} hunted {1}{2}", new Object[]
							{
								nameOf(actorID),
								food.getName(),
								am.getHuntingTeam() == null ? " alone." : " with team " + am.getHuntingTeam().hashCode()
							});
			return null;
		}

		/**
		 * Creates an instance of this action handler
		 */
		HuntHandler()
		{
			// Nothing to see here. Move along, citizen.
		}
	}

	/**
	 * Responds to a group application, indicating success or failure
	 */
	private class RespondToApplicationHandler implements
					AbstractEnvironment.ActionHandler
	{
		@Override
		public boolean canHandle(Action action)
		{
			return (action.getClass().equals(RespondToApplication.class));
		}

		@Override
		public Input handle(Action action, String actorID)
		{
			RespondToApplication application = (RespondToApplication)action;

			sim.getPlayer(application.getAgent()).enqueueInput(new ApplicationResponse(
							sim.getTime(), actorID, application.wasAccepted()));
			if (application.wasAccepted())
			{
				String old_group = dmodel.getAgentById(application.getAgent()).getGroupId();
				if (old_group != null)
				{
					sim.getPlayer(old_group).enqueueInput(new LeaveNotification(
									sim.getTime(), LeaveNotification.Reasons.Other,
									application.getAgent()));
				}
			}
			logger.log(Level.FINE,
							"Agent {0} has attempted to join group {1}, and the result was: {2}",
							new Object[]
							{
								nameOf(application.getAgent()),
								nameOf(actorID), application.wasAccepted()
							});
			return null;
		}

		/**
		 * Creates an instance of this action handler
		 */
		RespondToApplicationHandler()
		{
			// Nothing to see here. Move along, citizen.
		}
	}

	/**
	 * Issues instructions of what to hunt from group to environment which then
	 * passes the order onto agents.
	 */
	private class DistributeFoodHandler implements
					AbstractEnvironment.ActionHandler
	{
		@Override
		public boolean canHandle(Action action)
		{
			return (action.getClass().equals(DistributeFood.class));
		}

		@Override
		public Input handle(Action action, String actorID)
		{
			final DistributeFood result = (DistributeFood)action;
			sim.getPlayer(result.getAgent()).enqueueInput(new HuntResult(actorID,
							result.getAmountHunted(), result.getAmountRecieved(),
							sim.getTime()));
			logger.log(Level.FINE, "Agent {0} was allocated {1} units of food",
							new Object[]
							{
								nameOf(result.getAgent()),
								result.getAmountRecieved()
							});
			return null;
		}

		/**
		 * Creates an instance of this action handler
		 */
		DistributeFoodHandler()
		{
			// Nothing to see here. Move along, citizen.
		}
	}

	/**
	 * Handles {@link Proposal}
	 */
	private class ProposalHandler implements AbstractEnvironment.ActionHandler
	{
		@Override
		public boolean canHandle(Action action)
		{
			return (action.getClass().equals(Proposal.class));
		}

		@Override
		public Input handle(Action action, String actorID)
		{
			final Proposal prop = (Proposal)action;
			final Proposition p = new Proposition(prop.getType(), actorID,
							prop.getForGroup(), dmodel.time);

			logger.log(Level.FINE, "Agent {0} proposed a motion of  {1} to group {2}",
							new Object[]
							{
								nameOf(actorID),
								prop.getType(), nameOf(prop.getForGroup())
							});
			if (dmodel.getGroupById(prop.getForGroup()) == null) return null;
			for (String member : dmodel.getGroupById(prop.getForGroup()).getMemberList())
			{
				sim.getPlayer(member).enqueueInput(p);
			}

			return null;
		}

		/**
		 * Creates an instance of this action handler
		 */
		ProposalHandler()
		{
			// Nothing to see here. Move along, citizen.
		}
	}

	/**
	 * Handles {@link Vote}
	 */
	private class VoteHandler implements AbstractEnvironment.ActionHandler
	{
		@Override
		public boolean canHandle(Action action)
		{
			return (action.getClass().equals(Vote.class));
		}

		@Override
		public Input handle(Action action, String actorID)
		{
			final Vote vote = (Vote)action;
			final ise.mace.inputs.Vote v = new ise.mace.inputs.Vote(vote, dmodel.time,
							actorID);

			logger.log(Level.FINE,
							"Agent {0} voted {1} on a motion of  {2} to group {3}",
							new Object[]
							{
								nameOf(actorID),
								vote.getVote(), vote.getProposition().getType(),
								nameOf(vote.getProposition().getOwnerGroup())
							});
			sim.getPlayer(vote.getProposition().getOwnerGroup()).enqueueInput(v);
			return null;
		}

		/**
		 * Creates an instance of this action handler
		 */
		VoteHandler()
		{
			// Nothing to see here. Move along, citizen.
		}
	}

	/**
	 * Handles {@link VoteResult}
	 */
	private class VoteResultHandler implements AbstractEnvironment.ActionHandler
	{
		@Override
		public boolean canHandle(Action action)
		{
			return (action.getClass().equals(VoteResult.class));
		}

		@Override
		public Input handle(Action action, String actorID)
		{
			final VoteResult vote = (VoteResult)action;
			final ise.mace.inputs.VoteResult v = new ise.mace.inputs.VoteResult(vote,
							dmodel.time);

			sim.getPlayer(vote.getProposition().getProposer()).enqueueInput(v);
			return null;
		}

		/**
		 * Creates an instance of this action handler
		 */
		VoteResultHandler()
		{
			// Nothing to see here. Move along, citizen.
		}
	}

	/**
	 * Stores information about a {@link Hunt} action which included a non-null
	 * {@link HuntingTeam} so that the actions of the team can be reviewed
	 * together bt {@link #processTeamHunts()}
	 */
	private class TeamHuntEvent
	{
		/**
		 * The agent
		 */
		private final String agent;
		/**
		 * The food that the agent hunted
		 */
		private final Food food;

		/**
		 * Creates a new Team Hunt Event record based off the data passed to the
		 * {@link HuntHandler}, namely the Hunt and the ActorID
		 * @param hunt The hunt object
		 * @param actorID The hunter's id
		 */
		TeamHuntEvent(Hunt hunt, String actorID)
		{
			this.food = dmodel.getFoodById(hunt.getFoodTypeId());
			this.agent = actorID;
		}

		/**
		 * Returns the agent
		 * @return The agent
		 */
		String getAgent()
		{
			return agent;
		}

		/**
		 * Returns the food that was hunted
		 * @return The food that was hunted
		 */
		Food getFood()
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
	private EnvironmentDataModel dmodel;
	/**
	 * <p>Reference to the class that will be used as the {@link
	 * AbstractFreeAgentGroup Free Agent Group}.</p>
	 * <p>It may be null. It may not. If it is not, it is used to create
	 * the {@link #fAGroup Free Agent Group} instance, and so it must has a
	 * public no-args constructor</p>
	 */
	@Element(required = false)
	private Class<? extends AbstractFreeAgentGroup> freeAgentHandler;
	/**
	 * The Free Agent Group, which is used to team up agents not in groups
	 */
	private AbstractFreeAgentGroup fAGroup;
	/**
	 * Reference to the main logger for the framework
	 */
	private final static Logger logger = Logger.getLogger("mace.Main");
	/**
	 * Reference to the root logger for mace
	 */
	private final static Logger rootLogger = Logger.getLogger("mace");
	/**
	 * Temporary storage of hunt data from agents hunting in teams
	 */
	private Map<HuntingTeam, List<TeamHuntEvent>> storedHuntResults;

	/**
	 * Public no-args constructor for serialisation
	 * @deprecated For serialisation only
	 */
	@Deprecated
	public Environment()
	{
		super();
	}

	/**
	 * Creates a new Environment with the specified parameters
	 * @param randomseed Seed for random number generator
	 * @param dmodel The data model for the environment
	 * @param freeAgentHandler The handler class to use for ungrouped agents, see
	 * {@link #freeAgentHandler}
	 */
	@EnvironmentConstructor(
	{
		"queueactions", "randomseed", "dmodel"
	})
	public Environment(long randomseed,
					EnvironmentDataModel dmodel,
					Class<? extends AbstractFreeAgentGroup> freeAgentHandler)
	{
		super(true, randomseed);
		this.dmodel = dmodel;
		this.freeAgentHandler = freeAgentHandler;
	}

	/**
	 * <p>Callback for when a {@link Participant} leaves the environment</p>
	 * @param deregistrationObject De-register object
	 * @return Whether the de-registration was successful. This will be false if
	 * the participant was not registered with the Environment
	 */
	@Override
	public boolean deregister(ENVDeRegisterRequest deregistrationObject)
	{
		if (!authenticator.get(deregistrationObject.getParticipantID()).equals(
						deregistrationObject.getParticipantAuthCode()))
		{
			logger.log(Level.FINE, "Illegal deregister request from {0}",
							nameOf(deregistrationObject.getParticipantID()));
			return false;
		}

		boolean succeeded = dmodel.removeParticipant(
						deregistrationObject.getParticipantID());
		logger.log(Level.FINE, "Deregister request from {0} reutrned {1}",
						new Object[]
						{
							nameOf(deregistrationObject.getParticipantID()), succeeded
						});
		return succeeded;
	}

	/**
	 * <p>Callback for when an agent registers with the environment</p>
	 * @param registrationObject The registration object. {@link AbstractAgent
	 * agents} should use the {@link RegistrationRequest} class, {@link
	 * AbstractGroupAgent groups} should use the {@link GroupRegistration} class
	 * @return The response to the registration request, or null if the
	 * registration failed
	 * @see #deregister(presage.environment.messages.ENVDeRegisterRequest)
	 */
	@Override
	public ENVRegistrationResponse onRegister(
					ENVRegisterRequest registrationObject)
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
		return new RegistrationResponse(registrationObject.getParticipantID(), id,
						new EnvironmentConnection(this));
	}

	/**
	 * Returns the {@link EnvironmentDataModel}
	 * @return The {@link EnvironmentDataModel}
	 */
	@Override
	public EnvDataModel getDataModel()
	{
		return dmodel;
	}

	/**
	 * <p>Initialises this simulation</p>
	 * <ul>
	 * <li>Creates and adds the ActionHandlers</li>
	 * <li>Creates the {@link PublicEnvironmentConnection}</li>
	 * <li>Creates the {@link AbstractFreeAgentGroup Free Agent Group}</li>
	 * </ul>
	 * @param sim The simulation this environment is a part of
	 */
	@Override
	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	protected void onInitialise(Simulation sim)
	{
		this.sim = sim;

		this.actionhandlers.add(new HuntHandler());
		this.actionhandlers.add(new DeathHandler());
		this.actionhandlers.add(new ApplyToGroupHandler());
		this.actionhandlers.add(new GroupOrderHandler());
		this.actionhandlers.add(new RespondToApplicationHandler());
		this.actionhandlers.add(new DistributeFoodHandler());
		this.actionhandlers.add(new ProposalHandler());
		this.actionhandlers.add(new VoteHandler());
		this.actionhandlers.add(new VoteResultHandler());

		new PublicEnvironmentConnection(this);

		storedHuntResults = new HashMap<HuntingTeam, List<TeamHuntEvent>>();

		if (freeAgentHandler != null)
		{
			try
			{
				fAGroup = freeAgentHandler.newInstance();
			}
			catch (Exception ex)
			{
				logger.log(Level.SEVERE,
								"Evver whilst trying to load FreeAgentGroup class" + freeAgentHandler.getCanonicalName(),
								ex);
			}
		}

		authenticator.put(getId(), UUID.fromString(getId()));
	}

	/**
	 * Unused abstract function
	 */
	@Override
	protected void updatePerceptions()
	{
	}

	/**
	 * <p>Environment's primary execution function</p>
	 * <ul>
	 * <li>{@link #processTeamHunts() Processes Team Hunts}</li>
	 * <li>On {@link TurnType#TeamSelect Team Select} turn, calls the {@link
	 * AbstractFreeAgentGroup Free Agent Group} to put all {@link AbstractAgent
	 * agents} (who are not in {@link AbstractGroupAgent groups}) int {@link
	 * HuntingTeam teams}</li>
	 * </ul>
	 */
	@Override
	protected void updatePhysicalWorld()
	{
		processTeamHunts();

		// Deal with ungrouped agents having teams formed
		if (fAGroup != null && dmodel.getTurnType() == TurnType.TeamSelect)
		{
			List<HuntingTeam> teams = fAGroup.selectTeams(dmodel.getUngroupedAgents());
			for (HuntingTeam team : teams)
			{
				for (String agent : team.getMembers())
				{
					sim.getPlayer(agent).enqueueInput(new HuntOrder(sim.getTime(), team));
					logger.log(Level.FINE,
									"FreeAgentsGroup has created team {0} including {1}",
									new Object[]
									{
										team.hashCode(),
										nameOf(agent)
									});
				}
			}
		}

		// Energy used on hunting, so this is when food is consumed
		if (dmodel.getTurnType() == TurnType.GoHunt)
		{
			for (Participant agent : sim.players.values())
			{
				if (sim.isParticipantActive(agent.getId()))
				{
					if (agent instanceof ise.mace.participants.AbstractAgent)
					{
						agent.enqueueInput(new ConsumeFood(dmodel.getTime()));
					}
				}
			}
		}
	}

	/**
	 * Processes the {@link Hunt} actions of {@link AbstractAgent agents} that
	 * were hunting as part of {@link HuntingTeam HuntingTeams} whose hunt records
	 * were added to {@link #storedHuntResults}
	 */
	private void processTeamHunts()
	{
		for (HuntingTeam team : storedHuntResults.keySet())
		{
			// Reorganise each team into what they hunted
			// And who hunted it
			Map<Food, List<String>> hunters = new HashMap<Food, List<String>>();
			for (TeamHuntEvent h : storedHuntResults.get(team))
			{
				if (!hunters.containsKey(h.getFood()))
				{
					hunters.put(h.getFood(), new LinkedList<String>());
				}
				hunters.get(h.getFood()).add(h.getAgent());
			}

			// Now, for each food, see if they got a unit on it
			for (Food f : hunters.keySet())
			{
				List<String> agents = hunters.get(f);
				double foodGained;

				int count = 0;
				while ((count + 1) * f.getHuntersRequired() <= agents.size())
				{
					++count;
				}

				foodGained = count * f.getNutrition() / agents.size();

				// Then, for each agent, send the message
				String groupID = dmodel.getAgentById(agents.get(0)).getGroupId();
				if (groupID == null)
				{
					for (String agent : agents)
					{
						sim.getPlayer(agent).enqueueInput(new HuntResult(agent, foodGained,
										foodGained, dmodel.time));
					}
				}
				else
				{
					Participant g = sim.getPlayer(groupID);
					for (String agent : agents)
					{
						g.enqueueInput(new HuntResult(agent, foodGained, 0, dmodel.time));
					}
				}
			}
		}

		storedHuntResults.clear();
	}

	/**
	 * Unused abstract function
	 */
	@Override
	protected void updateNetwork()
	{
	}

	/**
	 * Updates the time in the model
	 * @param cycle New cycle time
	 */
	@Override
	public void setTime(long cycle)
	{
		this.dmodel.setTime(cycle);
	}

	/**
	 * Gets the public Logger
	 * @return A static logger instance
	 */
	Logger getMainLogger()
	{
		return logger;
	}

	/**
	 * Returns what {@link TurnType turn} it is in the round
	 * @return The current turn type
	 * @see #getRoundsPassed()
	 */
	public TurnType getCurrentTurnType()
	{
		return dmodel.getTurnType();
	}

	/**
	 * Gets the total number of rounds past
	 * @return Total completed rounds
	 * @see TurnType
	 */
	public int getRoundsPassed()
	{
		return dmodel.getCyclesPassed();
	}

	/**
	 * Creates an agent that represents a group
	 * @param food Amount of food
	 * @param economic Economic location
	 * @param social Social location
	 * @param name Name
	 * @return The id of the Group-Agent
	 * @see PoliticalAgentGroup
	 */
	String createGroupAgent(double food, double economic, double social,
					String name)
	{
		AbstractAgent a = new PoliticalAgentGroup(food, 0, AgentType.R, economic,
						social, name);
		a.initialise(new EnvironmentConnector(this));
		sim.addParticipant(a.getId(), a);
		sim.activateParticipant(a.getId());
		return a.getId();
	}

	/**
	 * Create a new {@link AbstractGroupAgent group} of a particular class,
	 * initialise it, and invite some {@link AbstractAgent agents} to the group
	 * @param type The class of group that you wish to create
	 * @param init The initialiser instance to initialise the group with
	 * @return The id of the new group, or null if the group could not be created
	 * @throws IllegalArgumentException If the group class is not in the list of
	 * @throws RuntimeException If the the reflection libraries or constructor 
	 * throw an exception
	 * {@link #getAllowedGroupTypes() permissible gorup classes}, or if it can not
	 * be initialised with a {@link GroupDataInitialiser}
	 * @see #createGroup(java.lang.Class, ise.mace.models.GroupDataInitialiser)
	 * @see #getAllowedGroupTypes()
	 * @see AbstractGroupAgent
	 * @see GroupDataInitialiser
	 */
	String createGroup(Class<? extends AbstractGroupAgent> type,
										 GroupDataInitialiser init)
	{
		AbstractGroupAgent g = dmodel.createGroup(type, init);
		g.initialise(new EnvironmentConnector(this));
		sim.addParticipant(g.getId(), g);
		sim.activateParticipant(g.getId());
		return g.getId();
	}

	/**
	 * Create a new {@link AbstractGroupAgent group} of a particular class,
	 * initialise it, and invite some {@link AbstractAgent agents} to the group
	 * @param type The class of group that you wish to create
	 * @param init The initialiser instance to initialise the group with
	 * @param invitees A list of the ids of all agents you want to invite
	 * @return The id of the new group, or null if the group could not be created
	 * @throws IllegalArgumentException If the group class is not in the list of
	 * @throws RuntimeException If the the reflection libraries or constructor 
	 * throw an exception
	 * {@link #getAllowedGroupTypes() permissible gorup classes}, or if it can not
	 * be initialised with a {@link GroupDataInitialiser}
	 * @see #createGroup(java.lang.Class, ise.mace.models.GroupDataInitialiser)
	 * @see #getAllowedGroupTypes()
	 * @see AbstractGroupAgent
	 * @see GroupDataInitialiser
	 */
	String createGroup(Class<? extends AbstractGroupAgent> type,
										 GroupDataInitialiser init, String... invitees)
	{
		String gid = createGroup(type, init);
		if (gid != null)
		{
			for (String agent : invitees)
			{
				sim.getPlayer(agent).enqueueInput(new GroupInvite(sim.getTime(), gid));
			}
		}
		return gid;
	}

	/**
	 * Returns the list of all classes of group that may be used in this
	 * simulation.
	 *
	 * Each of these classes will extend {@link AbstractGroupAgent}.
	 * The validity of this classes is not checked at simulation time - the
	 * framework may not be able to initialise them with a {@link
	 * GroupDataInitialiser}.
	 *
	 * The returned classes are also not guaranteed to be concrete
	 * implementations.
	 *
	 * Groups of these classes can then be created with {@link
	 * #createGroup(java.lang.Class, ise.mace.models.GroupDataInitialiser)
	 * createGroup()} with an appropriate initialiser.
	 * @return List of all permitted Group classes
	 * @see AbstractGroupAgent
	 * @see GroupDataInitialiser
	 * @see #createGroup(java.lang.Class, ise.mace.models.GroupDataInitialiser)
	 * @see #getGroups()
	 */
	List<Class<? extends AbstractGroupAgent>> getAllowedGroupTypes()
	{
		return dmodel.getAllowedGroupTypes();
	}

	/**
	 * Determines if the supplied UUID identifies an {@link AbstractAgent agnet}
	 * @param id The UUID (or derivative id)
	 * @return Whether this represents a group
	 * @see #isGroupId(java.lang.String)
	 * @see PublicAgentDataModel#getId()
	 * @see PublicGroupDataModel#getId()
	 */
	boolean isAgentId(String id)
	{
		if (id == null) return false;
		if (sim.isParticipantActive(id))
		{
			return (sim.getPlayer(id)) instanceof AbstractAgent;
		}
		return false;
	}

	/**
	 * Determines if the supplied UUID identifies a {@link AbstractGroupAgent
	 * group}
	 * @param gid The UUID (or derivative id)
	 * @return Whether this represents a group
	 * @see #isAgentId(java.lang.String)
	 * @see PublicAgentDataModel#getId()
	 * @see PublicGroupDataModel#getId()
	 */
	boolean isGroupId(String gid)
	{
		if (gid == null) return false;
		if (sim.isParticipantActive(gid))
		{
			if (sim.getPlayer(gid) instanceof AbstractGroupAgent)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the {@link Environment} Environment's id
	 * @return The Environment's id
	 * @see Environment#getId()
	 */
	public String getId()
	{
		return dmodel.getId();
	}

	/**
	 * Returns the set of all {@link AbstractGroupAgent groups} current active
	 * @return IDs of all current groups
	 * @see #getGroupById(java.lang.String)
	 * @see #getAgents()
	 */
	Set<String> getGroups()
	{
		return dmodel.getAvailableGroups();
	}

	/**
	 * Get advice for a calling {@link AbstractAgent agent} from a remote agent
	 * as the best option for hunting
	 * @param agent The source agent
	 * @param authToken The token used to verify the source agent's identification
	 * @param fromAgent The agent that is to advise the source agent
	 * @param agentsTeam The team that the source agent is a member of
	 * @return The food that they are advised to hunt
	 * @see AbstractAgent#advise(java.lang.String, ise.mace.models.HuntingTeam)
	 * @see AbstractAgent#seekAvice(java.lang.String)
	 * @see AbstractAgent#giveAdvice(java.lang.String, ise.mace.models.HuntingTeam)
	 */
	Food seekAdvice(String agent, UUID authToken, String fromAgent,
					HuntingTeam agentsTeam)
	{
		if (authenticator.get(agent) != authToken) throw new IllegalAccessError(
							"Incorrect access credentials");
		AbstractAgent a = (AbstractAgent)sim.getPlayer(fromAgent);
		AbstractAgent b = (AbstractAgent)sim.getPlayer(agent);

		String ga = a.getDataModel().getGroupId();
		String gb = b.getDataModel().getGroupId();
		if (ga == null ? gb != null : !ga.equals(gb)) return null;

		return a.advise(agent, agentsTeam);
	}

	/**
	 * Gets the amount of food consumes by asking someone for advice
	 * @return Amount of food
	 * @see AbstractAgent#seekAvice(java.lang.String)
	 */
	double getFoodConsumedPerAdvice()
	{
		return dmodel.getFoodConsumedPerAdvice();
	}

	/**
	 * Returns the name of an {@link AbstractAgent agent} or {@link
	 * AbstractGroupAgent group} based on a given id
	 * @param id The id
	 * @return The name matched with that id (or null if not located)
	 * @see PublicAgentDataModel#getName()
	 * @see PublicGroupDataModel#getName()
	 */
	String nameOf(String id)
	{
		if (this.isAgentId(id))
		{
			return dmodel.getAgentById(id).getName();
		}
		if (this.isGroupId(id))
		{
			return dmodel.getGroupById(id).getName();
		}
		return null;
	}
	/**
	 * Returns the set of all {@link AbstractAgent agents} current alive
	 * @return IDs of all current agents
	 * @see #getAgentById(java.lang.String)
	 * @see #getGroups()
	 */
	Set<String> getAgents()
	{
		return dmodel.getAgents();
	}
	/**
	 * Returns a list of all {@link AbstractAgent agents} that are not currently
	 * part of a {@link AbstractGroupAgent group}
	 * @return The IDs of all un-grouped agents
	 * @see #getAgentById(java.lang.String) getAgentById
	 */
	List<String> getUngroupedAgents()
	{
		return dmodel.getUngroupedAgents();
	}

	/**
	 * Gets the agent data object associated with a particular id, which is safe
	 * for being passed to other agents without giving them too much information
	 * @param id The id to search for
	 * @return The agent object, or null if not found
	 * @see #getAgents()
	 */
	PublicAgentDataModel getAgentById(String id)
	{
		return dmodel.getAgentById(id);
	}

	/**
	 * Gets the group object associated with a particular id
	 * @param id The id to search for
	 * @return The group object, or null if not found
	 * @see #getGroups()
	 */
	PublicGroupDataModel getGroupById(String id)
	{
		return dmodel.getGroupById(id);
	}

	/**
	 * Gets the food object associated with a particular id
	 * @param id The id to search for
	 * @return The food object, or null if not found
	 * @see #availableFoods()
	 */
	Food getFoodById(UUID id)
	{
		return dmodel.getFoodById(id);
	}

	/**
	 * Finds what food types are available to hunt
	 * @return set of available food.
	 * @see #getFoodById(java.util.UUID)
	 */
	Set<Food> availableFoods()
	{
		return dmodel.availableFoods();
	}
}
