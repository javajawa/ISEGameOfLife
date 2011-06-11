package ise.gameoflife.participants;

import ise.gameoflife.models.History;
import ise.gameoflife.models.UnmodifiableHistory;
import ise.gameoflife.models.Food;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.models.NameGenerator;
import ise.gameoflife.models.ScaledDouble;
import ise.gameoflife.tokens.AgentType;
import java.util.HashMap;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
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
	
	@ElementMap(keyType=String.class,valueType=History.class)
	private HashMap<String,History<ScaledDouble>> trust;

	@ElementMap(keyType=String.class,valueType=History.class)
	private HashMap<String,History<Food>> advice;

	@Element
	private History<ScaledDouble> happinessHistory;
	
	@Element
	private History<ScaledDouble> loyaltyHistory;

	@Element
	private double economicBelief;

	@Element
	private double socialBelief;

	@Element(required=false)
	private AgentType agentType;
	
	private History<Food> lastHunted;
	private History<HuntingTeam> huntingTeam;

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
	AgentDataModel(
	    String myId, String roles,
	    Class<? extends AbstractAgent> playerClass,
	    long randomseed, double foodInPossesion, double foodConsumption)
	{
		this(myId, roles, playerClass, randomseed, foodInPossesion, foodConsumption, null, 0, 0);
	}
	@SuppressWarnings("deprecation")
	AgentDataModel(
	    String myId, String roles,
	    Class<? extends AbstractAgent> playerClass,
	    long randomseed, double foodInPossesion, double foodConsumption,
	    AgentType type, double socialBelief, double economicBelief
	  )
	{
		super(myId, roles, playerClass.getSimpleName() + (type == null ? "" : " [" + type + ']'), randomseed);
		this.foodInPossesion = foodInPossesion;
		this.foodConsumption = foodConsumption;
		this.name = NameGenerator.getName();
		this.agentType = type;
		this.economicBelief = economicBelief;
		this.socialBelief = socialBelief;
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
		happinessHistory = new History<ScaledDouble>(50);
		huntingTeam = new History<HuntingTeam>(50);
		loyaltyHistory = new History<ScaledDouble>(50);
		lastHunted = new History<Food>(50);
		trust = new HashMap<String, History<ScaledDouble>>();
		advice = new HashMap<String, History<Food>>();

		happinessHistory.newEntry(new ScaledDouble(0.25));
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

	public History<Food> getHuntingHistory()
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

	public History<HuntingTeam> getTeamHistory()
	{
		return huntingTeam.getUnmodifableHistory();
	}

	public double getCurrentHappiness()
	{
		return happinessHistory.getValue().doubleValue();
	}

	@Deprecated
	public void setCurrentHappiness(double newHappiness)
	{
		happinessHistory.setValue(new ScaledDouble(newHappiness));
	}

	public void alterHappiness(int amount)
	{
		happinessHistory.getValue().alterValue(amount);
	}
	public History<ScaledDouble> getHappinessHistory()
	{
		return happinessHistory.getUnmodifableHistory();
	}

	public double getCurrentLoyalty()
	{
		return loyaltyHistory.getValue().doubleValue();
	}

	@Deprecated
	public void setCurrentLoyalty(double newLoyalty)
	{
		loyaltyHistory.setValue(new ScaledDouble(newLoyalty));
	}

	public void clearLoyalty()
	{
		loyaltyHistory.setValue(new ScaledDouble(0));
	}

	public void alterLoyalty(int amount)
	{
		loyaltyHistory.getValue().alterValue(amount);
	}

	public History<ScaledDouble> getLoyaltyHistory()
	{
		return loyaltyHistory.getUnmodifableHistory();
	}
	
	public History<ScaledDouble> getTrustHistory(String player)
	{
		if (trust.containsKey(player))
		{
			return trust.get(player).getUnmodifableHistory();
		}
		History<ScaledDouble> t = new History<ScaledDouble>(50);
		t.newEntry(new ScaledDouble(0.25));
		trust.put(player, t);
		return t.getUnmodifableHistory();
	}

	double getTrust(String player)
	{
		return getTrustHistory(player).getValue().doubleValue();
	}

	@Deprecated
	void setTrust(String player, double t)
	{
		if (trust.containsKey(player))
		{
			trust.get(player).setValue(new ScaledDouble(t));
			return;
		}
		History<ScaledDouble> h = new History<ScaledDouble>(50);
		h.newEntry(new ScaledDouble(t));
		trust.put(player, h);
	}

	void alterTrust(String player, int amount)
	{
		History<ScaledDouble> h = getTrustHistory(player);
		h.getValue().alterValue(amount);
	}
	/**
	 * Returns a history of the advice given to this agent.
	 * getValue() will return null unless advice has already been given this round
	 * Past rounds where not advice was given will also return null
	 * isEmpty will always be false, use size() to check for amount of history
	 * @param player
	 * @return 
	 */
	History<Food> getAdviceHistory(String player)
	{
		if (advice.containsKey(player))
		{
			return advice.get(player).getUnmodifableHistory();
		}
		History<Food> t = new History<Food>(50);
		t.newEntry(null);
		advice.put(player, t);
		return t.getUnmodifableHistory();
	}

	void gaveAdvice(String player, Food t)
	{
		if (advice.containsKey(player))
		{
			advice.get(player).setValue(t);
			return;
		}
		History<Food> h = new History<Food>(50);
		h.newEntry(t);
		advice.put(player, h);
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
		for (History<ScaledDouble> h : trust.values())
		{
			h.newEntry(true);
		}
		for (History<Food> h : advice.values())
		{
			h.newEntry(null);
		}
	}

	public String getName()
	{
		return name;
	}

	/**
	 * @return the socialBelief
	 */
	double getSocialBelief()
	{
		return socialBelief;
	}

	/**
	 * @return the agentType
	 */
	AgentType getAgentType()
	{
		return agentType;
	}

	void setSocialBelief(double socialBelief)
	{
		this.socialBelief = socialBelief;
	}

}
