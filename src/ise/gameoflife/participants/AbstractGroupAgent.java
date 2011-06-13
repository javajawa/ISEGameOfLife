package ise.gameoflife.participants;

import ise.gameoflife.actions.Death;
import ise.gameoflife.actions.DistributeFood;
import ise.gameoflife.actions.GroupOrder;
import ise.gameoflife.actions.RespondToApplication;
import ise.gameoflife.actions.ApplyToGroup;
import ise.gameoflife.actions.VoteResult;
import ise.gameoflife.environment.EnvConnector;
import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.inputs.HuntResult;
import ise.gameoflife.inputs.JoinRequest;
import ise.gameoflife.inputs.LeaveNotification;
import ise.gameoflife.inputs.Proposition;
import ise.gameoflife.inputs.Vote;
import ise.gameoflife.models.GroupDataInitialiser;
import ise.gameoflife.models.HuntingTeam;
import static ise.gameoflife.models.ScaledDouble.scale;
import ise.gameoflife.tokens.GroupRegistration;
import ise.gameoflife.tokens.RegistrationResponse;
import ise.gameoflife.tokens.TurnType;
import ise.gameoflife.tokens.UnregisterRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
// TODO: Make it clear that the contract calls for a public consturctor with one argument that takes in the datamodel.

/**
 * Abstract Group agent describes form of a group. Implemented as an agent for
 * ease of compatibility with presage, since their functionalities overlap a lot
 * @author Benedict
 */
public abstract class AbstractGroupAgent implements Participant
{
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger("gameoflife.AbstractGroup");
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
	private PublicEnvironmentConnection conn;
	private EnvConnector ec;
	private EnvironmentConnector tmp_ec;
	private Map<String, Double> huntResult;
	private Map<Proposition, Integer> voteResult;

	/**
	 * 
	 * @deprecated 
	 */
	@Deprecated
	public AbstractGroupAgent()
	{
		super();
	}

	/**
	 * Initialises itself with teh group data initialiser, which contains all
	 * the necessary information to get a group started
	 * @param init 
	 */
	public AbstractGroupAgent(GroupDataInitialiser init)
	{
		this.dm = GroupDataModel.createNew(init);
	}

	/**
	 * Gets the ID of the group
	 * @return the ID of the group
	 */
	@Override
	public String getId()
	{
		return dm.getId();
	}

	/**
	 * Gets a list of agent roles within this group
	 * @return a list of the agent roles in this group
	 */
	@Override
	public ArrayList<String> getRoles()
	{
		return new ArrayList<String>(Arrays.asList(new String[]
						{
							"group"
						}));
	}

	/**
	 * Initialises the group
	 * @param environmentConnector 
	 */
	@Override
	public void initialise(EnvironmentConnector environmentConnector)
	{
		tmp_ec = environmentConnector;
		dm.initialise(environmentConnector);
	}

	/**
	 * Function called when the group is activated, but has yet to be added to the 
	 * environment
	 */
	@Override
	public final void onActivation()
	{
		GroupRegistration request = new GroupRegistration(dm.getId(),
						dm.getPublicVersion());
		ENVRegistrationResponse r = tmp_ec.register(request);
		this.authCode = r.getAuthCode();
		this.ec = ((RegistrationResponse)r).getEc();
		conn = PublicEnvironmentConnection.getInstance();
		tmp_ec = null;
		onActivate();
	}

	/**
	 * function called to say any relevant goodbyes before the group is removed
	 * from the simulation
	 */
	@Override
	public final void onDeActivation()
	{
		ec.deregister(new UnregisterRequest(dm.getId(), authCode));
	}

	/**
	 * Function called upon group execution, where it has access to data concerning
	 * other objects in the simulation
	 */
	@Override
	public void execute()
	{
		TurnType turn = ec.getCurrentTurnType();

		if (TurnType.firstTurn.equals(turn))
		{
			beforeNewRound();
			clearRoundData();
		}

		switch (turn)
		{
			case GroupSelect:
				// Nothing to do here - this is handled in enQueueMessage
				break;
			case TeamSelect:
				doTeamSelect();
				break;
			case GoHunt:
				// Nothing to do here - agents are off hunting!
				break;
			case HuntResults:
				doHandleHuntResults();
				break;
			case MakeProposals:
				// Nothing here - agents are proposing and voting
				break;
			case Voting:
				countVotes();
				break;
		}
	}

	/**
	 * Data cleaned up to make way for a new round
	 */
	private void clearRoundData()
	{
		huntResult = new HashMap<String, Double>();
		voteResult = new HashMap<Proposition, Integer>();
		dm.clearRoundData();
	}

	/**
	 * Separates agents in the group into hunting teams
	 */
	private void doTeamSelect()
	{
		List<HuntingTeam> teams = selectTeams();
		// TODO: Remove non-group members from teams
		List<String> memberList = this.dm.getMemberList();
		for (HuntingTeam team : teams)
		{
			for (String agent : team.getMembers())
			{
				if (memberList.contains(agent))
				{
					ec.act(new GroupOrder(team, agent), getId(), authCode);
				}
			}
		}
	}

	/**
	 * Once the hunters have gathered their winnings, it is processed and
	 * distributed here
	 */
	private void doHandleHuntResults()
	{
		double shared = 0;
		double taxRate = 1 - dm.getCurrentEconomicPoisition();
		for (Double value : huntResult.values())
		{
			shared += value;
		}

		shared = shared * taxRate / dm.getMemberList().size();

		Map<String, Double> result = new HashMap<String, Double>(huntResult.size());

		for (String agent : huntResult.keySet())
		{
			double value = shared + (1 - taxRate) * huntResult.get(agent);
			result.put(agent, value);
		}

		List<String> informedAgents = new ArrayList<String>();

		for (String agent : result.keySet())
		{
			informedAgents.add(agent);
			ec.act(new DistributeFood(agent, huntResult.get(agent), result.get(agent)),
							getId(), authCode);
		}

		@SuppressWarnings("unchecked")
		List<String> uninformedAgents = new ArrayList<String>(dm.getMemberList());
		uninformedAgents.removeAll(informedAgents);

		for (String agent : uninformedAgents)
		{
			ec.act(new DistributeFood(agent, 0, 0), getId(), authCode);
		}
	}

	private void countVotes()
	{
		dm.setProposals(new HashMap<Proposition, Integer>(voteResult));
		HashMap<String, Proposition> props = new HashMap<String, Proposition>();
		double movement = 0;
		double motionsPassed = 0;

		for (Proposition p : voteResult.keySet())
		{
			if (voteResult.get(p) > 0)
			{
				movement += p.getType().getMovement();
				motionsPassed++;
				logger.log(Level.FINE, "{0}''s {1} proposal was voted in (Vote={2})",
								new Object[]
								{
									p.getProposer(),
									p.getType(), voteResult.get(p)
								});
			}
			else
			{
				logger.log(Level.FINE, "{0}''s {1} proposal was not voted in (Vote={2})",
								new Object[]
								{
									p.getProposer(),
									p.getType(), voteResult.get(p)
								});
			}
			// TODO: Store each proposition and result in history?
			props.put(p.getProposer(), p);
		}
                
		// Calculate the groups new position
		double change = dm.getCurrentEconomicPoisition();
		if (motionsPassed > 0)
		{  
			dm.setEconomicPosition(scale(change, movement / motionsPassed, 0.1));
			change = dm.getCurrentEconomicPoisition() - change;
		}
		else
		{
			change = 0;
		}
		// Inform each agent of how their vote went, and the overall group movement
		for (String agent : props.keySet())
                {
			Proposition p = props.get(agent);
			ec.act(new VoteResult(p, voteResult.get(p), change), getId(), authCode);
		}
	}

	/**
	 * Sets the number of cycles passed
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

	/**
	 * Returns the externally visible elements of the group agent
	 * @return The externally visible elements of the group agent
	 */
	public final PublicGroupDataModel getDataModel()
	{
		return dm.getPublicVersion();
	}

	protected final void setEconomicPosition(double newPosition)
	{
		this.dm.setEconomicPosition(newPosition);
	}

	/**
	 * This function puts the inputs into a queue to be processed at the end of
	 * the cycle
	 * @param input 
	 */
	@Override
	public final void enqueueInput(Input input)
	{
		if (input.getClass().equals(JoinRequest.class))
		{
			final JoinRequest req = (JoinRequest)input;
			boolean response = this.respondToJoinRequest(req.getAgent());
			if (response) this.dm.addMember(req.getAgent());
			ec.act(new RespondToApplication(req.getAgent(), response), this.getId(),
							authCode);
			logger.log(Level.FINE, "{0} got a join request from {1}", new Object[]
							{
								dm.getName(),
								ec.nameof(((JoinRequest)input).getAgent())
							});
			return;
		}

		if (input.getClass().equals(LeaveNotification.class))
		{    
			final LeaveNotification in = (LeaveNotification)input;
			dm.removeMember(in.getAgent());
			this.onMemberLeave(in.getAgent(), in.getReason());
			logger.log(Level.FINE, "{0} lost memeber {1} because of {2}", new Object[]
							{
								dm.getName(),
								ec.nameof(in.getAgent()), in.getReason()

							});                        

			if (dm.getMemberList().isEmpty())
				ec.act(new Death(), dm.getId(), authCode);

			return;
		}

		if (input.getClass().equals(HuntResult.class))
		{
			final HuntResult in = (HuntResult)input;
			huntResult.put(in.getAgent(), in.getFoodHunted());
			logger.log(Level.FINE, "Agent {0} has hunted food worth {1} for {2}",
							new Object[]
							{
								ec.nameof(in.getAgent()),
								in.getFoodHunted(), dm.getName()
							});
			return;
		}

		if (input.getClass().equals(Vote.class))
		{
			final Vote v = (Vote)input;
			if (!voteResult.containsKey(v.getProposition()))
			{
				if (!v.getProposition().getOwnerGroup().equals(getId())) return;
				voteResult.put(v.getProposition(), 0);
			}
			voteResult.put(v.getProposition(),
							voteResult.get(v.getProposition()) + v.getVote().getValue());
			logger.log(Level.FINE,
							"Agent {0} has voted {1} on {2} by {3} as a member of {4}",
							new Object[]
							{
								ec.nameof(v.getAgent()),
								v.getVote(), v.getProposition().getType(),
								ec.nameof(v.getProposition().getProposer()), dm.getName()
							});
			return;
		}

		logger.log(Level.SEVERE, "Group Unable to handle Input of type {0}",
						input.getClass().getCanonicalName());
	}

	/**
	 * Same as above, but used to handle ArrayLists
	 * @param input
	 */
	@Override
	public final void enqueueInput(ArrayList<Input> input)
	{
		for (Input in : input) enqueueInput(in);
	}

	/**
	 * Function called at the very end of the simulation
	 */
	@Override
	public void onSimulationComplete()
	{
		// Nothing to see here. Move along, citizen!
	}

	/**
	 * Called when the group has been activated, and when both the {@link 
	 * GroupDataModel data model} and the {@link PublicEnvironmentConnection
	 * environment connection} have been initialised
	 * @see #getDataModel() 
	 * @see #getConn() 
	 */
	abstract protected void onActivate();

	/**
	 * Allows the group to process and respond to a join request
	 * @param playerID The player wishing to join
	 * @return Whether the group wishes to let them join
	 */
	abstract protected boolean respondToJoinRequest(String playerID);

	/**
	 * Procedure to assign members to different teams, and get them to hunt food.
	 * The list of different foods can be found using the {@link #getConn() 
	 * environment connection}, and the list of current group members can be 
	 * found in the {@link GroupDataModel dataModel} which can be accessed with 
	 * {@link #getInternalDataModel() }
	 * @return A map of all hunting teams, and the food they should be ordered to
	 * hunt
	 */
	abstract protected List<HuntingTeam> selectTeams();

	/**
	 * Function that is called after a member leaves the group.
	 * The member will not appear in the member list
	 * Remember to check if the leader has left and the implications of that
	 * @param playerID The id of the leaving playing
	 * @param reason The reason that the player left the group
	 */
	abstract protected void onMemberLeave(String playerID,
					LeaveNotification.Reasons reason);

	/**
	 * Here you implement any code concerning data storage about the events
	 * of this round before it is all deleted for a new round to begin.
	 * N.B: a "round" occurs after all {@link TurnType turn types} have been 
	 * iterated through. This
	 * is to avoid confusion between "cycles", "turn" and "time". 
	 * Alternatively, use of the unit "Harcourt" may also be used. 
	 * 1 Round = 1 Harcourt
	 */
	abstract protected void beforeNewRound();

	/**
	 * @return the conn
	 */
	protected PublicEnvironmentConnection getConn()
	{
		return conn;
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
}
