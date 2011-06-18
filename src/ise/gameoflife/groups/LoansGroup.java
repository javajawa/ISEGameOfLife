/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.gameoflife.groups;

import ise.gameoflife.inputs.LeaveNotification.Reasons;
import ise.gameoflife.models.GroupDataInitialiser;
import ise.gameoflife.models.History;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.models.Tuple;
import ise.gameoflife.participants.AbstractGroupAgent;
import ise.gameoflife.tokens.AgentType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author george
 */
public class LoansGroup extends AbstractGroupAgent {
    private static final long serialVersionUID = 1L;
    //TODO: 1) Add an abstract inspectOtherGroups() in GroupDataModel. Concrete implementation here.
    // *What if we use public static methods for the histories to keep the framework intact?

    //The following structures store the group ID of the group that gave or took a loan. The tuple stores
    //the amount borrowed and  the interest rate
    private static final double achievementThreshold = 1000;//the goal that any group is trying to achieve (it can be thought as the group's progression to a new age)
    private History< HashMap<String, Tuple<Double, Double> > > loansGiven = new History< HashMap<String, Tuple<Double, Double> > >(50);
    private History< HashMap<String, Tuple<Double, Double> > > loansTaken = new History< HashMap<String, Tuple<Double, Double> > >(50);
    
    @Deprecated
    public LoansGroup() {
    	super();
    }

    public LoansGroup(GroupDataInitialiser dm) {
	super(dm);
    }

    @Override
    protected void onActivate() {
        //Do nothing!
    }

    @Override
    protected boolean respondToJoinRequest(String playerID) {
        //To keep it simple always accept agents no matter what 
        return true;
    }

    private Comparator<String> c = new Comparator<String>() {
            private Random r = new Random(0);
            @Override
            public int compare(String o1, String o2)
            {
                    return (r.nextBoolean() ? -1 : 1);
            }
    };

    @Override
    public List<HuntingTeam> selectTeams()
    {
            ArrayList<HuntingTeam> teams = new ArrayList <HuntingTeam>();
            List<String> members = new ArrayList<String>(getDataModel().getMemberList());
            Collections.sort(members, c);
            int agents = members.size();

            for(int i=0; i < agents; i += 2){
                    int ubound = (i + 2 >= agents) ? agents : i + 2;
                    teams.add(new HuntingTeam(members.subList(i, ubound)));
        }

            return teams;
    }

    @Override
    protected void onMemberLeave(String playerID, Reasons reason) {
        //TODO: Reuse code from TestPoliticalGroup but it doesn't really matter because we don't care about politics
    }
    
    @Override
    protected void beforeNewRound() {
        //TODO: Reuse code from TestPoliticalGroup
    }    

    @Override
    protected AgentType decideGroupStrategy() {
        //Check if this group has leader/leaders. If leaders have not emerge yet then no decision at all
        List<String> currentPanel = getDataModel().getPanel();
        int population = getDataModel().getMemberList().size();

        if (currentPanel.isEmpty()||(population == 1))  return null;

        List<Tuple<AgentType, Double> > followersTypeCounterList = new LinkedList<Tuple<AgentType, Double> >();
        List<Tuple<AgentType, Double> > panelTypeCounterList = new LinkedList<Tuple<AgentType, Double> >();

        //We get lists containing panel's and followers' preferences in strategies in descending order
        followersTypeCounterList = getStrategyPreferences(getDataModel().getMemberList());
        panelTypeCounterList = getStrategyPreferences(currentPanel);

        //Calculate the quotum. It is the number of supporters needed to pass a proposal. In this case proposal
        //is the strategy of the group. The quotum is a function of the social belief of the group
        double quotum = (population * getDataModel().getEstimatedSocialLocation())/population;

        //Start with the most prefereed strategy of the panel (the strategy that the leader/leaders wish to follow
        //If this strategy is supported by a high enough number of followers (quotum) then we pick this strategy
        //Otherwise try the next best strategy. The lower the quotum the less easy is to get your proposal accepted
        //This is the case of dictatorship.
        Iterator<Tuple<AgentType, Double> > i = panelTypeCounterList.iterator();
        while(i.hasNext())
        {
            int n = 0;
            Tuple<AgentType, Double> panelPreference = i.next();
            while (panelPreference.getKey() != followersTypeCounterList.get(n).getKey())
            {
                n++;
            }
            double followerSupport = followersTypeCounterList.get(n).getValue();
            if (followerSupport >= quotum)
            {
                return panelPreference.getKey();
            }
        }
        //If we have reached this statement then we have not found a well suported strategy probably because the
        //quotum is very high (bottom of y axis - anarchism), so the group does not get to play the hunting game
        //other groups
        return null;
    }
    
    private List<Tuple<AgentType, Double>> getStrategyPreferences(List<String> agents) {
        
        int population = agents.size();

        Tuple<AgentType, Double> tftTypes = new Tuple<AgentType, Double>(AgentType.TFT, 0.0);
        Tuple<AgentType, Double> acTypes = new Tuple<AgentType, Double>(AgentType.AC, 0.0);
        Tuple<AgentType, Double> adTypes = new Tuple<AgentType, Double>(AgentType.AD, 0.0);
        Tuple<AgentType, Double> rTypes = new Tuple<AgentType, Double>(AgentType.R, 0.0);

        //Count types in agents list
        for (String agentID : agents)
        {
            switch(getConn().getAgentById(agentID).getAgentType())
            {
                case AC:
                    double oldCountAC = acTypes.getValue();
                    acTypes.setValue(oldCountAC+1);
                    break;
                case AD:
                    double oldCountAD = adTypes.getValue();
                    adTypes.setValue(oldCountAD+1);
                    break;
                case TFT:
                    double oldCountTFT = tftTypes.getValue();
                    tftTypes.setValue(oldCountTFT+1);
                    break;
                case R:
                    double oldCountR = rTypes.getValue();
                    rTypes.setValue(oldCountR+1);
                    break;
            }
        }

        //Find the average of each type
        acTypes.setValue(acTypes.getValue()/population);
        adTypes.setValue(adTypes.getValue()/population);
        tftTypes.setValue(tftTypes.getValue()/population);
        rTypes.setValue(rTypes.getValue()/population);

        List< Tuple<AgentType, Double> > preferencesRatioList = new LinkedList<Tuple<AgentType, Double> >();

        //Add the ratios to the list
        preferencesRatioList.add(acTypes);
        preferencesRatioList.add(adTypes);
        preferencesRatioList.add(tftTypes);
        preferencesRatioList.add(rTypes);

        //Sort the preferred startegies in descending order
        Collections.sort(preferencesRatioList, preferencesComparator);

        return preferencesRatioList;
    }
    
    private Comparator< Tuple<AgentType, Double> > preferencesComparator = new Comparator< Tuple<AgentType, Double> >() {
        @Override
        public int compare(Tuple<AgentType, Double> o1, Tuple<AgentType, Double> o2)
        {
            Double v1 = o1.getValue();
            Double v2 = o2.getValue();
            return (v1>v2 ? -1 : 1);
        }
    };
    
    @Override
    protected Tuple<AgentType, Double> makePayments()
    {
        //TODO: The panel should make a decision. This decision will determine how much food we will spend this round/
        //The amount spent is taken from the reserve pool. Two possibilities: Either groups go hunting and spend energy = food
        //or spend money for public service (build roads, schools, big pointless sculptures etc)
        double currentFoodReserve = getDataModel().getCurrentReservedFood();
        AgentType strategy = getDataModel().getGroupStrategy();
        
        if (!loansTaken.isEmpty())
        {
            //TODO: Repay loans (if possible)
        }

        //Spend money       
        //We want to take away a percentage of the reserve
        double percentDecrease = 0;

        if(theMoneyIsOK(currentFoodReserve))
        {
             if(strategy != null)
            {
                percentDecrease = 0.3;//30% of the reserve will be payment to play the game
                currentFoodReserve -= percentDecrease * currentFoodReserve;
            }  
             
            if(theMoneyIsOK(currentFoodReserve))//if its still okay then make your citizens happy
            {
                //check the happiness of the citizens
                double deltaHappiness = getAverageHappiness(0) - getAverageHappiness(1);
                if(deltaHappiness < 0)
                {
                    //check how close you are to attaining achievement
                    double goalRatio = currentFoodReserve / achievementThreshold;
                    //make your citizens happy by funding public service but dont spend too much
                    //if you're far away from attaining achievement
                    percentDecrease = Math.abs(deltaHappiness) * goalRatio;
                }
                currentFoodReserve -= percentDecrease * currentFoodReserve;                
            }
            return new Tuple<AgentType, Double>(strategy, currentFoodReserve);
        }            
        else 
        {
            //you dont have enough to spend for anything, including to play the hunting game
            return new Tuple<AgentType, Double>(null, currentFoodReserve);
        }        
    }
    
    private boolean theMoneyIsOK(double mostRecentReserve)
    {
        //need to assess the reserve, your loan history and the wealth of the people!?
        
//        double deltaFoodReserve;
//        if(getDataModel().getReservedFood().size() < 2)
//            deltaFoodReserve = mostRecentReserve;
//        else
//            deltaFoodReserve = mostRecentReserve - getDataModel().getReservedFood().getValue(1); 
        
        return true;
    }
    
    private double getAverageHappiness(int turnsAgo)
    {
        double average = 0;
        if(!getDataModel().getMemberList().isEmpty())
        {
            Double happiness;
            for(String member : getDataModel().getMemberList())
            {
                if(turnsAgo > 0)
                {
                    happiness = getConn().getAgentById(member).getCurrentHappiness();
                    if(happiness != null)//otherwise add nothing
                    {
                        average += happiness;                   
                    }
                }
                else
                {
                    happiness = getConn().getAgentById(member).getHappinessHistory().getValue(turnsAgo);
                    if(happiness != null)//otherwise add nothing
                    {
                        average += happiness;                   
                    }                    
                }
            }
            average = average / getDataModel().getMemberList().size();
        }
        return average;
    }

    @Override
    protected double decideTaxForReservePool() {
        double currentFoodReserve;        
        if(getDataModel().getReservedFood().isEmpty())
            currentFoodReserve = 0;
        else
            currentFoodReserve = getDataModel().getCurrentReservedFood();
                            
        double tax = 0;
        double deltaHappiness = getAverageHappiness(0) - getAverageHappiness(1);       
        //check how close you are to attaining achievement
        double goalRatio = currentFoodReserve / achievementThreshold;
            
        if(theMoneyIsOK(currentFoodReserve))
        {
            //tax as normal
            //check the happiness of the citizens
            if(deltaHappiness < 0)
            {
                //tax your citizens, but not too much becasue they are unhappy
                //lower the tax significantly if closer to achievement
                tax = Math.abs(deltaHappiness) * (1 - goalRatio);
            }
            else
            {
                //everyone is happy, the group's money is ok, concentrate on getting achievement
                tax = 1 - goalRatio;//if you're far away from achievement then tax high
            }
        }
        else
        {
            //otherwise, you're in trouble and you have to tax high
            tax = 1 - goalRatio;//since you're far away from your achievement, tax high            
        }
        return tax;
    }
}
