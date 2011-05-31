package ise.gameoflife.plugins;

import java.awt.Color;
import javax.swing.JPanel;
import presage.Plugin;
import presage.Simulation;

/**
 * Creates the JPanel Plugin which will be used to log errors
 * @author Olly Hill
 * @author Benedict Harcourt
 */
public class ErrorLog extends JPanel implements Plugin
{

	private final static long serialVersionUID = 1L;

	private final static String label = "Error Log";

	private Simulation sim;

	/**
	 * Default constructor - does nothing.
	 */
	public ErrorLog()
	{
		// Nothing to see here. Move along, citizen!
	}

	/**
	 * Returns the Label
	 * @return 
	 */
	@Override
	public String getLabel()
	{
		return label;
	}

	/**Returns the Short Label
	 * @return 
	 */
	@Override
	public String getShortLabel()
	{
		return label;
	}

	/**
	 * Creates a new instance of ErrorLog - called during simulation
	 * @param sim
	 */
	@Override
	public void initialise(Simulation sim)
	{
		this.sim = sim;
		setBackground(Color.LIGHT_GRAY);
	}

	/**
	 * TODO: Documentation
	 */
	@Override
	public void execute()
	{
		// FIXME: Write execute code for ErrorLog Plugin
	}

	/**
	 * Deals with plugin upon plugin deletion
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void onDelete()
	{
		// Nothing to see here. Move along, citizen!
	}

	/**
	 * Deals with plugin upon simulation completion
	 */
	@Override
	public void onSimulationComplete()
	{
		// FIXME: Write onSimulationComplete code for ErrorLog Plugin
	}

}