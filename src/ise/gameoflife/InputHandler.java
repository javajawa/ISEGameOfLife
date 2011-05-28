package ise.gameoflife;

import presage.Input;

/**
 * FIXME: Do documentation
 * @author Benedict
 */
interface InputHandler
{
	public boolean canHandle(Input input);
	public void handle(Input input);
}
