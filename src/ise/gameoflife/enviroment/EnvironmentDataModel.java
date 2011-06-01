package ise.gameoflife.enviroment;

import ise.gameoflife.models.AgentDataModel;
import ise.gameoflife.models.PublicAgentDataModel;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.GroupDataModel;
import ise.gameoflife.tokens.RegistrationRequest;
import ise.gameoflife.tokens.TurnType;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import presage.environment.AEnvDataModel;

/**
 *
 * @author Benedict Harcourt
 */
public class EnvironmentDataModel extends AEnvDataModel
{

	public final static long serialVersionUID = 1L;
	/**
	 * A sorted list/map of all the state of all players in the game
	 */
	@ElementMap(keyType = String.class, valueType = AgentDataModel.class)
	public TreeMap<String, PublicAgentDataModel> agents = new TreeMap<String, PublicAgentDataModel>();
	/**
	 * List of all the available food types in the environment
	 */
	@ElementMap
	private HashMap<String, Food> availableFoodTypes;
	/**
	 * List of all the groups in the environment
	 */
//	@ElementMap
//	private HashMap<UUID, GroupDataModel> agentGroups;
	@Element
	private TurnType turn;

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
		return null; // agentGroups.get(id);
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

}
