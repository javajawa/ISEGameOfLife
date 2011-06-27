package ise.mace.inputs;

/**
 *
 */
public final class HuntResult extends GenericInput
{

	private static final long serialVersionUID = 1L;
	private String agent;
	private double foodHunted;
	private double foodReceived;

	/**
	 * Creates a new instance of the HuntResult class, which gives the result of a 
	 * Hunt action.
	 * @param agent The agent that did the hunting
	 * @param foodHunted The amount of food that was hunted
	 * @param foodReceived The amount of food gained in this hunt
	 * @param time The simulation time at which this result occurred 
	 */
	public HuntResult(String agent, double foodHunted, double foodReceived,
					long time)
	{
		super(time, "huntresult:" + foodReceived + '/' + foodHunted);
		this.foodHunted = foodHunted;
		this.foodReceived = foodReceived;
		this.agent = agent;
	}

	/**
	 * Gets the agent that returned this much food
	 * @return The hunter
	 */
	public String getAgent()
	{
		return agent;
	}

	public double getFoodReceived()
	{
		return foodReceived;
	}

	public double getFoodHunted()
	{
		return foodHunted;
	}

}
