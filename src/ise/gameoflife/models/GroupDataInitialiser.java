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
	 * Creates an Initialiser for a group
	 * @param randomSeed The random seed for the group
	 */
	public GroupDataInitialiser(long randomSeed)
	{
		this.randomSeed = randomSeed;
	}

	/**
	 * Gets the random seed used to by the group
	 * @return The random seed used to by the groups
	 */
	public long getRandomSeed()
	{
		return randomSeed;
	}

}
