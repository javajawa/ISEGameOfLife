package ise.mace.tokens;
import ise.mace.environment.EnvironmentDataModel;
import presage.Presage;
/**
 * <p>Enumoratored Class that determines the stage of the games</p>
 * <p>Each cycle that {@link Presage} simulates, which is equivalent to each
 * {@link EnvironmentDataModel#time time stamp} has an associated turn, which
 * can be found in {@link EnvironmentDataModel#turn}.
 * These turns are cycles through based on the constant {@link #values() values}
 * array, which follows the order of declaration within the class.
 * A series of cycles consisting of all of the turn in <code>values</code> is
 * known as a round, and the current round number is stored at {@link
 * EnvironmentDataModel#rounds}.
 * Rounds begin with the turn specified in the {@link #firstTurn first turn}
 * constant.</p>
 */
public enum TurnType
{
	/**
	 * Environment specifies which groups it has accepted agents into
	 */
	GroupSelect,
	/**
	 * Group splits agents into hunting teams
	 */
	TeamSelect,
	/**
	 * Agent specifies which food it wishes to obtain
	 */
	GoHunt,
	/**
	 * Environment states what the result of each hunt has been
	 */
	HuntResults,
	/**
	 * Agents make proposals to their group
	 */
	MakeProposals,
	/**
	 * Votes data is returned from the previous round, and data is updated
	 */
	Voting;
	/**
	 * The first turn type in a round
	 */
	public final static TurnType firstTurn = values()[0];
}
