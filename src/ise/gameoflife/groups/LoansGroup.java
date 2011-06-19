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
import ise.gameoflife.participants.PublicGroupDataModel;
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
import java.util.TreeSet;
import ise.gameoflife.tokens.InteractionResult;
import java.util.Set;

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
    private static final double priceToPlay = 100;
    private int reserveTrend;    
    private static final double achievementThreshold = 1000;//the goal that any group is trying to achieve (it can be thought as the group's progression to a new age)
    private Map<String, Tuple<Double, Double> > loansGiven = new HashMap<String, Tuple<Double, Double> >();
    private Map<String, Tuple<Double, Double> > loansTaken = new HashMap<String, Tuple<Double, Double> >();
    private static Map<String, Double> inNeed = new HashMap<String, Double>();
    private static Map<String, HashMap<String, Tuple<Double, Double> >> loanRequestsAccepted = new HashMap<String, HashMap<String, Tuple<Double, Double> >>();

    @Deprecated
    public LoansGroup() {
    	super();
    }

    public LoansGroup(GroupDataInitialiser dm) {
	super(dm);
        reserveTrend = 0;
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
        double currentFoodReserve;

        if(getDataModel().getReservedFoodHistory().isEmpty())
        {
            currentFoodReserve = 0;
        }
        else
        {
            currentFoodReserve = getDataModel().getCurrentReservedFood();        
        }

        AgentType strategy = getDataModel().getGroupStrategy();
        
        if (!loansTaken.isEmpty())
        {
            //TODO: Repay loans (if possible)
        }

        //Spend money       
        if(strategy != null)
        {
            currentFoodReserve -= priceToPlay;//pay, in theory, to join the game among groups
            if(currentFoodReserve < priceToPlay)//if the theory is way to risky
            {
                currentFoodReserve += priceToPlay;//go back
                strategy = null;//and sit this turn out
            }
        }

        //check the happiness of the citizens
        double deltaHappiness = getAverageHappiness(0) - getAverageHappiness(1);
        double goalRatio = currentFoodReserve / achievementThreshold;//check how close you are to attaining achievement
        double percentDecrease;
        if(deltaHappiness < 0)
        {
            //make your citizens happy by funding public service but dont spend too much
            //if you're far away from attaining achievement
            percentDecrease = (deltaHappiness * goalRatio) * currentFoodReserve;
            currentFoodReserve += percentDecrease;
            if(currentFoodReserve < priceToPlay)//if helping the public cant be done at the moment
            {
                currentFoodReserve -= percentDecrease;//go back
            }            
        }
        else
        {
            //make your citizens satisfied by funding public service but dont spend too much
            //if you're far away from attaining achievement

            percentDecrease = (getAverageHappiness(0) * goalRatio) * currentFoodReserve;
            currentFoodReserve -= percentDecrease;
            if(currentFoodReserve < priceToPlay)//if helping the public cant be done at the moment
            {
                currentFoodReserve += percentDecrease;//go back
            }              
        }

        isTheMoneyOK(currentFoodReserve);
        
        return new Tuple<AgentType, Double>(strategy, currentFoodReserve);            
    }
    
    private void isTheMoneyOK(double mostRecentReserve)
    {
        //need to assess the reserve, your loan history and the wealth of the people!?

        //is the reserve increasing or decreasing        
        double deltaFoodReserve;
        if(getDataModel().getReservedFoodHistory().getValue() == null)
        {
            deltaFoodReserve = mostRecentReserve;
        }
        else
        {
            deltaFoodReserve = mostRecentReserve - getDataModel().getReservedFoodHistory().getValue();
        }

        //3 negatives and you're close to playing one last game with the other groups
        //then you have to declare you need help
        if((deltaFoodReserve < 0)&&(mostRecentReserve < 150) && (!inNeed.containsKey(this.getId())))
        {
            inNeed.put(this.getId(), mostRecentReserve); 
        }
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
        if(getDataModel().getReservedFoodHistory().isEmpty())
            currentFoodReserve = 0;
        else
            currentFoodReserve = getDataModel().getCurrentReservedFood();
                            
        double tax = 0;

        double deltaHappiness = getAverageHappiness(0) - getAverageHappiness(1);       
        //check how close you are to attaining achievement
        double goalRatio = currentFoodReserve / achievementThreshold;

        //if you're in trouble you need to tax high
        if(reserveTrend < -3 && currentFoodReserve < 150)
        {
            tax = 1 - goalRatio;//since you're far away from your achievement, tax high 
        }
        else
        {
            //check the happiness of the citizens
            if(deltaHappiness < 0)
            {
                //tax your citizens, but not too much because they are unhappy
                //lower the tax significantly if closer to achievement
                tax = Math.abs(deltaHappiness) * (1 - goalRatio);
            }
            else
            {
                //everyone is happy, the group's money is ok, concentrate on getting achievement
                tax = 1 - goalRatio;//the farther away from achievement the higher the tax
            }            
        }
        return tax;
    }

    @Override
    protected Tuple<InteractionResult, Double> interactWithOtherGroups() {

        double currentFoodReserve = getDataModel().getCurrentReservedFood();
        Tuple<InteractionResult, Double> interactionResult = new Tuple<InteractionResult, Double>();
        
        //FOR DEBUGGING ONLY
//        System.out.println("------------------");
//        System.out.println(this.getDataModel().getName());
//        System.out.println("Current reserved food: "+this.getDataModel().getCurrentReservedFood());
        //FOR DEBUGGING ONLY END

        //First check your financial status and if your in need ask for a loan
        if(inNeed.containsKey(this.getId()))
        {
            if (currentFoodReserve > priceToPlay)
            {
                //We are hardworkers and we managed to recover our economic status to good
                inNeed.remove(this.getId());
                System.out.println("We solved our economic problem so we don't want a loan anymore!");
                interactionResult.add(InteractionResult.NothingHappened, 0.0);
            }
            else if (loanRequestsAccepted.containsKey(this.getId()))
            {
                 //If the request for a loan was granted then store the receipt in your records to help with repayments later (Hopefully..)
                 HashMap<String, Tuple<Double, Double> > loanRecord = loanRequestsAccepted.get(this.getId());
                 Set<String> giverID = loanRecord.keySet();
                 this.loansTaken.put(giverID.iterator().next(), loanRecord.get(giverID.iterator().next()));
                 loanRequestsAccepted.remove(this.getId());
                 System.out.println("I have requested a loan and the result is "+ interactionResult.getKey()+ ". I have been given "+ interactionResult.getValue()+ " units of food!");
                 interactionResult.add(InteractionResult.LoanTaken, loanRecord.get(giverID.iterator().next()).getKey());
            }
            else
            {
                interactionResult.add(InteractionResult.NothingHappened, 0.0);
                System.out.println("I have requested a loan and the result is "+ interactionResult.getKey()+ ". I have been given "+ interactionResult.getValue()+ " units of food!");
            }
            
            return interactionResult;
        }
        
        if (!inNeed.isEmpty())
        {
            //FOR DEBUGGING ONLY
            System.out.println("There are "+ inNeed.size()+" some groups in need!");
            for (String s: inNeed.keySet())
            {
                if (getConn().getGroupById(s) != null)
                    System.out.println("    "+ getConn().getGroupById(s).getName()+ " has requested "+inNeed.get(s)+" units of food!");
            }
            //FOR DEBUGGING ONLY END

            for (String groupID: inNeed.keySet() )
            {
                double amountNeeded = inNeed.get(groupID);
                double interestRate = 0.15;
                //TODO: Design a heuristic to decide if group will give a loan
                //For now give a loan if u have the amount needed
                if (currentFoodReserve - amountNeeded >  priceToPlay+50)
                {
                    //Create a tuple containing the amount granted and the interest
                    Tuple<Double, Double> loanInfo = new Tuple<Double, Double>();
                    loanInfo.add(amountNeeded, interestRate);

                    //Then store the loan info along with the requester ID in your records
                    this.loansGiven.put(groupID, loanInfo);

                    //Use the same structure to send a receipt to the requester to store it in his records
                    HashMap<String, Tuple<Double, Double> > loanRecord = new HashMap<String, Tuple<Double, Double> >();
                    loanRecord.put(this.getId(), loanInfo);
                    loanRequestsAccepted.put(groupID, loanRecord);
                    inNeed.remove(groupID);
                    interactionResult.add(InteractionResult.LoanGiven, amountNeeded);
                    System.out.println(getConn().getGroupById(groupID).getName() + " requested a loan and the result is "+ interactionResult.getKey()+ ". I gave them "+ interactionResult.getValue()+ " units of food!");
                    return interactionResult;
                }
            }
        }
        interactionResult.add(InteractionResult.NothingHappened, 0.0);
        System.out.println("No interaction at all!");
        return interactionResult;
    }
    
}

