package ise.mace.participants;

import presage.Input;

/**
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
	 */
	public void handle(Input input);
}
