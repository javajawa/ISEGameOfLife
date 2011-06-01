package ise.gameoflife.participants;

import presage.Input;

/**
 * @author Benedict
 */
interface InputHandler
{
	/**
	 * Returns true is the input can be handled
	 * @param input
	 * @return 
	 */
	public boolean canHandle(Input input);
	/**
	 * Handles the input
	 * @param input
	 * @return 
	 */
	public void handle(Input input);
}
