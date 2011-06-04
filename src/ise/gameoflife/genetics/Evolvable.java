package ise.gameoflife.genetics;

/**
 * An evolvable being that can be evaluated for fitness. Its behaviour is
 * defined by a Genome of its own type.
 * @author Xitong Gao
 */
public class Evolvable
{

	protected Genome genome = null;
	protected double fitness = -1;

	public Genome genome()
	{
		return this.genome;
	}

	public void setGenome(Genome aGenome)
	{
		// this.genome = aGenome.clone();
		this.genome = aGenome;
		// change of parameters after setting genome...
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
