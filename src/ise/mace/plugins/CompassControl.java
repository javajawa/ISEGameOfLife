package ise.mace.plugins;

import ise.mace.environment.Environment;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import presage.Plugin;
import presage.Simulation;

/**Control the functionality of the compass on real time
 *
 */
public class CompassControl extends JPanel implements Plugin
{
	private static final long serialVersionUID = 1L;
	private final static String title = "Compass Control";
	private final static String label = "Compass Control";
	private Simulation sim;
	private Environment en;
	private JPanel control = new JPanel();
	private JPanel control2 = new JPanel();
	public static boolean agent_button = true;
	public static boolean group_button = true;

	public CompassControl()
	{
		// Nothing to see here. Move along, citizen.
	}

	@Override
	public void execute()
	{
	}

	private void updateChart()
	{
	}

	/**
	 * Returns the label of this plugin
	 * @return The label of this plugin
	 */
	@Override
	public String getLabel()
	{
		return label;
	}

	/**
	 * Returns the short label of this plugin
	 * @return The short label of this plugin
	 */
	@Override
	public String getShortLabel()
	{
		return label;
	}

	/**
	 * Initialises a plugin that was stored using the SimpleXML framework, making
	 * it ready to be used in the visualisation of a simulation
	 * @param sim The simulation to which this plugin will belong
	 */
	@Override
	public void initialise(Simulation sim)
	{
		System.out.println(" -Initialising....");

		this.sim = sim;
		this.en = (Environment)sim.environment;

		setBackground(Color.GRAY);

		final JButton ShowAgentButton = new JButton("Show agents");
		final JButton HideAgentButton = new JButton("Hide agents");
		final JButton ShowGroupButton = new JButton("Show Special agents");
		final JButton HideGroupButton = new JButton("Hide Special agents");
		ShowAgentButton.setEnabled(false);
		HideAgentButton.setEnabled(true);
		ShowGroupButton.setEnabled(false);
		HideGroupButton.setEnabled(true);

		ShowAgentButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				ShowAgentButton.setEnabled(false);
				HideAgentButton.setEnabled(true);
				agent_button = true;
			}
		});

		HideAgentButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				ShowAgentButton.setEnabled(true);
				HideAgentButton.setEnabled(false);
				agent_button = false;
			}
		});


		ShowGroupButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				ShowGroupButton.setEnabled(false);
				HideGroupButton.setEnabled(true);
				group_button = true;
			}
		});

		HideGroupButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				ShowGroupButton.setEnabled(true);
				HideGroupButton.setEnabled(false);
				group_button = false;
			}
		});

		//control.add(label);
		control.setMaximumSize(control.getMinimumSize());
		control2.setMaximumSize(control2.getMinimumSize());

		control.add(ShowAgentButton);
		control.add(HideAgentButton);

		control2.add(ShowGroupButton);
		control2.add(HideGroupButton);

		this.setLayout(new BorderLayout());
		add(control, BorderLayout.NORTH);
		add(control2, BorderLayout.CENTER);

	}

	/**
	 * Is not used by simulation
	 * @deprecated Not used by any calling class
	 */
	@Deprecated
	@Override
	public void onDelete()
	{
		// Nothing to see here. Move along, citizen!
	}

	/**
	 * Preforms actions when a simulation has run to completion, such as
	 * outputting the graph to a file for later viewing
	 */
	@Override
	public void onSimulationComplete()
	{

		// Firstly, remove the update button, and (forcibly) refresh the chart
		this.remove(control);

	}
}
