package ise.gameoflife.enviroment.actionhandlers;

import ise.gameoflife.actions.Hunt;
import ise.gameoflife.enviroment.Environment;
import presage.Action;
import presage.Input;

/**
 *
 * @author Benedict Harcourt
 */
public class HuntHandler extends Environment.ActionHandler
{
	private Environment en;
	
	@Override
	public boolean canHandle(Action action)
	{
		return (Action.class.equals(Hunt.class));
	}

	@Override
	public Input handle(Action action, String actorID)
	{
		final Hunt act = (Hunt)action;
		// FIXME: Implement this action
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public HuntHandler(Environment en)
	{
		this.en = en;
	}
	
}
