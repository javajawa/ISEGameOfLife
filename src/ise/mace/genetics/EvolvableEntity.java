package ise.mace.genetics;

/**
 * An entity which conforms to the Evolvable interface
 */
public class EvolvableEntity
	<EntityGenome extends Genome<EntityGenome>>
	implements Evolvable<EntityGenome>
{
	EntityGenome genome = null;
	double fitness = -1;

	@Override
	public EntityGenome genome()
	{
		return this.genome;
	}

	/**
	 * Subclasses should override this method and call super
	 * @param aGenome a compatible Genome instance for this
	 */
	@Override
	public void setGenome(EntityGenome aGenome)
	{
		// this.genome = aGenome.clone();
		this.genome = aGenome;
	}

	@Override
	public double fitness()
	{
		return this.fitness;
	}

	@Override
	public void setFitness(double aFitness)
	{
		this.fitness = aFitness;
	}

}
