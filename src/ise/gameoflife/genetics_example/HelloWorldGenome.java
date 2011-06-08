package ise.gameoflife.genetics_example;

import ise.gameoflife.genetics.Genome;
import java.util.Random;
import java.lang.String;

/**
 * A simple Hello World Genome
 * @author Xitong Gao
 */
public class HelloWorldGenome extends Genome<HelloWorldGenome>
{
	private int strLen = 13;
	private String emptyStr = "             ";
	private float mutateRate = 0.25f;

	// geneString and its setter & getter
	private String geneString;
	public String geneString()
	{
		return this.geneString;
	}
	public void setGeneString(String newString)
	{
		geneString = newString;
	}

	/**
	 * Generates a randomized genome representation
	 * by giving a random string
	 */
	public void randomize()
	{
		char str[] = emptyStr.toCharArray();
		for (int i = 0; i < strLen; i++)
		{
			str[i] = (char)(rand.nextInt(90) + 32);
		}
		this.setGeneString(new String(str));
	}

	/**
	 * Mutates the genome
	 * @return a new genome
	 */
	public HelloWorldGenome mutate()
	{
		if (rand.nextFloat() >= mutateRate)
		{
			return this;
		}

		char str[] = this.geneString().toCharArray();
		int pos = rand.nextInt(strLen);
		str[pos] = (char)(rand.nextInt(90) + 32);

		HelloWorldGenome genome = new HelloWorldGenome();
		genome.setGeneString(new String(str));

		return genome;
	}

	/**
	 * Crosses over with another genome
	 * Must check if the genome is compatible with this before
	 * crossing over
	 * GenePool already knows how to provide genomes in an unbiased order
	 * @param genome another genome
	 * @return a new genome
	 */
	public HelloWorldGenome crossOver(HelloWorldGenome genome)
	{
		String geneStringA = genome.geneString();
		String geneStringB = this.geneString();

		int pos = rand.nextInt(strLen) + 1;
		String newString = new String(geneStringA.substring(0, pos));
		newString += geneStringB.substring(pos, strLen);

		HelloWorldGenome newGenome = new HelloWorldGenome();
		newGenome.setGeneString(newString);

		return newGenome;
	}

	Random rand = new Random();
}
