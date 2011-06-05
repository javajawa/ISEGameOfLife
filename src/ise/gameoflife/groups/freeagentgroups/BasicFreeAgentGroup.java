package ise.gameoflife.groups.freeagentgroups;

import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.participants.AbstractFreeAgentGroup;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benedict
 */
public class BasicFreeAgentGroup extends AbstractFreeAgentGroup
{

	@Override
	public List<HuntingTeam> selectTeams(List<String> freeAgents)
	{
		ArrayList<HuntingTeam> teams = new ArrayList<HuntingTeam>();
		int agents = freeAgents.size();

		for(int i=0; i < agents; i += 2){
			int ubound = (i + 2 >= agents) ? agents : i + 2;
			teams.add(new HuntingTeam (freeAgents.subList(i, ubound)));
		}

		return teams;
	}
	
}
