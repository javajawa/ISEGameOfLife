package ise.gameoflife.enviroment;

import ise.gameoflife.models.AgentDataModel;
import ise.gameoflife.models.PublicAgentDataModel;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.GroupDataModel;
import ise.gameoflife.participants.AbstractGroupAgent;
import ise.gameoflife.tokens.RegistrationRequest;
import ise.gameoflife.tokens.TurnType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import presage.environment.AEnvDataModel;

/**
 * TODO: Maybe some documentation here sometime
 * @author Benedict Harcourt
 */
public class EnvironmentDataModel extends AEnvDataModel
{
	private final static long serialVersionUID = 1L;

	/**
	 * A sorted list/map of all the state of all players in the game
	 */
	@ElementMap(keyType = String.class, valueType = AgentDataModel.class)
	private TreeMap<String, PublicAgentDataModel> agents = new TreeMap<String, PublicAgentDataModel>();
	/**
	 * List of all the available food types in the environment
	 */
	@ElementMap
	private HashMap<String, Food> availableFoodTypes;
	/**
	 * List of all the groups in the environment
	 */
	@ElementMap
	private HashMap<String, GroupDataModel> agentGroups;
	/**
	 * 
	 */
	@ElementList(type=Class.class)
	private ArrayList<Class<? extends AbstractGroupAgent>> allowedGroupTypes;
	/**
	 * 
	 */
	@Element
	private TurnType turn;
	@Element
	private int cycles;

	/**
	 * Serialisable no-arg constructor, do not use
	 * @deprecated
	 */
	@Deprecated
	public EnvironmentDataModel()
	{
		super();
	}

	public EnvironmentDataModel(String environmentname, HashMap<String, Food> availableFoodTypes)
	{
		super(environmentname, "ISE Game of Life Enviroment Data Model", 0);
		this.availableFoodTypes = availableFoodTypes;
		this.turn = TurnType.firstTurn;
		this.agentGroups = new HashMap<String, GroupDataModel>();
		this.allowedGroupTypes = new ArrayList<Class<? extends AbstractGroupAgent>>();
		this.cycles = 0;
	}

	public Set<Food> availableFoods()
	{
		return Collections.unmodifiableSet(new HashSet<Food>(availableFoodTypes.values()));
	}

	public Food getFoodById(UUID id)
	{
		return availableFoodTypes.get(id.toString());
	}

	public GroupDataModel getGroupById(UUID id)
	{
		return agentGroups.get(id.toString());
	}

	public PublicAgentDataModel getAgentById(String id)
	{
		return agents.get(id);
	}

	public boolean removeParticipant(String id)
	{
		return (agents.remove(id)!=null);
	}

	public boolean registerParticipant(RegistrationRequest id)
	{
		agents.put(id.getParticipantID(), id.getModel());
		return true;
	}

	public TurnType getTurnType()
	{
		return turn;
	}

	public List<Class<? extends AbstractGroupAgent>> getAllowedGroupTypes()
	{
		return Collections.unmodifiableList(allowedGroupTypes);
	}

	@Override
	public void setTime(long time)
	{
		super.setTime(time);
		TurnType[] t = TurnType.values();

		int next = turn.ordinal() + 1;
		if (next == t.length)
		{
			next = 0;
			cycles ++;
		}

		turn = t[next];
	}

	public int getCyclesPassed()
	{
		return cycles;
	}

}
