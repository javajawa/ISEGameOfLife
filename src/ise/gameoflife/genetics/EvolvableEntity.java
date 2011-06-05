package ise.gameoflife.genetics;

/**
 * An entity which conforms to the Evolvable interface
 * @author admko
 */
public abstract class EvolvableEntity implements Evolvable
{
	Genome genome = null;
	double fitness = -1;

	public Genome genome()
	{
		return this.genome;
	}

	/**
	 * Subclasses should override this method and call super
	 * @param aGenome a compatible Genome instance for this
	 */
	public void setGenome(Genome aGenome)
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

	public double fitness()
	{
		return this.fitness;
	}

	public void setFitness()
	{
		this.fitness = fitness;
	}

}
