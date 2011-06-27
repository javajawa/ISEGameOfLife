package ise.mace.genetics;

/**
 * An evolvable being that can be evaluated for fitness.
 * Its behaviour is defined by a Genome instance of its own type.
 * @author Xitong Gao
 */
public interface Evolvable<EntityGenome extends Genome<EntityGenome>>
{

	public EntityGenome genome();

	/**
	 * Evolvable being must update its data structures that define its
	 * behaviour when setting the genome.
	 * @param genome a genome matching EntityGenome generic
	 */
	public void setGenome(EntityGenome genome);

	public double fitness();

	public void setFitness(double aFitness);

}
