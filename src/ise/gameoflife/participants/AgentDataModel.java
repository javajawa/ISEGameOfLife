package ise.gameoflife.participants;

import ise.gameoflife.models.History;
import ise.gameoflife.models.UnmodifiableHistory;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.models.NameGenerator;
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

	@Element
	private String name;

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
	private History<Double> foodConsumedPerTurnHistory;

	/**
	 * Field that holds the id of {@link #group}
	 * Will be null if {@link #group} is null
	 */
	@Element(required=false)
	private String groupId;
	
	@Element
	private HashMap<String,History<Double>> trust;

	@Element
	private History<Double> happinessHistory;
	
	@Element
	private History<Double> loyaltyHistory;

	@Element
	private double economicBelief;
	
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
		this.name = NameGenerator.getName();
		onInitialise();
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
	 * Returns the history of food consumed per turn
	 * @return The history of food consumption per turn
	 */
	UnmodifiableHistory<Double> getFoodConsumedPerTurnHistory()
	{
		return foodConsumedPerTurnHistory.getUnmodifableHistory();
	}
	/**
	 * Gets the amount food consumed so far this turn
	 * @return The amount of food consumed so far this turn
	 */
	public double getFoodConsumedThisTurn()
	{
		return foodConsumedPerTurnHistory.getValue();
	}
	/**
	 * Updates the food consumed so far this turn
	 * @param food The amount to be consumed
	 */
	@SuppressWarnings("AssignmentToMethodParameter")
	public void increaseFoodConsumedThisTurn(double food)
	{
		food += foodConsumedPerTurnHistory.getValue();
		foodConsumedPerTurnHistory.setValue(food);
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
	public final void onInitialise()
	{
		foodConsumedPerTurnHistory = new History<Double>(50);
		happinessHistory = new History<Double>(50);
		huntingTeam = new History<HuntingTeam>(50);
		loyaltyHistory = new History<Double>(50);
		lastHunted = new History<Food>(50);
		trust = new HashMap<String, History<Double>>();
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

	public UnmodifiableHistory<Food> getHuntingHistory()
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

	public UnmodifiableHistory<HuntingTeam> getTeamHistory()
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

	public UnmodifiableHistory<Double> getHappinessHistory()
	{
		return happinessHistory.getUnmodifableHistory();
	}

	public double getCurrentLoyalty()
	{
		return loyaltyHistory.getValue();
	}

	public Double setCurrentLoyalty(double newLoyalty)
	{
		return loyaltyHistory.setValue(newLoyalty);
	}

	public UnmodifiableHistory<Double> getLoyaltyHistory()
	{
		return loyaltyHistory.getUnmodifableHistory();
	}
	
	public History<Double> getTrustHistory(String player)
	{
		if (trust.containsKey(player))
		{
			return trust.get(player).getUnmodifableHistory();
		}
		History<Double> t = new History<Double>(50);
		t.newEntry(null);
		trust.put(player, t);
		return t.getUnmodifableHistory();
	}

	public Double getTrust(String player)
	{
		Double t = getTrustHistory(player).getValue();
		if (t==null) return null;
		return new Double(t);
	}

	public void setTrust(String player, double t)
	{
		if (trust.containsKey(player))
		{
			trust.get(player).setValue(t);
			return;
		}
		History<Double> h = new History<Double>(50);
		h.newEntry(t);
		trust.put(player, h);
	}

	public double getEconomicBelief()
	{
		return economicBelief;
	}
	
		public void setEconomicBelief(double economicBelief)
	{
		this.economicBelief = economicBelief;
	}

	public void newHistoryEntry()
	{
		happinessHistory.newEntry(true);
		loyaltyHistory.newEntry(true);
		huntingTeam.newEntry(false);
		foodConsumedPerTurnHistory.newEntry(foodConsumption);
		lastHunted.newEntry(false);
		for (History<Double> h : trust.values())
		{
			h.newEntry(true);
		}
	}

	public String getName()
	{
		return name;
	}
}
