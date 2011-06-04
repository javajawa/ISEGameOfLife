package ise.gameoflife.plugins;

import ise.gameoflife.environment.Environment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import presage.Plugin;
import presage.Simulation;

/**
 *
 * @author Benedict
 */
public class DebugSwitchPlugin extends JPanel implements Plugin
{
	private static final long serialVersionUID = 1L;

	JButton b;
	Environment env;
	
	@Override
	public void execute()
	{
		// Nothing to see here. Move along, citizen!
	}

	@Override
	public void initialise(Simulation sim)
	{
		env = (Environment)sim.environment;
		b = new JButton("Turn on Debug Mode");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				env.setDebug(!env.getDebug());
				b.setText("Turn Debug " + (env.getDebug() ? "Off" : "On"));
			}
		});
		this.add(b);
	}

	@Override
	public void onDelete()
	{
		// Nothing to see here. Move along, citizen!
	}

	@Override
	public void onSimulationComplete()
	{
		// Nothing to see here. Move along, citizen!
	}

	@Override
	public String getLabel()
	{
		return "DebugSwitch";
	}

	@Override
	public String getShortLabel()
	{
		return "DebugSwitch";
	}
	
}
