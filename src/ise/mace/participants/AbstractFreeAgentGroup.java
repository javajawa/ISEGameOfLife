package ise.mace.participants;

import ise.mace.models.HuntingTeam;
import java.util.List;

/**
 *
 */
public abstract class AbstractFreeAgentGroup
{
	abstract public List<HuntingTeam> selectTeams(List<String> freeAgents);
}
