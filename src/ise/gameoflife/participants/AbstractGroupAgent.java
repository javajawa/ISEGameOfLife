package ise.gameoflife.participants;

import ise.gameoflife.models.GroupDataModel;
import ise.gameoflife.actions.RespondToApplication;
import ise.gameoflife.enviroment.EnvConnector;
import ise.gameoflife.inputs.JoinRequest;
import ise.gameoflife.inputs.LeaveNotification;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.tokens.GroupRegistration;
import ise.gameoflife.tokens.RegistrationResponse;
import ise.gameoflife.tokens.UnregisterRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.simpleframework.xml.Element;
import presage.EnvironmentConnector;
import presage.Input;
import presage.Participant;
import presage.environment.messages.ENVRegistrationResponse;

/**
 *
 * @author Benedict
 */
public abstract class AbstractGroupAgent implements Participant
{
	private static final long serialVersionUID = 1L;

	/**
	 * Flag to show whether the initialise function has been called
	 */
	private boolean beenInitalised = false;

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
	protected EnvConnector ec;
	private EnvironmentConnector tmp_ec;
	
	@Override
	public String getId()
	{
		return dm.getId();
	}

	@Override
	public ArrayList<String> getRoles()
	{
		return new ArrayList<String>(Arrays.asList(new String[]{"group"}));
	}

@Override
	public void initialise(EnvironmentConnector environmentConnector)
	{
		if (beenInitalised) throw new IllegalStateException("This object has already been initialised");
		beenInitalised = true;

		System.out.println(environmentConnector.getClass().getCanonicalName());
		tmp_ec = environmentConnector;
		dm.initialise(environmentConnector);

		// TODO: Add input handlers here
		
		onInit(environmentConnector);
	}

	@Override
	public final void onActivation()
	{
		GroupRegistration request = new GroupRegistration(dm.getId());
		ENVRegistrationResponse r = tmp_ec.register(request);
		this.authCode = r.getAuthCode();
		this.ec = ((RegistrationResponse)r).getEc();
		onActivate();
	}

	@Override
	public final void onDeActivation()
	{
		ec.deregister(new UnregisterRequest(dm.getId(), authCode));
	}

	@Override
	public void execute()
	{
		throw new UnsupportedOperationException("Not supported yet.");
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
	public final GroupDataModel getInternalDataModel()
	{
		return dm;
	}

	@Override
	public final void enqueueInput(Input input)
	{
		if (input.getClass().equals(JoinRequest.class))
		{
			boolean response = this.respondToJoinRequest(((JoinRequest)input).getAgent());
			ec.act(new RespondToApplication(this.getId(), response), this.getId(), authCode);
			if (response)	this.dm.memberList.add(((JoinRequest)input).getAgent());
			return;
		}

		if (input.getClass().equals(LeaveNotification.class))
		{
			final LeaveNotification in = (LeaveNotification)input;
			onMemberLeave(null, LeaveNotification.Reasons.Death);
			this.onMemberLeave(in.getAgent(), in.getReason());
			return;
		}

		ec.logToErrorLog("Group Unable to handle Input of type " + input.getClass().getCanonicalName());
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
			enqueueInput(in);
		}
	}

	@Override
	public void onSimulationComplete()
	{
		// TODO: Need anything here?
	}
	
	
	/**
	 * TODO: Document
	 * @param ec
	 */
	abstract protected void onInit(EnvironmentConnector ec);
	/**
	 * TODO: Document
	 */
	abstract protected void onActivate();
	
	/**
	 * TODO: Document
	 * @param playerID
	 * @return
	 */
	abstract protected boolean respondToJoinRequest(String playerID);
	/**
	 * TODO: Document
	 * @return
	 */
	abstract protected List<HuntingTeam> selectTeams();
	/**
	 * TODO: Document
	 * @param gains
	 * @return
	 */
	abstract protected Map<String, Double> distributeFood(Map<String, Double> gains);
	/**
	 * 
	 * @param playerID
	 * @param reason
	 */
	abstract protected void onMemberLeave(String playerID, LeaveNotification.Reasons reason);
}
