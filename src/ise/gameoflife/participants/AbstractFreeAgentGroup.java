package ise.gameoflife.participants;

import ise.gameoflife.models.HuntingTeam;
import java.util.List;

/**
 *
 * @author Benedict
 */
public abstract class AbstractFreeAgentGroup
{
	abstract public List<HuntingTeam> selectTeams(List<String> freeAgents);
}
