package ise.gameoflife.participants;

import ise.gameoflife.models.HuntingTeam;
import java.util.List;
import java.util.Map;

/**
 * Class describes a leader and states the roles of the leader.
 * Leader may respond to requests to join their group, issue orders to 
 * agents to hunt a specific type of food and distribute food once
 * everyone is done hunting
 * 
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
