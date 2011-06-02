package ise.gameoflife.actions;

import java.io.Serializable;
import presage.Action;

/**
 * Marker Abstract class that makes sure that all Actions are Serializable,
 * and helps JavaDoc draw the class tree
 * @author Benedict Harcourt
 */
public abstract class GenericAction implements Action, Serializable
{

	// To force this to be a class, not an interface
	@Override
	public String toString()
	{
		return super.toString();
	}

}
