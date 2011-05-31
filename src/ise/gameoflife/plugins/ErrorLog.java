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

	private static final long serialVersionUID = 1L;
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
	 * TODO: Move this to a private final static field
	 * @return 
	 */
	@Override
	public String getLabel()
	{
		return "ErrorLog";
	}

	/**Returns the Short Label
	 * TODO: Move this to a private final static field
	 * @return 
	 */
	@Override
	public String getShortLabel()
	{
		return "ErrorLog";
	}

	/**
	 * Creates a new instance of ErrorLog - called during simulation
	 * @param sim
	 */
	@Override
	public void initialise(Simulation sim)
	{
		System.out.println(" ErrorLog Initialising....");
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