package ise.gameoflife.models;

/**
 * Group data intialiser is an object that contains all the information used
 * to create a new group.
 * @author Benedict
 */
public class GroupDataInitialiser
{
	private long randomSeed;
	
	/**
	 * Initialises the random seed
	 * @param randomSeed 
	 */
	public GroupDataInitialiser(long randomSeed)
	{
		this.randomSeed = randomSeed;
	}

	/**
	 * Gets the random seed used to create groups
	 * @return 
	 */
	public long getRandomSeed()
	{
		return randomSeed;
	}

}
