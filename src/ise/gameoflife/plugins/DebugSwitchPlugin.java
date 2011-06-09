package ise.gameoflife.plugins;

import java.awt.BorderLayout;
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
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
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
		private static final long serialVersionUID = 1L;
		private final JComboBox box;
		private final JLabel lbl;
		@SuppressWarnings("NonConstantLogger")
		private final Logger logger;

		private LoggerPanel(String loggerName)
		{
			logger = Logger.getLogger(loggerName);
			if (logger == null) throw new IllegalArgumentException("Logger " + loggerName + " not found");

			lbl = new JLabel(loggerName);
			lbl.setAlignmentX(RIGHT_ALIGNMENT);
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

			if (!Arrays.asList(logger.getHandlers()).contains(logDel)) logger.addHandler(logDel);

			this.setLayout(new GridLayout(1, 2));
			this.add(lbl);
			this.add(box);
		}
	}
	
	final LogDelegate logDel = new LogDelegate();
	final static DateFormat df = DateFormat.getTimeInstance();
		
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
			b.append("] ");
			b.append(record.getLoggerName());
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
			SwingUtilities.invokeLater(new LogWriter(b.toString()));
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
		private String data;

		LogWriter(String data)
		{
			this.data = data;
		}

		@Override
		public void run()
		{
			synchronized(textArea)
			{
				textArea.append(data);
				textArea.setCaretPosition(textArea.getDocument().getLength());
				// TODO: Make things colourful
				// TODO: Clear up excess old data				
			}
		}

	}
	final JPanel loggers = new JPanel();
	final JTextArea textArea = new JTextArea();

	@Override
	public void execute()
	{
		TreeMap<String, LoggerPanel> sortingTree = new TreeMap<String, LoggerPanel>(Collator.getInstance());

		LogManager root =	java.util.logging.LogManager.getLogManager();
		for (Enumeration<String> it = root.getLoggerNames(); it.hasMoreElements();)
		{
			String name = it.nextElement();

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
		}

		loggers.removeAll();
		for (String it : sortingTree.keySet())
		{
			loggers.add(sortingTree.get(it));
		}
	}

	@Override
	public void initialise(Simulation sim)
	{
		loggers.setLayout(new BoxLayout(loggers, BoxLayout.PAGE_AXIS));
		textArea.setEditable(false);
		//textArea.setRows(10000);

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
