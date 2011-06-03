package ise.gameoflife.participants;

import ise.gameoflife.History;
import ise.gameoflife.UnmodifableHistory;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.HuntingTeam;
import java.util.HashMap;
import org.simpleframework.xml.Element;
import presage.abstractparticipant.APlayerDataModel;

/**
 *
 * @author Christopher Fonseka
 */
class AgentDataModel extends APlayerDataModel
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
	 * Stores the amount of food an agent has consumed this turn
	 */
	@Element
	private double foodConsumedThisTurn;

	/**
	 * Field that holds the id of {@link #group}
	 * Will be null if {@link #group} is null
	 */
	@Element(required=false)
	private String groupId;
	
	@Element
	private History<HashMap<String, Double>> trust;

	@Element
	private History<Double> happinessHistory;
	
	@Element
	private History<Double> loyaltyHistory;

	@Element
	private Double econimicBelief;
	
	private History<Food> lastHunted = null;
	private History<HuntingTeam> huntingTeam = null;
	private Food lastOrderReceived = null;

	/**
	 * Serialised constructors in the package are implemented as deprecated to
	 * stop warnings being shown
	 * @deprecated Due to serialisation conflicts
	 */
	@Deprecated
	 AgentDataModel()
	{
		super();
	}
	
	/**
	 * Creates a new agent with a given amount of initial food 
	 * and the amount of food they consume per turn
	 * @param myId 
	 * @param roles 
	 * @param playerClass
	 * @param randomseed 
	 * @param foodInPossesion Initial amount of food
	 * @param foodConsumption Food consumed per turn
	 */
	@SuppressWarnings("deprecation")
	 AgentDataModel(String myId, String roles, String playerClass, long randomseed, double foodInPossesion, double foodConsumption)
	{
		super(myId, roles, playerClass, randomseed);
		this.foodInPossesion = foodInPossesion;
		this.foodConsumption = foodConsumption;
	}

	/**
	 * @return the foodInPossesion
	 */
	public double getFoodInPossesion()
	{
		return foodInPossesion;
	}

	/**
	 * Returns the amount of food consumed per turn by this Agent
	 * @return
	 */
	public double getFoodConsumption()
	{
		return foodConsumption;
	}
	/**
	 * Returns the amount of total food consumed this turn
	 * @return 
	 */
	public double getFoodConsumedThisTurn()
	{
		return foodConsumedThisTurn;
	}
	/**
	 * Updates the food consumed so far this turn
	 * @param food The amount to be consumed
	 */
	public void setFoodConsumedThisTurn(double food)
	{
		this.foodConsumedThisTurn += food;
	}
	/**
	 * @param consumed reduces the foodInPossesion by a given amount
	 */
	public void foodConsumed(double consumed)
	{
		this.foodInPossesion -= consumed;
	}

	/**
	 * @param acquired increases food inventory by given amount
	 */
	public void foodAquired(double acquired)
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

	/**
	 * Returns the group ID of the agent
	 * @return 
	 */
	public String getGroupId()
	{
		return groupId;
	}

	/**
	 * assigns the agent to a specified group
	 * @param gid 
	 */
	public void setGroup(String gid)
	{
		this.groupId = gid;
	}

	@Override
	public void onInitialise()
	{
		happinessHistory = new History<Double>(50);
	}

	/**
	 * @return The food the agent decided to hunt on the previous turn
	 */
	public Food getLastHunted()
	{
		return lastHunted.getValue();
	}

	/**
	 * @return The food the agent decided to hunt on the previous turn
	 */
	public void setLastHunted(Food lastFood)
	{
		lastHunted.setValue(lastFood);
	}

	public UnmodifableHistory<Food> getHuntingHistory()
	{
		return lastHunted.getUnmodifableHistory();
	}

	/**
	 * @return which hunting pair this agent belongs to
	 */
	public HuntingTeam getHuntingTeam() {
		return huntingTeam.getValue();
	}

	/**
	 * @return which hunting pair this agent belongs to
	 */
	public void setHuntingTeam(HuntingTeam team) {
		huntingTeam.setValue(team);
	}

	public UnmodifableHistory<HuntingTeam> getTeamHistory()
	{
		return huntingTeam.getUnmodifableHistory();
	}

	/**
	 * The food that this agent has been ordered to hunt with it's team in this
	 * round
	 * @return Food that was ordered 
	 */
	public Food getOrder()
	{
		return lastOrderReceived;
	}

	/**
	 * The food that this agent has been ordered to hunt with it's team in this
	 * round
	 * @return Food that was ordered 
	 */
	public void setOrder(Food newFood)
	{
		lastOrderReceived = newFood;
	}

	public double getCurrentHappiness()
	{
		return happinessHistory.getValue();
	}

	public Double setCurrentHappiness(Double newHappiness)
	{
		return happinessHistory.setValue(newHappiness);
	}

	public UnmodifableHistory<Double> getHappinessHistory()
	{
		return happinessHistory.getUnmodifableHistory();
	}

	public double getCurrentLoyalty()
	{
		return loyaltyHistory.getValue();
	}

	public double setLoyaltyHappiness(double newLoyalty)
	{
		return loyaltyHistory.setValue(newLoyalty);
	}

	public UnmodifableHistory<Double> getLoyaltyHistory()
	{
		return loyaltyHistory.getUnmodifableHistory();
	}
	
	public History<HashMap<String, Double>> getTrust()
	{
		return trust;
	}

	public void setTrust(String s, Double t)
	{
		this.trust.getValue().put(s, t);
	}

	public Double getEconimicBelief()
	{
		return econimicBelief;
	}

	public void setEconimicBelief(Double econimicBelief)
	{
		this.econimicBelief = econimicBelief;
	}
	
	
}
