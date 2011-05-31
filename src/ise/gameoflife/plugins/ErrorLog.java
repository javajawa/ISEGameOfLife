package ise.gameoflife.plugins;

import example.ExampleEnvDataModel;
import java.awt.Color;
import javax.swing.JPanel;
import presage.Plugin;
import presage.Simulation;

/**
 * TODO: Documentation
 * @author Olly Hill
 * @author Benedict Harcourt
 */
public class ErrorLog extends JPanel implements Plugin
{

	private static final long serialVersionUID = 1L;
	private Simulation sim;

	/**
	 * TODO: Documentation
	 */
	public ErrorLog()
	{
		// Nothing to see here. Move along, citizen!
	}

	/**
	 * TODO: Documentation
	 * TODO: Move this to a private final static field
	 * @return 
	 */
	public String getLabel()
	{
		return "ErrorLog";
	}

	/**
	 * TODO: Documentation
	 * TODO: Move this to a private final static field
	 * @return 
	 */
	public String getShortLabel()
	{
		return "ErrorLog";
	}

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
	 * TODO: Documentation
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void onDelete()
	{
		// Nothing to see here. Move along, citizen!
	}

	@Override
	public void onSimulationComplete()
	{
		// FIXME: Write onSimulationComplete code for ErrorLog Plugin
	}

}