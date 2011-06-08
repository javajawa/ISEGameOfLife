package ise.gameoflife.plugins;

import ise.gameoflife.environment.Environment;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;
import presage.Plugin;
import presage.Simulation;

/**
 *
 * @author Benedict
 */
public class DebugSwitchPlugin extends JPanel implements Plugin
{
	private static final long serialVersionUID = 1L;
	private final static String levels[] = {"INHERITED", "ALL", "SEVERE", "WARNING", "INFO", "FINE", "OFF"};

	private class LoggerPanel extends JPanel
	{
		private final JComboBox box;
		private final JLabel lbl;
		private final Logger logger;

		private LoggerPanel(String loggerName)
		{
			logger = Logger.getLogger(loggerName);
			if (logger == null) throw new IllegalArgumentException("Logger " + loggerName + " not found");

			lbl = new JLabel(loggerName.isEmpty() ? "-- ROOT LOGGER -- " : loggerName);
			box = new JComboBox(levels);

			Level lvl = logger.getLevel();
			box.setSelectedItem((lvl == null ? "INHERITED" : lvl.getName()));

			box.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e)
				{
					String newLevel = ((JComboBox)e.getSource()).getSelectedItem().toString();
					if (newLevel.equals("INHERITED"))
					{
						logger.setLevel(null);
					}
					else
					{
						logger.setLevel(Level.parse(newLevel));
					}
				}
			});

			this.setLayout(new GridLayout(1, 2));
			this.add(lbl);
			this.add(box);
		}
	}
	private JButton b;
	private Environment env;
	
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
		this.setLayout(new BorderLayout());

		JPanel loggers = new JPanel();
		loggers.setLayout(new BoxLayout(loggers, BoxLayout.PAGE_AXIS));
		TreeMap<String, LoggerPanel> sortingTree = new TreeMap<String, LoggerPanel>(Collator.getInstance());

		LogManager root =	java.util.logging.LogManager.getLogManager();
		for (Enumeration<String> it = root.getLoggerNames(); it.hasMoreElements();)
		{
			String name = it.nextElement();

			if (name.indexOf('.') > -1)
			{
				String rootPackage = name.substring(0, name.indexOf('.'));
				if (rootPackage.equals("java") || rootPackage.equals("sun")) continue;
			}

			sortingTree.put(name, new LoggerPanel(name));
		}

		for (String it : sortingTree.keySet())
		{
			loggers.add(sortingTree.get(it));
		}
		this.add(b, BorderLayout.NORTH);
		this.add(new JScrollPane(loggers), BorderLayout.CENTER);
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
