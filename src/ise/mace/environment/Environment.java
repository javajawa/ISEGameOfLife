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

		DistributeFoodHandler()
		{
			// Nothing to see here. Move along, citizen.
		}
	}

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

		ProposalHandler()
		{
			// Nothing to see here. Move along, citizen.
		}
	}

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

		VoteHandler()
		{
			// Nothing to see here. Move along, citizen.
		}
	}

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

		VoteResultHandler()
		{
			// Nothing to see here. Move along, citizen.
		}
	}

	private class TeamHuntEvent
	{
		private final Food food;
		private final String agent;

		TeamHuntEvent(Hunt hunt, String actorID)
		{
			this.food = dmodel.getFoodById(hunt.getFoodTypeId());
			this.agent = actorID;
		}

		String getAgent()
		{
			return agent;
		}

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
	@Element(required = false)
	private Class<? extends AbstractFreeAgentGroup> freeAgentHandler;
	private AbstractFreeAgentGroup fAGroup;
	/**
	 * Reference to the list that backs the ErrorLog view plugin.
	 */
	private final static Logger logger = Logger.getLogger("mace.Main");
	private final static Logger rootLogger = Logger.getLogger("mace");
	private Map<HuntingTeam, List<TeamHuntEvent>> storedHuntResults;

	@Deprecated
	public Environment()
	{
		super();
	}

	/**
	 *
	 * @param randomseed
	 * @param dmodel
	 * @param freeAgentHandler
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
						new EnvConnector(this));
	}

	@Override
	public EnvDataModel getDataModel()
	{
		return dmodel;
	}

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

		new PublicEnvironmentConnection(new EnvConnector(this));

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

	@Override
	protected void updatePerceptions()
	{
	}

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

	@Override
	protected void updateNetwork()
	{
	}

	@Override
	public void setTime(long cycle)
	{
		this.dmodel.setTime(cycle);
	}

	Logger getMainLogger()
	{
		return logger;
	}

	public TurnType getCurrentTurnType()
	{
		return dmodel.getTurnType();
	}

	public int getRoundsPassed()
	{
		return dmodel.getCyclesPassed();
	}

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

	String createGroup(Class<? extends AbstractGroupAgent> groupType,
					GroupDataInitialiser init)
	{
		AbstractGroupAgent g = dmodel.createGroup(groupType, init);
		g.initialise(new EnvironmentConnector(this));
		sim.addParticipant(g.getId(), g);
		sim.activateParticipant(g.getId());
		return g.getId();
	}

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
		if (sim.isParticipantActive(gid))
		{
			if (sim.getPlayer(gid) instanceof AbstractGroupAgent)
			{
				return true;
			}
		}
		return false;
	}

	public String getId()
	{
		return dmodel.getId();
	}

	Set<String> getGroups()
	{
		return dmodel.getAvailableGroups();
	}

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

	double getFoodConsumedPerAdvice()
	{
		return dmodel.getFoodConsumedPerAdvice();
	}

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

	Set<String> getAgents()
	{
		return dmodel.getAgents();
	}

	List<String> getUngroupedAgents()
	{
		return dmodel.getUngroupedAgents();
	}
}
