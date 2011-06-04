package ise.gameoflife.models;

/**
 * Group data intialiser is an object that contains all the information used
 * to create a new group.
 * @author Benedict
 */
public class GroupDataInitialiser
{
	private final static Object counterLock = new Object();
	private static int counter = 0;

	private long randomSeed;
	private double initialEconomicBelief;
	private String name;
	
	/**
	 * Creates an Initialiser for a group
	 * @param randomSeed The random seed for the group
	 * @param initialEconomicBelief The initial economic belief of the group
	 */
	public GroupDataInitialiser(long randomSeed, double initialEconomicBelief)
	{
		this.initialEconomicBelief = initialEconomicBelief;
		this.randomSeed = randomSeed;
		synchronized(counterLock)
		{
			counter++;
			this.name = "Group #" + counter;
		}
	}

	/**
	 * Gets the random seed used to by the group
	 * @return The random seed used to by the groups
	 */
	public long getRandomSeed()
	{
		return randomSeed;
	}

	public double getInitialEconomicBelief()
	{
		return initialEconomicBelief;
	}

	public String getName()
	{
		return name;
	}

}
