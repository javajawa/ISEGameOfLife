package ise.gameoflife.participants;

import ise.gameoflife.actions.Death;
import ise.gameoflife.actions.DistributeFood;
import ise.gameoflife.actions.GroupOrder;
import ise.gameoflife.actions.RespondToApplication;
import ise.gameoflife.environment.EnvConnector;
import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.inputs.HuntResult;
import ise.gameoflife.inputs.JoinRequest;
import ise.gameoflife.inputs.LeaveNotification;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.GroupDataInitialiser;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.tokens.GroupRegistration;
import ise.gameoflife.tokens.RegistrationResponse;
import ise.gameoflife.tokens.TurnType;
import ise.gameoflife.tokens.UnregisterRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
		return new ArrayList<String>(Arrays.asList(new String[]{"group"}));
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
		GroupRegistration request = new GroupRegistration(dm.getId());
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
		}
	}

	/**
	 * Data cleaned up to make way for a new round
	 */
	private void clearRoundData()
	{
		huntResult = new HashMap<String, Double>();
	}
	
	/**
	 * Separates agents in the group into hunting teams
	 */
	private void doTeamSelect()
	{
		Map<HuntingTeam, Food> teams = selectTeams();
		// TODO: Remove non-group members from teams
		List<String> memberList = this.dm.getMemberList();
		for (HuntingTeam team : teams.keySet())
		{
			Food toHunt = teams.get(team);
			for (String agent : team.getMembers())
			{
				if (memberList.contains(agent)){
				ec.act(new GroupOrder(toHunt, team, agent), getId(), authCode);
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
		Map<String, Double> result = distributeFood(Collections.unmodifiableMap(huntResult));
		double totalHunted = 0;
		double totalDistributed = 0;
		
		for (Double value : huntResult.values()){
			totalHunted += value;
		}		
		for (Double value : result.values()){
			totalDistributed+= value;
		}
		
		if(totalHunted != totalDistributed){
			ec.logToErrorLog(this.getId() + " of class type " + this.getClass().getCanonicalName() + " has a discrepacny: " + totalHunted + " was hunted but " + totalDistributed + " was distributed.");
		}
			
		List<String> informedAgents = new ArrayList<String>();

		for (String agent : result.keySet())
		{
			informedAgents.add(agent);
			ec.act(new DistributeFood(agent, result.get(agent)), getId(), authCode);
		}

		@SuppressWarnings("unchecked")
		List<String> uninformedAgents = new ArrayList<String>(dm.getMemberList());
		uninformedAgents.removeAll(informedAgents);

		for (String agent : uninformedAgents)
		{
			ec.act(new DistributeFood(agent, 0), getId(), authCode);
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
			// FIXME: Notify any old group of us leaving
			final JoinRequest req = (JoinRequest)input;
			boolean response = this.respondToJoinRequest(req.getAgent());
			if (response)	this.dm.addMember(req.getAgent());
			ec.act(new RespondToApplication(req.getAgent(), response), this.getId(), authCode);
			System.out.println("I, group " + getId() + ", got a join request from" + ((JoinRequest)input).getAgent());
			return;
		}

		if (input.getClass().equals(LeaveNotification.class))
		{
			final LeaveNotification in = (LeaveNotification)input;
			dm.removeMember(in.getAgent());
			this.onMemberLeave(in.getAgent(), in.getReason());
			System.out.println("I, group " + getId() + ", lost memeber " + in.getAgent() + " because of " + in.getReason());
			
			if (dm.getMemberList().isEmpty()) ec.act(new Death(), dm.getId(), authCode);
			
			return;
		}

		if (input.getClass().equals(HuntResult.class))
		{
			final HuntResult in = (HuntResult)input;
			huntResult.put(in.getAgent(), in.getNutritionValue());
			System.out.println("Agent " + in.getAgent() + " has hunted food worth" + in.getNutritionValue() + " for I, group" + getId());
			return;
		}
		ec.logToErrorLog("Group Unable to handle Input of type " + input.getClass().getCanonicalName());
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
	 * // TODO-Later: make a getDataModel() function that returns right type
	 * @return A map of all hunting teams, and the food they should be ordered to
	 * hunt
	 */
	abstract protected Map<HuntingTeam, Food> selectTeams();
	/**
	 * TODO-Later: Add stuff to say whether they hunted as ordered / what they hunted
	 * TODO-Later: Add system for groups to store non-distributed food
	 * Function used to distribute the food around after the brave
	 * hunters have returned with their winnings.
	 * Note that the total values of the distributedFood should be less than or 
	 * equal to the total amount in gains. This is checked externally, and 
	 * classes that do not behave in this way will not be acceptable for 
	 * competition simulations.
	 * @param gains The map between each member of the group, and the amount of
	 * food they have contributed to the group from hunting this term
	 * @return A map of player to the amount of food the group allocated to them
	 */
	abstract protected Map<String, Double> distributeFood(final Map<String, Double> gains);
	/**
	 * Function that is called after a member leaves the group.
	 * The member will not appear in the member list
	 * Remember to check if the leader has left and the implications of that
	 * @param playerID The id of the leaving playing
	 * @param reason The reason that the player left the group
	 */
	abstract protected void onMemberLeave(String playerID, LeaveNotification.Reasons reason);
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
