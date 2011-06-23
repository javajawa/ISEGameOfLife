/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.gameoflife.plugins;

import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.groups.LoansGroup;
import ise.gameoflife.models.Tuple;
import ise.gameoflife.participants.PublicGroupDataModel;
import ise.gameoflife.tokens.TurnType;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import presage.Plugin;
import presage.Simulation;
/**
 *
 * @author george
 */
public class LoansPlugin extends JPanel implements Plugin
{
	private static final long serialVersionUID = 1L;

	private class GroupPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		private final PublicGroupDataModel gm;

		private JLabel labelise(String s)
		{
			JLabel ret = new JLabel(s);
			ret.setHorizontalAlignment(SwingConstants.CENTER);
			ret.setFont(ret.getFont().deriveFont(6));
			return ret;
		}
                private JLabel labelise(String s,int font)
		{
			JLabel ret = new JLabel(s);
			ret.setHorizontalAlignment(SwingConstants.CENTER);
			ret.setFont(ret.getFont().deriveFont(font));
			return ret;
		}

		@SuppressWarnings("LeakingThisInConstructor")
		GroupPanel(PublicGroupDataModel gm)
		{
                        this.gm = gm;
                        double happiness = 0;
                        double reserve = 0;
                        double size = this.gm.getMemberList().size();
                        
                        reserve = PublicEnvironmentConnection.getInstance().getGroupById(gm.getId()).getCurrentReservedFood();                       

                        for (String member: gm.getMemberList())
                        {
                            happiness += PublicEnvironmentConnection.getInstance().getAgentById(member).getCurrentHappiness();
                        }
                        happiness = happiness / size;
                        Map<String, List<Tuple<Double, Double> > > loansGivenByThisGroup = LoansGroup.getLoansGiven(gm);

                        JPanel dataPanel = new JPanel(new GridLayout(4, 2, 1, -1));

                        dataPanel.add(labelise(this.gm.getName(),8));
			dataPanel.add(labelise("Population: "+ this.gm.getMemberList().size()));
			dataPanel.add(labelise("Average Happiness: "+happiness));
                        dataPanel.add(labelise("Current reserve: "+reserve));
                        if(loansGivenByThisGroup != null)
                        {
                            dataPanel.add(labelise("I gave loans to :"));
                            for (String debtor: loansGivenByThisGroup.keySet())
                            {
                                dataPanel.add(labelise(PublicEnvironmentConnection.getInstance().getGroupById(debtor).getName()));
                            }
                            
                        }
                        

                        this.setLayout(new GridLayout(1,1));
			this.add(dataPanel);
			this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			window.add(this);
			this.setPreferredSize(new Dimension(getWidth() - barWidth, 125));
		}

		void updateData()
		{

                }
	}

	private final static String label = "Loan info";

	private Simulation sim;
	private PublicEnvironmentConnection ec = null;

	private final JPanel window = new JPanel();
	private final JScrollPane pane = new JScrollPane(window, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	//private final HashMap<String, GroupPanel> panels = new HashMap<String, GroupPanel>();
        private final TreeMap<String, GroupPanel> panels = new TreeMap<String, GroupPanel>();
	private int barWidth;
	private int graphHeight = 50;

	public LoansPlugin()
	{
		// Nothing to see here. Move along, citizen!
	}

	@Override
	public void execute()
	{
              if (ec == null) ec = PublicEnvironmentConnection.getInstance();
              if (ec.getCurrentTurnType() != TurnType.firstTurn) return;

              barWidth = this.pane.getVerticalScrollBar().getWidth();
              panels.clear();
              this.window.removeAll();
		for (String aid : ec.availableGroups())
		{       if (!panels.containsKey(aid) && ec.getGroupById(aid).getMemberList().size() > 0 )
			{
				panels.put(aid, new GroupPanel(ec.getGroupById(aid)));

                                panels.get(aid).updateData();
                        }
		}
		validate();
                this.repaint();
	}

	@Override
	public void initialise(Simulation sim)
	{
		setLayout(new BorderLayout());
		this.sim = sim;
		window.setLayout(new BoxLayout(window, BoxLayout.PAGE_AXIS));
		this.add(pane);
	}

	@Deprecated
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
		return label;
	}

	@Override
	public String getShortLabel()
	{
		return label;
	}

}
