/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ise.gameoflife.plugins;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import ise.gameoflife.environment.Environment;
import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.groups.PoliticalGroup;
import ise.gameoflife.models.Tuple;
import ise.gameoflife.participants.PublicGroupDataModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import presage.Plugin;
import presage.Simulation;

/**
 *
 * @author Aadil
 */
public class LoansInfoPolitics extends JPanel implements Plugin {
/**
* Class that creates a syncronised, type safe list that can be used with
* a JList, and allow the list to be updated after creation.
*/
    // Set of political participants that are active
    private TreeMap<String, PoliticalGroup> p_players = new TreeMap<String, PoliticalGroup>();
    
    double currentReserve = 0 ;
    int rounds= 0 ;

   private final class JListModel implements ListModel, List<String>
   {
        private final ArrayList<String> data;
        private final ArrayList<ListDataListener> listeners;

     JListModel()
     {
       this(new ArrayList<String>());
     }

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
     synchronized public boolean addAll(int index, Collection<? extends String> c)
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

   private final static long serialVersionUID =   1L;
   private final static String label = "Loans Log";
   private Simulation sim;
   private Environment en;
   private final JListModel data = new JListModel();
   /**
    * Default constructor   does nothing.
    */
   public LoansInfoPolitics()
   {
     super(new BorderLayout());
   }

   /**
    * Returns the Label
    * @return The label
    */

   @Override
   public String getLabel()
   {
     return label;
   }

   /**
    * Returns the Short Label
    * @return The short label
    */

   @Override
   public String getShortLabel()
   {
     return label;
   }

   /**
    * Creates a new instance of Loans Log called during simulation
    * @param sim
    */
   @Override
   public void initialise(Simulation sim)
   {
     this.sim = sim;
     this.en = (Environment)sim.environment;
     this.add(new JScrollPane(new JList(data)));
     setBackground(Color.LIGHT_GRAY);
   }
   
   /**
    * Marks the beginning of a new cycle
    */
   @Override
   public void execute()
   {
       SortedSet<String> active_agent_ids = sim.getactiveParticipantIdSet("group");
       Iterator<String> iter = active_agent_ids.iterator();
       String name;
       updateLoanPlayers(active_agent_ids, iter);
       
       if (en.getRoundsPassed() == rounds)
       {
            data.add(" ==== Cycle " + sim.getTime() + " Begins (" + en.getRoundsPassed() + ':' + en.getCurrentTurnType() + ") ==== ");
            data.add("************************************************************************************************************ ");
            for(Map.Entry<String,PoliticalGroup> entry : p_players.entrySet())
            {
                while (iter.hasNext())
                {
                    String id = iter.next();
                    if (p_players.get(id) != null)
                    {
                        name = p_players.get(id).getDataModel().getName();
                        data.add("==============================="+name+"===============================");
                        data.add("Group population: " + p_players.get(id).getDataModel().getMemberList().size());
                        data.add("Current reserve: " + p_players.get(id).getDataModel().getCurrentReservedFood());
                        data.add("Greediness: " + PoliticalGroup.getGreediness(p_players.get(id).getDataModel()));
                        data.add("++++++LOAN HISTORY OF THIS GROUP++++++");

                        //Display debtors / loans given
                        data.add("Debtors History-------------------------------");
                        Map<String, List<Tuple<Double, Double>>> loansGiven = PoliticalGroup.getLoansGiven(p_players.get(id).getDataModel());
                        if (loansGiven!= null)
                        {
                            Set<String> debtors = loansGiven.keySet();
                            for (String deb: debtors)
                            {
                                String gname;
                                if (PublicEnvironmentConnection.getInstance().getGroupById(deb) == null)
                                {
                                    gname = "A DEAD GROUP";
                                }
                                else
                                {
                                    gname = PublicEnvironmentConnection.getInstance().getGroupById(deb).getName();
                                }
                                data.add("---->Debtor: " + gname+" has been given: " + loansGiven.get(deb).size()+" loans from this group!");
                                double amountBorrowed = 0;
                                for (Tuple<Double, Double> t: loansGiven.get(deb))
                                {
                                    amountBorrowed += t.getKey()*(1+t.getValue());
                                }
                                data.add("          This debtor has borrowed " + amountBorrowed + " units of food");
                            }
                        }
                        else
                        {
                            data.add("No loans given at the moment!");
                        }
                        
                        data.add("Creditors History-------------------------------");
                        //Display debtors / loans given
                        Map<String, List<Tuple<Double, Double>>> loansTaken = PoliticalGroup.getLoansTaken(p_players.get(id).getDataModel());
                        if (loansTaken!= null)
                        { 
                            Set<String> creditors = loansTaken.keySet();
                            for (String cred: creditors)
                            {
                                String gname;
                                if (PublicEnvironmentConnection.getInstance().getGroupById(cred) == null)
                                {
                                    gname = "A DEAD GROUP";
                                }
                                else
                                {
                                    gname = PublicEnvironmentConnection.getInstance().getGroupById(cred).getName();
                                }
                                data.add("---->Creditor: " + gname+" has given this group: " + loansTaken.get(cred).size()+" loans!");
                                double amountBorrowed = 0;
                                for (Tuple<Double, Double> t: loansTaken.get(cred))
                                {
                                    amountBorrowed += t.getKey()*(1+t.getValue());
                                }
                                data.add("          This group has been given " + amountBorrowed + " units of food from this creditor");
                            }
                        }
                        else
                        {
                            data.add("No loans taken at the moment!");
                        }
                    }
                }
            data.add("        ");
            iter = active_agent_ids.iterator();
        }
        rounds++;
    }
    }
   /**
    * Deals with plugin upon plugin deletion
    * @deprecated
    */
   @Deprecated
   @Override
   public void onDelete()
   {
   }

   @Override
   public void onSimulationComplete()
   {
       data.add("==========================SUMMARY==========================");
       data.add("Groups final score (food reserve)");
       data.add("------------------------------------------------");
       data.add("Group Name | Current reserve \t| Amount given \t| Amount borrowed \t| Average Happiness \t| Greediness" );
       SortedSet<String> active_agent_ids = sim.getactiveParticipantIdSet("group");
       PublicEnvironmentConnection ec = PublicEnvironmentConnection.getInstance();       

       //get rid of special group
       String SpecialID = "";
       for(String group : active_agent_ids)
            if(ec.getGroupById(group).getName().equals("Group #2"))
               SpecialID = group;
       active_agent_ids.remove(SpecialID);
       
       Iterator<String> iter = active_agent_ids.iterator();
       while(iter.hasNext())
       {
            String groupID = iter.next();
            PublicGroupDataModel dm = ec.getGroupById(groupID);
            Map<String, List<Tuple<Double, Double> > > loansGiven = PoliticalGroup.getLoansGiven(dm);
            double totalAmountGiven = 0;
            if (loansGiven != null)
            {
                for (String debtors: loansGiven.keySet())
                {
                    //if (ec.getGroupById(debtors) == null) break;
                    double amountBorrowed = 0;
                    for (Tuple<Double, Double> t: loansGiven.get(debtors))
                    {
                        amountBorrowed += t.getKey()*(1+t.getValue());
                    }
                    totalAmountGiven += amountBorrowed;
                }
           }
           Map<String, List<Tuple<Double, Double> > > loansTaken = PoliticalGroup.getLoansTaken(dm);
           double totalAmountTaken = 0;
           if (loansTaken != null)
           {
                for (String creditors: loansTaken.keySet())
                {
                    //if (ec.getGroupById(creditors) == null) break;
                    double amountBorrowed = 0;
                    for (Tuple<Double, Double> t: loansTaken.get(creditors))
                    {
                        amountBorrowed += t.getKey()*(1+t.getValue());
                    }
                    totalAmountTaken += amountBorrowed;
                }
           }
           double averageHappiness = 0;
           for(String member: dm.getMemberList())
           {
               averageHappiness += ec.getAgentById(member).getCurrentHappiness();
           }
           averageHappiness = averageHappiness/ dm.getMemberList().size();
           String spaces = "                               ";
           data.add(dm.getName()+
                   spaces+Math.round(dm.getCurrentReservedFood())+
                   spaces+Math.round(totalAmountGiven) +
                   spaces + Math.round(totalAmountTaken)+
                   spaces + averageHappiness+
                   spaces + PoliticalGroup.getGreediness(dm));
       }
       data.add("*Note: Amounts given and amounts borrowed may not balance because dead groups are not displayed in this summary");
   }

     private void updateLoanPlayers(SortedSet<String> active_agent_ids, Iterator<String> itera)
     {
            PublicEnvironmentConnection ec = PublicEnvironmentConnection.getInstance();
            // Add any new groups
            while(itera.hasNext())
            {
                String id = itera.next();
                if(!p_players.containsKey(id))
                {
                    if(!sim.getPlayer(id).getClass().equals(ec.getAllowedGroupTypes().get(1)))
                    {
                      p_players.put(id, (PoliticalGroup) sim.getPlayer(id));
                      
                    }
                }
            }

           // Delete agents which are no longer active
            List<String> ids_to_remove = new LinkedList<String>();
            for(Map.Entry<String, PoliticalGroup> entry : p_players.entrySet())
            {
                    String id = entry.getKey();
                    if(!active_agent_ids.contains(id))
                    {
                            ids_to_remove.add(id);
                    }
            }
            itera = ids_to_remove.iterator();
            while(itera.hasNext())
           {
                    p_players.remove(itera.next());
           }
    }    
}
