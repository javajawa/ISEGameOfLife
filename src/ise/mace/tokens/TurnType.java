package ise.mace.tokens;

import ise.mace.actions.Hunt;
import ise.mace.actions.Proposal;
import ise.mace.environment.EnvironmentDataModel;
import ise.mace.inputs.VoteResult;
import ise.mace.models.HuntingTeam;
import ise.mace.participants.AbstractAgent;
import ise.mace.participants.AbstractFreeAgentGroup;
import ise.mace.participants.AbstractGroupAgent;
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
	 * {@link AbstractAgent Agents} choose which {@link AbstractGroupAgent Group}
	 * they wish to join. They may also choose to leave groups by joining the
	 * {@link AbstractFreeAgentGroup Free Agents Group}
	 */
	GroupSelect,
	/**
	 * {@link AbstractGroupAgent Groups} split {@link AbstractAgent Agents} into
	 * {@link HuntingTeam hunting teams}
	 */
	TeamSelect,
	/**
	 * {@link AbstractAgent Agents} specify which {@link Food} it wishes to
	 * attempt to {@link Hunt} with its {@link HuntingTeam}
	 */
	GoHunt,
	/**
	 * {@link Environment} determines what the result of each {@link Hunt} was
	 */
	HuntResults,
	/**
	 * Agents make {@link Proposal}s to their group
	 */
	MakeProposals,
	/**
	 * {@link VoteResult Votes data} is returned from the previous round, and data is updated
	 */
	Voting;
	/**
	 * The first turn type in a round
	 */
	public final static TurnType firstTurn = values()[0];
}
