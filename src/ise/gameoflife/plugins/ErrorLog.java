package ise.gameoflife.plugins;

import ise.gameoflife.enviroment.Environment;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import presage.Plugin;
import presage.Simulation;

/**
 * Creates the JPanel Plugin which will be used to log errors
 * TODO: Finish documentation
 * TODO: Add option to write the log to a specific file at end of simulation
 * @author Christopher Fonseka
 * @author Olly Hill
 * @author Benedict Harcourt
 */
public class ErrorLog extends JPanel implements Plugin
{

	/**
	 * 
	 * TODO: Document each of these functions
	 * TODO: Less s****e implementation of this class
	 */
	private final class JListModel implements ListModel, List<String>
	{
		private final ArrayList<String> data;
		private final ArrayList<ListDataListener> listeners;

		JListModel()
		{
			this(new ArrayList<String>());
		}

		/**
		 * TODO: Spread this out to be better implementation
		 */
		synchronized private void informListeners()
		{
			for (ListDataListener l : listeners)
			{
				l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, this.getSize()));
			}
		}

		JListModel(Collection<String> data)
		{
			this.data = new ArrayList<String>(data);
			this.listeners = new ArrayList<ListDataListener>();
		}

		@Override
		synchronized public boolean add(String e)
		{
			informListeners();
			return data.add(e);
		}

		@Override
		synchronized public void add(int index, String element)
		{
			informListeners();
			data.add(index, element);
		}

		@Override
		synchronized public boolean addAll(Collection<? extends String> c)
		{
			informListeners();
			return data.addAll(c);
		}

		@Override
		synchronized public boolean addAll(int index,
						Collection<? extends String> c)
		{
			informListeners();
			return data.addAll(index, c);
		}

		@Override
		synchronized public void addListDataListener(ListDataListener l)
		{
			listeners.add(l);
		}

		@Override
		synchronized public int getSize()
		{
			return data.size();
		}

		@Override
		synchronized public Object getElementAt(int index)
		{
			return data.get(index);
		}

		@Override
		synchronized public void removeListDataListener(ListDataListener l)
		{
			listeners.remove(l);
		}

		@Override
		synchronized public int size()
		{
			return data.size();
		}

		@Override
		synchronized public boolean isEmpty()
		{
			return data.isEmpty();
		}

		@Override
		synchronized public boolean contains(Object o)
		{
			return data.contains((String)o);
		}

		@Override
		synchronized public Iterator<String> iterator()
		{
			return data.iterator();
		}

		@Override
		synchronized public Object[] toArray()
		{
			return data.toArray();
		}

		@Override
		synchronized public <T> T[] toArray(T[] a)
		{
			return data.toArray(a);
		}

		@Override
		synchronized public boolean remove(Object o)
		{
			informListeners();
			return data.remove((String)o);
		}

		@Override
		synchronized public boolean containsAll(Collection<?> c)
		{
			return data.containsAll(c);
		}

		@Override
		synchronized public boolean removeAll(Collection<?> c)
		{
			informListeners();
			return data.removeAll(c);
		}

		@Override
		synchronized public boolean retainAll(Collection<?> c)
		{
			return data.retainAll(c);
		}

		@Override
		synchronized public void clear()
		{
			informListeners();
			data.clear();
		}

		@Override
		synchronized public String get(int index)
		{
			return data.get(index);
		}

		@Override
		synchronized public String set(int index, String element)
		{
			return data.set(index, element);
		}

		@Override
		synchronized public String remove(int index)
		{
			informListeners();
			return data.remove(index);
		}

		@Override
		synchronized public int indexOf(Object o)
		{
			return data.indexOf(o);
		}

		@Override
		synchronized public int lastIndexOf(Object o)
		{
			return data.lastIndexOf((String)o);
		}

		@Override
		synchronized public ListIterator<String> listIterator()
		{
			return data.listIterator();
		}

		@Override
		synchronized public ListIterator<String> listIterator(int index)
		{
			return data.listIterator(index);
		}

		@Override
		synchronized public List<String> subList(int fromIndex, int toIndex)
		{
			return data.subList(fromIndex, toIndex);
		}
	}

	private final static long serialVersionUID = 1L;
	private final static String label = "Error Log";

	private Simulation sim;
	private final JListModel data = new JListModel();

	/**
	 * Default constructor - does nothing.
	 */
	public ErrorLog()
	{
		super(new BorderLayout());
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
		this.add(new JScrollPane(new JList(data)));

		((Environment)sim.environment).setErrorLog(data);

		setBackground(Color.LIGHT_GRAY);
	}

	/**
	 * TODO: Documentation
	 */
	@Override
	public void execute()
	{
		data.add(" ==== Cycle " + sim.getTime() + " Begins ==== ");
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
	 * by writing a completion banner and outputting
	 * the log to a file called ErrorLog.txt
	 */
	@Override
	public void onSimulationComplete()
	{
		data.add(" ==== Simulation Ended ==== ");
		
		try
		{
			String configPath = new File(System.getProperty("user.dir"), "simulations").getAbsolutePath();
			FileWriter fstream = new FileWriter(configPath + "/ErrorLog.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			final String newline = System.getProperty("line.separator");
			
			for (String s : data)	out.write(s + newline);

			out.close();
		}
		catch (Exception e)
		{
			System.err.println("ErrorLog Making Error: " + e.getMessage());
		}
	}

}