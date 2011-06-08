package ise.gameoflife.genetics;

/**
 * An entity which conforms to the Evolvable interface
 * @author Xitong Gao
 */
public class EvolvableEntity
	<SpecieGenome extends Genome<SpecieGenome>>
	implements Evolvable<SpecieGenome>
{
	SpecieGenome genome = null;
	double fitness = -1;

	@Override
	public SpecieGenome genome()
	{
		return this.genome;
	}

	/**
	 * Subclasses should override this method and call super
	 * @param aGenome a compatible Genome instance for this
	 */
	@Override
	public void setGenome(SpecieGenome aGenome)
	{
		// check if genome is compatible
		if (!aGenome.compatibleEvolvable(this))
		{
			throw new RuntimeException(
					"Genome (" + aGenome.getClass().getName() + ") " +
					"is not compatible with Evolvable (" +
					this.getClass().getName() +")");
		}

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
