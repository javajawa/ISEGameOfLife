package ise.gameoflife.tokens;

/**
 * FIXME: Document these values
 * @author Benedict
 */
public enum TurnType
{
	GroupSelect,
	TeamSelect,
	GoHunt,
	HuntResults;

	public final static TurnType firstTurn = values()[0];
}
