package ise.gameoflife;

import ise.gameoflife.enviroment.EnvConnector;
import ise.gameoflife.participants.GroupDataModel;
import java.util.UUID;
import org.simpleframework.xml.Element;
import presage.abstractparticipant.APlayerDataModel;

/**
 *
 * @author Christopher Fonseka
 */
public class AgentDataModel extends APlayerDataModel
{
	private static final long serialVersionUID = 1L;

	/**
	 * Stores amount of food owned by agent
	 */
	@Element
	private double foodInPossesion;
	/**
	 * Stores the amount of the food that the Agent must consume in order to
	 * survive each turn
	 */
	@Element
	private double foodConsumption;

	/**
	 * The group that this agent currently belongs to.
	 * It will be null if the agent does not belong to a group
	 */
	private GroupDataModel group;
	/**
	 * Field that holds the id of {@link #group}
	 * Will be null if {@link #group} is null
	 */
	@Element(required=false)
	private UUID groupId;

	/**
	 * Serialised constructors in the package are implemented as deprecated to
	 * stop warnings being shown
	 * @deprecated Due to serialisation conflicts
	 */
	@Deprecated
	public AgentDataModel()
	{
		super();
	}
	
	/**
	 * Creates a new agent, with a given amount of initial food, 
	 * and the amount of food they consume per turn
	 * @param foodInPossesion Initial amount of food
	 * @param foodConsumption Food consumed per turn
	 */
	@SuppressWarnings("deprecation")
	public AgentDataModel(String myId, String roles, String playerClass, long randomseed, double foodInPossesion, double foodConsumption)
	{
		super(myId, roles, playerClass, randomseed);
		this.foodInPossesion = foodInPossesion;
		this.foodConsumption = foodConsumption;
	}

	/**
	 * Code being called after all XML-reading
	 * @param environmentConnector The connector to the environment we are in
	 */
	public void initialise(EnvConnector environmentConnector)
	{
		super.initialise(environmentConnector);
		this.group = environmentConnector.getGroupById(this.groupId);
	}

	/**
	 * @return the foodInPossesion
	 */
	public double getFoodInPossesion()
	{
		return foodInPossesion;
	}

	/**
	 * Sets a new value for the foodInPossesion
	 * @param foodInPossesion the foodInPossesion to set
	 * @deprecated Use {@link #foodConsumed(int)} and {@link #foodAquired(int)}
	 */
	@Deprecated
	public void setFoodInPossesion(int foodInPossesion)
	{
		this.foodInPossesion = foodInPossesion;
	}

	/**
	 * Returns the amount of food consumed per turn by this Agent
	 * @return The amount of food consumed per turn by this Agent
	 */
	public double getFoodConsumption()
	{
		return foodConsumption;
	}

	/**
	 * @param consumed reduces the foodInPossesion by a given amount
	 */
	public void foodConsumed(int consumed)
	{
		this.foodInPossesion -= consumed;
	}

	/**
	 * @param acquired increases food inventory by given amount
	 */
	public void foodAquired(int acquired)
	{
		this.foodInPossesion += acquired;
	}

	/**
	 * Get a re-distribution safe copy of this object. The returned object is
	 * backed by this one, so their is no need to keep calling this to receive
	 * updated data.
	 * @return
	 */
	public PublicAgentDataModel getPublicVersion()
	{
		return new PublicAgentDataModel(this);
	}

	public UUID getGroupId()
	{
		return groupId;
	}

	@Override
	public void onInitialise()
	{
		// TODO: Do things here :)
	}
}
