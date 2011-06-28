package ise.mace.plugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import presage.Plugin;
import presage.Simulation;

/**
 *
 */
public class DebugSwitchPlugin extends JPanel implements Plugin
{
	private static final long serialVersionUID = 1L;
	private final static String levels[] =
	{
		"INHERITED", "ALL", "SEVERE", "WARNING", "INFO", "FINE", "OFF"
	};

	private class LoggerPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		private final JComboBox box;
		private final JLabel lbl;
		@SuppressWarnings("NonConstantLogger")
		private final Logger logger;

		private LoggerPanel(String loggerName)
		{
			logger = Logger.getLogger(loggerName);
			if (logger == null)
				throw new IllegalArgumentException("Logger " + loggerName + " not found");

			lbl = new JLabel(loggerName);
			lbl.setAlignmentY(Component.RIGHT_ALIGNMENT);
			box = new JComboBox(levels);

			Level lvl = logger.getLevel();
			box.setSelectedItem((lvl == null ? "INHERITED" : lvl.getName()));

			box.addActionListener(new ActionListener()
			{
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

			if (!Arrays.asList(logger.getHandlers()).contains(logDel))
				logger.addHandler(logDel);

			this.setLayout(new GridLayout(1, 2));
			this.add(lbl);
			this.add(box);
		}
	}

	private class LogDelegate extends Handler
	{
		private final SimpleFormatter f = new SimpleFormatter();

		private LogDelegate()
		{
		}

		@Override
		public void publish(LogRecord record)
		{
			StringBuilder b = new StringBuilder();
			b.append('[');
			b.append(df.format(new Date(record.getMillis())));
			b.append('/');
			synchronized (timeLock)
			{
				b.append(time);
			}
			b.append("] ");
			b.append(record.getLoggerName());
			b.append(" [");
			b.append(record.getLevel().getLocalizedName());
			b.append("] ");
			b.append(": ");
			b.append(f.formatMessage(record));

			Throwable t = record.getThrown();
			if (t != null)
			{
				String indent = "\n";
				Throwable subt = t;

				while (subt != null)
				{
					indent += '\t';
					b.append(indent);
					b.append(t.getClass().getSimpleName());
					b.append(' ');
					b.append(t.getMessage());

					subt = subt.getCause();
				}

				for (StackTraceElement ste : t.getStackTrace())
				{
					b.append("\n\t\t");
					b.append(ste.toString());
				}
			}

			b.append('\n');

			Color c;
			if (record.getLevel().intValue() > Level.INFO.intValue())
			{
				c = new Color((float)record.getLevel().intValue() / 1000, 0, 0);
			}
			else
			{
				c = Color.BLACK;
			}

			SwingUtilities.invokeLater(new LogWriter(b.toString(), c));
		}

		@Override
		public void flush()
		{
			// Nothing to see here. Move along, citizen!
		}

		@Override
		public void close() throws SecurityException
		{
			// Nothing to see here. Move along, citizen!
		}
	}

	private class LogWriter implements Runnable
	{
		private final String data;
		private final Color c;

		LogWriter(String data, Color c)
		{
			this.data = data;
			this.c = c;
		}

		@Override
		public void run()
		{
			synchronized (textArea)
			{
				SimpleAttributeSet as = new SimpleAttributeSet();
				StyleConstants.setForeground(as, c);
				textArea.setCharacterAttributes(as, true);

				textArea.setCaretPosition(textArea.getDocument().getLength());
				textArea.replaceSelection(data);
			}
		}
	}

	private final JPanel loggers = new JPanel();
	private final TreeMap<String, LoggerPanel> sortingTree = new TreeMap<String, LoggerPanel>(
					Collator.getInstance());
	private final LogManager root = java.util.logging.LogManager.getLogManager();
	private final JTextPane textArea = new JTextPane();
	private final LogDelegate logDel = new LogDelegate();
	private final static DateFormat df = DateFormat.getTimeInstance();
	private final Object timeLock = new Object();
	private Simulation sim;
	private long time = 0;

	@Override
	public void execute()
	{
		synchronized (timeLock)
		{
			time = sim.getTime();
		}

		Enumeration<String> logs = root.getLoggerNames();
		boolean areNew = false;

		while (logs.hasMoreElements())
		{
			String name = logs.nextElement();
			if (sortingTree.containsKey(name)) continue;

			if (name.indexOf('.') > -1)
			{
				String rootPackage = name.substring(0, name.indexOf('.'));
				if (rootPackage.contains("java") || rootPackage.equals("sun")) continue;
			}
			else
			{
				continue;
			}

			sortingTree.put(name, new LoggerPanel(name));
			areNew = true;
		}

		if (areNew)
		{
			loggers.removeAll();
			for (String it : sortingTree.keySet())
			{
				loggers.add(sortingTree.get(it));
			}
		}
	}

	@Override
	public void initialise(Simulation sim)
	{
		this.sim = sim;
		loggers.setLayout(new BoxLayout(loggers, BoxLayout.PAGE_AXIS));

		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(loggers), BorderLayout.NORTH);
		this.add(new JScrollPane(textArea), BorderLayout.CENTER);
		execute();
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
		return "Loggers";
	}

	@Override
	public String getShortLabel()
	{
		return "Loggers";
	}
}
