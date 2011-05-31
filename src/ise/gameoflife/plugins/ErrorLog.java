package ise.gameoflife.plugins;

import ise.gameoflife.enviroment.Environment;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import presage.Plugin;
import presage.Simulation;

/**
 * Creates the JPanel Plugin which will be used to log errors
 * TODO: Finish documentation
 * TODO: Add option to write the log to a file at end of simulation
 * @author Olly Hill
 * @author Benedict Harcourt
 */
public class ErrorLog extends JPanel implements Plugin
{

	/**
	 * TODO: Make all of these functions synchronised
	 * TODO: Document each of these functions
	 * TODO: Call inform listeners whenever dataset changes
	 * TODO: Less shite implementation of this class
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
		private void informListeners()
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
		public boolean add(String e)
		{
			informListeners();
			return data.add(e);
		}

		@Override
		public void add(int index, String element)
		{
			data.add(index, element);
		}

		@Override
		public boolean addAll(Collection<? extends String> c)
		{
			return data.addAll(c);
		}

		@Override
		public boolean addAll(int index,
						Collection<? extends String> c)
		{
			return data.addAll(index, c);
		}

		@Override
		public void addListDataListener(ListDataListener l)
		{
			listeners.add(l);
		}

		@Override
		public int getSize()
		{
			return data.size();
		}

		@Override
		public Object getElementAt(int index)
		{
			return data.get(index);
		}

		@Override
		public void removeListDataListener(ListDataListener l)
		{
			listeners.remove(l);
		}

		@Override
		public int size()
		{
			return data.size();
		}

		@Override
		public boolean isEmpty()
		{
			return data.isEmpty();
		}

		@Override
		public boolean contains(Object o)
		{
			return data.contains((String)o);
		}

		@Override
		public Iterator<String> iterator()
		{
			return data.iterator();
		}

		@Override
		public Object[] toArray()
		{
			return data.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a)
		{
			return data.toArray(a);
		}

		@Override
		public boolean remove(Object o)
		{
			return data.remove((String)o);
		}

		@Override
		public boolean containsAll(Collection<?> c)
		{
			return data.containsAll(c);
		}

		@Override
		public boolean removeAll(Collection<?> c)
		{
			return data.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c)
		{
			return data.retainAll(c);
		}

		@Override
		public void clear()
		{
			data.clear();
		}

		@Override
		public String get(int index)
		{
			return data.get(index);
		}

		@Override
		public String set(int index, String element)
		{
			return data.set(index, element);
		}

		@Override
		public String remove(int index)
		{
			return data.remove(index);
		}

		@Override
		public int indexOf(Object o)
		{
			return data.indexOf(o);
		}

		@Override
		public int lastIndexOf(Object o)
		{
			return data.lastIndexOf((String)o);
		}

		@Override
		public ListIterator<String> listIterator()
		{
			return data.listIterator();
		}

		@Override
		public ListIterator<String> listIterator(int index)
		{
			return data.listIterator(index);
		}

		@Override
		public List<String> subList(int fromIndex, int toIndex)
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
		this.add(new JList(data));

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
	 */
	@Override
	public void onSimulationComplete()
	{
		data.add(" ==== Simulation Ended ==== ");
	}

}