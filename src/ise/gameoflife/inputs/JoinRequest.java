package ise.gameoflife.inputs;

/**
 * After considering the balance between work and play, this class specified
 * how an agent goes about choosing a group to be with for their term in
 * the game
 * @author Benedict
 */
public class JoinRequest extends GenericInput
{
	
	//Candidate in question
	private String agent;

	
	/**
	 * Formalises the candidates request. A series of strange and
	 * embarrassing forms of "hazing" is sure to follow
	 * @param time
	 * @param agent 
	 */
	public JoinRequest(long time, String agent)
	{
		super(time, "joinrequest");
		this.agent = agent;
	}

	/**
	 * @return agent wanting to join the group
	 */
	public String getAgent()
	{
		return agent;
	}

}
