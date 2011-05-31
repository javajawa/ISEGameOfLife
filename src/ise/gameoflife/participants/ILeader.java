package ise.gameoflife.participants;

import ise.gameoflife.models.HuntingTeam;
import java.util.List;
import java.util.Map;

/**
 * TODO: Document this class
 * Note: contract must include adding "leader" to the default list of roles.
 * MUST MUST MUST!
 * @author Benedict
 */
public interface ILeader
{
	boolean respondToJoinRequest(String playerID);
	List<HuntingTeam> selectTeams();
	Map<String, Double> distributeFood(Map<String, Double> gains);
}
