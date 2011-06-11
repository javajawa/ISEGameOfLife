package ise.gameoflife.participants;

import ise.gameoflife.actions.ApplyToGroup;
import ise.gameoflife.actions.Death;
import ise.gameoflife.actions.Hunt;
import ise.gameoflife.actions.Proposal;
import ise.gameoflife.actions.Proposal.ProposalType;
import ise.gameoflife.actions.Vote;
import ise.gameoflife.environment.EnvConnector;
import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.inputs.ApplicationResponse;
import ise.gameoflife.inputs.ConsumeFood;
import ise.gameoflife.inputs.GroupInvite;
import ise.gameoflife.inputs.HuntOrder;
import ise.gameoflife.inputs.HuntResult;
import ise.gameoflife.inputs.Proposition;
import ise.gameoflife.inputs.VoteResult;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.tokens.AgentType;
import ise.gameoflife.tokens.RegistrationRequest;
import ise.gameoflife.tokens.RegistrationResponse;
import ise.gameoflife.tokens.TurnType;
import ise.gameoflife.tokens.UnregisterRequest;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * All agents which extend this class should be in the agents package
 * @author Benedict Harcourt
 * @see ise.gameoflife.agents 
 */
abstract public class AbstractAgent implements Participant
{

	/**
	 * @return the conn
	 */
	protected PublicEnvironmentConnection getConn()
	{
		return conn;
	}

	private class ConsumeFoodHandler implements InputHandler
	{
		@Override
		public boolean canHandle(Input input){
			return (input instanceof ConsumeFood);
		}

		@Override
		public void handle(Input input)
		{
			logger.log(Level.FINE, "I, agent {0}, consumed {1} units of food", new Object[]{dm.getName(),
							dm.getFoodConsumedThisTurn()});
			dm.foodConsumed(dm.getFoodConsumedThisTurn());
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
			dm.foodAquired(in.getFoodReceived());
			logger.log(Level.FINE, "I, agent {0}, recieved {1} units of food", new Object[]{dm.getName(),
							in.getFoodReceived()});

			dm.alterHappiness(updateHappinessAfterHunt(in.getFoodHunted(), in.getFoodReceived()));
			dm.alterLoyalty(updateLoyaltyAfterHunt(in.getFoodHunted(), in.getFoodReceived()));
			Map<String,Integer> t = updateTrustAfterHunt(in.getFoodHunted(), in.getFoodReceived());
			if (t==null) return;
			for (String agent : t.keySet())
			{
				dm.alterTrust(agent, t.get(agent));
			}
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
			dm.setHuntingTeam(in.getTeam());
			logger.log(Level.FINE, "I, Agent {0} have been told to hunt with Team {1}", new Object[]{dm.getName(),
							in.getTeam().hashCode()});
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
				dm.clearLoyalty();
			}
			groupApplicationResponse(in.wasAccepted());	
			logger.log(Level.FINE, "I, agent {0} was {1}accepted into group {2}", new Object[]{dm.getName(),
							in.wasAccepted() ? "" : "not ", ec.nameof(in.getGroup())});
		}
		
	}

	private class PropositionHandler implements InputHandler
	{

		@Override
		public boolean canHandle(Input input)
		{
			return (input instanceof ise.gameoflife.inputs.Proposition);
		}

		@Override
		public void handle(Input input)
		{
			final ise.gameoflife.inputs.Proposition in = (ise.gameoflife.inputs.Proposition)input;
			
			Vote.VoteType v = castVote(in);
			ec.act(new Vote(in, v), getId(), authCode);
			
			logger.log(Level.FINE, "I, agent {0} voted {1} in {2}'s vote of {3}", new Object[]{dm.getName(),
							v, ec.nameof(in.getProposer()), in.getType()});
		}
		
	}

	private class VoteResultHandler implements InputHandler
	{

		@Override
		public boolean canHandle(Input input)
		{
			return (input instanceof VoteResult);
		}

		@Override
		public void handle(Input input)
		{
			final VoteResult in = (VoteResult)input;
			logger.log(Level.FINE, "I, agent {0} got {1} for my {2} proposal.", new Object[]{dm.getName(),
							in.getVotes(), in.getProposition().getType()});
			dm.alterHappiness(updateHappinessAfterVotes(in.getProposition(), in.getVotes(), in.getOverallMovement()));
			dm.alterLoyalty(updateLoyaltyAfterVotes(in.getProposition(), in.getVotes(), in.getOverallMovement()));
			dm.setSocialBelief(updateSocialBeliefAfterVotes(in.getProposition(), in.getVotes(), in.getOverallMovement()));
			dm.setEconomicBelief(updateEconomicBeliefAfterVotes(in.getProposition(), in.getVotes(), in.getOverallMovement()));
			Map<String,Integer> t = updateTrustAfterVotes(in.getProposition(), in.getVotes(), in.getOverallMovement());
			if (t==null) return;
			for (String agent : t.keySet())
			{
				dm.alterTrust(agent, t.get(agent));
			}
		}
	}

	private class InvitationHandler implements InputHandler
	{

		@Override
		public boolean canHandle(Input input)
		{
			return (input instanceof GroupInvite);
		}

		@Override
		public void handle(Input input)
		{
			final GroupInvite in = (GroupInvite)input;

			onInvite(in.getGroup());
			logger.log(Level.FINE, "I, agent {0} was inivited to the new {1} group.", new Object[]{dm.getName(),
							ec.nameof(in.getGroup())});
		}

	}

	private final static Logger logger = Logger.getLogger("gameoflife.AbstractAgent");
	public final static String leaveGroup = UUID.randomUUID().toString();
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
	private PublicEnvironmentConnection conn;
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
		this(myroles, randomseed, initialFood, consumption, null, 0, 0);
	}
	/**
	 * Creates a new agent with the two primary properties
	 * @param myroles The roles that this agent can perform
	 * @param randomseed the random seed for this agent
	 * @param initialFood The initial amount of food
	 * @param consumption The amount consumed per turn
	 * @param type The logic type of this agent
         * @param socialBelief The social belief of this agent
         * @param economicBelief The economic belief of this agent
	 */
	public AbstractAgent(String myroles, long randomseed, double initialFood, double consumption, AgentType type,
                            double socialBelief, double economicBelief)
	{
		UUID myid = UUID.randomUUID();
		this.dm = new AgentDataModel(myid.toString(), myroles, this.getClass(), randomseed, initialFood, consumption, type, socialBelief, economicBelief);
	}

	/**
	 * Returns the String representation of the Agent's UUID, which is used to
	 * identify the agent in all interactions.
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
		tmp_ec = environmentConnector;
		dm.initialise(environmentConnector);

		this.handlers.add(new ConsumeFoodHandler());
		this.handlers.add(new HuntResultHandler());
		this.handlers.add(new HuntOrderHandler());
		this.handlers.add(new ApplicationResponseHandler());
		this.handlers.add(new PropositionHandler());
		this.handlers.add(new VoteResultHandler());
		this.handlers.add(new InvitationHandler());
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
		if (this.dm.getFoodInPossesion() < 0)
		{
			logger.log(Level.FINE, "I, agent, {0}, am starving to death!", dm.getName());
			ec.act(new Death(), dm.getId(), authCode);
		}

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
			case MakeProposals:
				doMakeProposal();
				break;
			case Voting:
				// This is the group's move
				break;
			default:
				throw new IllegalStateException("Turn was not recognised");
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
		logger.log(Level.SEVERE, "AbstractAgent can not handle inputs of type {0}", i.getClass().getCanonicalName());
	}

	private void clearRoundData()
	{
		dm.newHistoryEntry();
	}

	private void doGroupSelect()
	{
		String gid = chooseGroup();
		if (gid == null ? true : gid.equals(dm.getGroupId())) return;
		if (gid.equals(leaveGroup))
		{
			ec.act(new ApplyToGroup(gid), getId(), authCode);
			this.dm.setGroup(null);
			return;
		}
		if (getConn().isGroupId(gid)) ec.act(new ApplyToGroup(gid), getId(), authCode);
	}
        
	private void doHuntTurn()
	{
		Food toHunt = chooseFood();

		if (toHunt == null)
		{
			logger.log(Level.WARNING, "Agent {0} did not pick a food to hunt", dm.getName());
		}
		else
		{
			ec.act(new Hunt(toHunt), this.getId(), authCode);
		}
		dm.setLastHunted(toHunt);
	}

	private void doMakeProposal()
	{
		if (dm.getGroupId() == null) return;
		ProposalType t = makeProposal();
		ec.act(new Proposal(t, dm.getGroupId()), getId(), authCode);
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
	 * Makes this agent seek advice from another agent
	 * @param agent The agent to seek advice from
	 * @return The food they advice you hunt, based on your team etc.
	 */
	protected final Food seekAvice(String agent)
	{
		dm.increaseFoodConsumedThisTurn(ec.getFoodConsumedPerAdvice());
		return ec.seekAdvice(getId(), authCode, agent, dm.getHuntingTeam());
	}

	/**
	 * Wrapper function for advice giving
	 * @param agent The agent who is asking for advice
	 * @param agentsTeam The team are part of
	 * @return What food you want to tell them to hunt
	 */
	public Food advise(String agent, HuntingTeam agentsTeam)
	{
		Food advice = giveAdvice(agent, agentsTeam);
		dm.gaveAdvice(agent, advice);
		return advice;
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

	/**
	 * Gets the public data model of this agent
	 * @return The public data model of this agent
	 */
	public final PublicAgentDataModel getDataModel()
	{
		return dm.getPublicVersion();
	}

	@Override
	public final void enqueueInput(Input input)
	{
		if (input.getClass().equals(Proposition.class))
		{
			handleInput(input);
			return;
		}
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
	@SuppressWarnings("NoopMethodInAbstractClass")
	public final void onSimulationComplete()
	{
		// Nothing to see here. Move along, citizen!
	}

	/**
	 * Get the next random number in the sequence as a double uniformly
	 * distributed between 0 and 1
	 * @return Next random number
	 */
	protected final double uniformRand()
	{
		return this.dm.random.nextDouble();
	}

	/**
	 * Get the next random number in the sequence as a double uniformly
	 * distributed between 0 and 1
	 * @return Next random number
	 */
	protected final long uniformRandLong()
	{
		return this.dm.random.nextLong();
	}

	protected final boolean uniformRandBoolean()
	{
		return this.dm.random.nextBoolean();
	}

	/**
	 * Called when the agent has been activated, and when both the {@link 
	 * PublicAgentDataModel data model} and the {@link PublicEnvironmentConnection
	 * environment connection} have been initialised
	 * @see #getDataModel() 
	 * @see #getConn()
	 */
	abstract protected void onActivate();
	/**
	 * Used to implement any code necessary before all properties of the current
	 * round are deleted to make way for a newer, fresher, more flexible
	 * round to begin.
	 * The reset fields are the last thing we hunted, the last order we received,
	 * and the last team we were in. All of these
	 */
	abstract protected void beforeNewRound();
	/**
	 * Magic heuristic to select which Group the agent wishes to be a part of
	 * for the next round. The list of groups can be obtained through the 
	 * connector {@link #conn this.conn}, as can functions to create a new group.
	 * IF the agent wishes to remain in the same group, it can either return the
	 * current group id, or null.
	 * If the agent wished to leave it's current group, and rejoin the pool of 
	 * free agents, it should return the constant value at {@link 
	 * AbstractAgent#leaveGroup}. The value is designed not to collide with any
	 * group name.
	 * @return The is of the group we should try to join
	 */
	abstract protected String chooseGroup();
	/**
	 * Called once the environment has issued a response to the application
	 * to join a group. Also states whether or not the application has been
	 * successful. The group will already be filled into the groupId field of the
	 * {@link PublicAgentDataModel data model}, which can be obtained through
	 * the {@link #getDataModel() getDataModel()} function
	 * @param accepted Whether you were accepted into the group
	 */
	abstract protected void groupApplicationResponse(boolean accepted);
	/**
	 * Function called to get the Agent to select what kind of food it would like
	 * to hunt. It should use all the other information it has received to inform
	 * this decision.
	 * You can get the types of food from {@link #getConn() this.getConn()}, 
	 * which has various functions related to determining food properties.
	 * If the agent is a member of a group, the food they have been ordered to
	 * hunt and their current group can be found in the {@link 
	 * PublicAgentDataModel data model}, which is accessed this {@link
	 * #getDataModel() this.getDataModel()}
	 * @return The type of food they have decided to hunt
	 */
	abstract protected Food chooseFood();
	/**
	 * Allows the agent to propose a shift of the group's economic status
	 * along the axis. The exact direction and magnitude of the shift 
	 * depends on how the function is defined.
	 * @return The direction and magnitude of the proposed move
	 */
	abstract protected ProposalType makeProposal();
	/**
	 * Allows the agent to vote on a proposition that has been put before the
	 * group
	 * @param p The proposition to vote on
	 * @return The agent's decision
	 */
	abstract protected Vote.VoteType castVote(Proposition p);
	/**
	 * Allows other agents to ask this agent what to hunt given their team
	 * @param agent The agent who is asking for advice
	 * @param agentsTeam The team are part of
	 * @return What food you want to tell them to hunt
	 */
	abstract protected Food giveAdvice(String agent, HuntingTeam agentsTeam);
	/**
	 * Allows the happiness value of the agent to be updated after the completion 
	 * of the hunt
	 * @param foodHunted The amount of food the agent brought back to the group
	 * @param foodReceived The amount of food that the agent was allowed to keep
	 * @return The new happiness value of the agent, scaled between 0 and 1
	 */
	abstract protected int updateHappinessAfterHunt(double foodHunted,	double foodReceived);
	/**
	 * Allows the loyalty value of the agent to be updated after the completion of
	 * the hunt
	 * @param foodHunted The amount of food the agent brought back to the group
	 * @param foodReceived THe amount of food that the agent was allowed to keep
	 * @return The new loyalty value of the agent, scaled between 0 and 1
	 */
	abstract protected int updateLoyaltyAfterHunt(double foodHunted, double foodReceived);
	/**
	 * Allows the trust values of the agent to be updated after the completion of
	 * the hunt
	 * @param foodHunted The amount of food the agent brought back to the group
	 * @param foodReceived THe amount of food that the agent was allowed to keep
	 * @return A mapping between agent ids, and the new value for their trust
	 * rating. Agent ids which are to in the map will not be updated. The trust 
	 * values should be scaled between 0 and 1.
	 */
	abstract protected Map<String, Integer> updateTrustAfterHunt(double foodHunted, double foodReceived);
	abstract protected int updateLoyaltyAfterVotes(Proposition proposition, int votes,	double overallMovement);
	abstract protected int updateHappinessAfterVotes(Proposition proposition, int votes,	double overallMovement);
	abstract protected int updateSocialBeliefAfterVotes(Proposition proposition, int votes,	double overallMovement);
	abstract protected int updateEconomicBeliefAfterVotes(Proposition proposition, int votes,	double overallMovement);
	abstract protected Map<String, Integer> updateTrustAfterVotes(Proposition proposition,	int votes, double overallMovement);
	/**
	 * Notifies you when you have been invited to a new group.
	 * It is up to agent implementations to supply a place in their own class to
	 * store this information. They will be able to join the group when the
	 * chooseGroup function is called.
	 * @param group The group they've been invited to
	 */
	abstract protected void onInvite(String group);
}
