/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.gameoflife.groups;

import ise.gameoflife.agents.PoliticalAgentGroup;
import ise.gameoflife.environment.PublicEnvironmentConnection;
import ise.gameoflife.inputs.LeaveNotification.Reasons;
import ise.gameoflife.models.GroupDataInitialiser;
import ise.gameoflife.models.HuntingTeam;
import ise.gameoflife.models.Tuple;
import ise.gameoflife.participants.AbstractGroupAgent;
import ise.gameoflife.tokens.AgentType;
import ise.gameoflife.tokens.InteractionResult;
import ise.gameoflife.participants.PublicGroupDataModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.LinkedHashMap;
import java.util.Random;
/**
 *
 * @author The0s
 */
public class PoliticalGroup extends AbstractGroupAgent {
	private static final long serialVersionUID = 1L;
        
        //The fee a group must pay to play the game
        private static final double priceToPlay = 100;
        //This constant defines the maximum score a group can attain during this simulation
        private static final double achievementThreshold = 1000;//the goal that any group is trying to achieve (it can be thought as the group's progression to a new age)

        //The greediness level of this group
        private final double greediness = new Random().nextDouble();

        //This structure stores the ids of the groups that are currently in need and the amount they request
        private static Map<String, Double> inNeed = new HashMap<String, Double>();

        //Stores all the requests that have been accepted
        private static Map<String, HashMap<String, Tuple<Double, Double> >> loanRequestsAccepted = new HashMap<String, HashMap<String, Tuple<Double, Double> >>();

        //The elements of this structure is a signal to a group that someone has repaid for the loan
        private static Map<String, List<Tuple<String, Double> > > loanRepayments = new HashMap<String, List<Tuple<String, Double> > >();

        //The following structures store the loans given and taken by this group along with the information for that loan
        private Map<String, List<Tuple<Double, Double> > > loansGiven = new HashMap<String, List<Tuple<Double, Double> > >();
        private Map<String, List<Tuple<Double, Double> > > loansTaken = new HashMap<String, List<Tuple<Double, Double> > >();
        private Map<String, List<Tuple<Double, Double> > > loansTakenHist = new HashMap<String, List<Tuple<Double, Double> > >();

        //This structures provide public access to some information about this group
        private static Map<String, Map<String, List<Tuple<Double, Double> > > > publicLoansGiven = new HashMap<String, Map<String, List<Tuple<Double, Double> > > >();
        private static Map<String, Map<String, List<Tuple<Double, Double> > > > publicLoansTaken = new HashMap<String, Map<String, List<Tuple<Double, Double> > > >();
        private static Map<String, Double> publicGreediness = new HashMap<String, Double>();        

	@Deprecated
	public PoliticalGroup() {
		super();
	}

	public PoliticalGroup(GroupDataInitialiser dm) {
		super(dm);
	}

	@Override
	protected void onActivate() {
                this.setGroupStrategy(AgentType.R); //ADDED The0

	}

	@Override
	protected boolean respondToJoinRequest(String playerID) {


            if (getDataModel().getMemberList().isEmpty())
            {
                //if empty then 'playerID' created the group so there is no need to compute a heuristic
                return true;
            }
            else if (getDataModel().getMemberList().size() == 1)
            {
                //if there is only one member then 'playerID' was invited and wants to accept the invitation
                //so there is no need to compute a heuristic
                return true;
            }
            else if (getConn().getAgentById(playerID) == null) //ADDED THE0
            {
                //agent does not exist so invitation is denied
                return false;
            }
            else if(this.getId().equals(PoliticalAgentGroup.special))
            { //exception for the Special group
                return true;
            }
            else
            {
                double maxDistance = Math.sqrt(2);
                int numKnownTrustValues = 0;
                double trustSum = 0;

                //Retieve the trust value between requester and the group
                for (String trustor : this.getDataModel().getMemberList())
                {
                        if (getConn().getAgentById(trustor).getTrust(playerID) != null)
                        {
                                trustSum += getConn().getAgentById(trustor).getTrust(playerID);
                                numKnownTrustValues++;
                        }
                }

                //Calculate the vector distance between these two agents socio-economic beliefs
                double economic = getConn().getAgentById(playerID).getEconomicBelief() - getDataModel().getCurrentEconomicPoisition();//change in X
                double social = getConn().getAgentById(playerID).getSocialBelief() - getDataModel().getEstimatedSocialLocation();//change in Y
                double vectorDistance = Math.sqrt(Math.pow(economic, 2) + Math.pow(social, 2));

                //The longer the distance the lower the esFaction is. Therefore, agents close to group's beliefs have
                //higher probability of joining this group
                double esFaction = 1 - (vectorDistance / maxDistance);

                double heuristicValue;
                if (numKnownTrustValues != 0)
                {
                    //The actual heuristic value is calculated. The politics is more important for compatibility than
                    //trust when a group decides on the request, but trust does contribute signicantly
                    heuristicValue = 0.4*(trustSum/numKnownTrustValues) + 0.6*esFaction;
                }
                else
                {
                    heuristicValue = 0.6*esFaction;
                }

                if (heuristicValue > 0.5)
                {
                    //agent can join, so update economic beliefs
                    double size = this.getDataModel().getMemberList().size();
                    economic = 0;
                    for (String members : getDataModel().getMemberList()){
                        if (getConn().getAgentById(members) != null)   //GIVES PROBLEMS
                            economic += getConn().getAgentById(members).getEconomicBelief();
                    }
                    economic += getConn().getAgentById(playerID).getEconomicBelief();
                    economic = economic / (size+1);
                    setEconomicPosition(economic);
                    return true;
                }
                else
                {
                    return false;
                }
            }
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
            if(this.getId().equals(PoliticalAgentGroup.special)){
                //do nothing
            }
            else{            
                //update economic belief of the group when the agent leaves the group
                double size = this.getDataModel().getMemberList().size();
                double economic = 0;
                for (String members : this.getDataModel().getMemberList()){
                    economic += getConn().getAgentById(members).getEconomicBelief();
                }
                economic = economic / (size);
                this.setEconomicPosition(economic);
            }
	}

	@Override
	protected void beforeNewRound() {
            if (getDataModel().getMemberList().size() != 1)
            {
                List<String> newPanel = updatePanel();
                this.setPanel(newPanel);
            }
            this.setGroupStrategy(decideGroupStrategy());
            
            //If a group in need has died then we have to remove it from the set.
            if(!getConn().availableGroups().containsAll(inNeed.keySet()))
            {
                Set<String> available = getConn().availableGroups();
                Iterator<String> i = inNeed.keySet().iterator();
                while(i.hasNext())
                {
                    String member = i.next();
                    if (!available.contains(member))
                    {
                        i.remove();
                    }
                }
            }

            //Here we check the economic status of a group
            theMoneyIsOK(getDataModel().getCurrentReservedFood());

            //We update the group panel
            if (getDataModel().getMemberList().size() != 1)
            {
                List<String> newPanel = updatePanel();
                this.setPanel(newPanel);
            }

            //We update the group's greediness. For now it is constant
            publicGreediness.put(this.getId(), greediness);            
	}

    /**
    * This method updates the panel for this group. The panel is the set of leaders in this group
    * The size of the panel depends on the social position of the group. If it is at the very top
    * it has a single leader (dictator). If it is at the bottom then every member belongs to the panel (anarchism).
    * @param none
    * @return The new panel members.
    */
        private List<String> updatePanel(){

            double groupSocialPosition;
            int population, panelSize;

            //STEP 1:Find the size of the panel. It is the proportion of the total population that
            // can be in the panel. It is calculated using the social position of the group.
            population = getDataModel().getMemberList().size();
            groupSocialPosition = getDataModel().getEstimatedSocialLocation();

            //Round to the closest integer
            panelSize = (int) Math.round(population * groupSocialPosition);
            if (panelSize == 0) //The group is on the very top of the axis. Dictatorship
            {
                //Force panelSize to be at least one (dictator)
                panelSize = 1;
            }
            //STEP 1 END

            //STEP 2: Get the average trust of each agent in the group
            List< Tuple<String, Double> > panelCandidates = new LinkedList< Tuple<String, Double> >();

            List<String> groupMembers = getDataModel().getMemberList();

            for (String candidate: groupMembers )
            {
                double sum = 0;
                int numKnownTrustValues = 0;
                for (String member: groupMembers )
                {
                    if ((getConn().getAgentById(member).getTrust(candidate) != null)&&(!member.equals(candidate)))
                    {
                        sum += getConn().getAgentById(member).getTrust(candidate);
                        numKnownTrustValues++;
                    }
                }

                Tuple<String, Double> tuple;
                if (numKnownTrustValues != 0)
                {
                    tuple = new Tuple<String, Double>(candidate, sum/numKnownTrustValues);
                    panelCandidates.add(tuple);
                }
            }
            //STEP 2 END

            //STEP 3: Sort the agents in descending order of trust values
            Collections.sort(panelCandidates, d);
            //STEP 3 END

            //STEP 4: Populate the panel list with the most trusted agents in the group (i.e. the leaders)
            //Note that eventhough an agent is a candidate its trust must be above a threshold to become member of the panel.
            //The threshold is the social position. If the group is highly authoritarian then anyone with a trust value
            //above zero can become a leader. In libertarian groups panel formations are rare since a relatively high trust value
            //must be achieved! Also the threshold acts as a warning for current panel members. If their trust falls
            //below this threshold due to bad decisions they will be ousted in the next round.
            List<String> newPanel = new LinkedList<String>();
            if (!panelCandidates.isEmpty()&&(panelCandidates.size() >= panelSize))//Panel is not empty and we have enough candidates to select leaders
            {
                for (int i = 0; i < panelSize; i++)
                {
                    if (panelCandidates.get(i).getValue() >= groupSocialPosition)
                    {
                        newPanel.add(panelCandidates.get(i).getKey());
                    }
                }
            }
            //STEP 4 END

            return newPanel;
        }

        private Comparator< Tuple<String, Double> > d = new Comparator< Tuple<String, Double> >() {
            @Override
            public int compare(Tuple<String, Double> o1, Tuple<String, Double> o2)
            {
                Double v1 = o1.getValue();
                Double v2 = o2.getValue();
            	return (v1>v2 ? -1 : 1);
            }
	};

    /**
    * A political group can play the stag hunt game with another group. The panel must make a decision regarding
    * their strategy. Important considerations for the decision is the social belief and the preferred strategy of
    * the group and the panel.
    * @param none
    * @return The group's chosen strategy
    */
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
        //quotum is very high (bottom of y axis - anarchism)
        return null;
    }

    /**
    * This is a helper method which returns preferred strategies of a set of agents in descending order
    * @param The set of agents
    * @return A list of preferred strategies in descending order
    */
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
    
    private Comparator< Tuple<Double, Double> > loansComparator = new Comparator< Tuple<Double, Double> >() {
        @Override
        public int compare(Tuple<Double, Double> o1, Tuple<Double, Double> o2)
        {
            Double v1 = o1.getKey();
            Double v2 = o2.getKey();
            return (v1>v2 ? -1 : 1);
        }
    };    

    /**
    * This method allows a group to spend money for either playing the game or for public service.
    * The group can also repay some of its loans in this function
    * @param none
    * @return The new strategy (if they don't have money to play the sit out) and the new reserve after payments.
    */
    @Override
    protected Tuple<AgentType, Double> makePayments()
    {   
        double currentFoodReserve;
        AgentType strategy = getDataModel().getGroupStrategy();
        
        currentFoodReserve = getDataModel().getCurrentReservedFood();
        
        if(this.getDataModel().getReservedFoodHistory().size() < 3)
            return new Tuple<AgentType, Double>(strategy, 300.0);
        
        //Check if the group is in need. If it doesn't then proceed with the payments
        if(!inNeed.containsKey(this.getId()))
        {   
            //Spend money for playing the game. The standard fee is defined in the data members
            if(strategy != null)
            {
                //pay, in theory, to join the game among groups
                currentFoodReserve -= priceToPlay;
                if(currentFoodReserve < priceToPlay)//if the theory is way to risky
                {
                    currentFoodReserve += priceToPlay;//go back
                    strategy = null;//and sit this turn out
                }
            }

            //Check you greediness against a random value. If your greediness is above that value spend.
            //Groups with higher greediness value have higher chances of spending money
            if ( this.greediness > new Random().nextDouble() )
            {
                //check how close you are to attaining achievement
                double goalRatio = currentFoodReserve / achievementThreshold;
                double percentDecrease;
                percentDecrease = ( (1-getAverageHappiness(0)) * goalRatio) * currentFoodReserve;
                currentFoodReserve -= percentDecrease;
            }

            //After paying for the game and for public service check if you can repay any of the loans (if there are any)
            if (!loansTaken.isEmpty())
            {
                //Set<String> creditorsSet = loansTaken.keySet();
                //for (String creditorID: creditorsSet)
                Iterator<String> creditors = loansTaken.keySet().iterator();
                while(creditors.hasNext())
                {
                    String creditorID = creditors.next();
                    double totalAmountPaid = 0;
                    //Find the loans taken from this creditor and sort the in descending order
                    Collections.sort(loansTaken.get(creditorID), loansComparator);
                    //Iterate through the loans from this creditor
                    List<Tuple<Double, Double> > loanInfoList = loansTaken.get(creditorID);
                    Iterator<Tuple<Double, Double> > i = loanInfoList.iterator();
                    while(i.hasNext())
                    {
                        Tuple<Double, Double> loanInfo = i.next();
                        //Calculate the amount to pay (amount *(1+ interest))
                        double amountToPay = loanInfo.getKey()* (1+loanInfo.getValue());
                        //If the group has the money then it pays
                        if (currentFoodReserve > amountToPay + priceToPlay)
                        {
                            currentFoodReserve -= amountToPay;
                            //We remove this loan since it is paid
                            i.remove();
                            totalAmountPaid += amountToPay;
                        }
                    }
                    //If that was the only (or the only non repaid) loan taken from this creditor then remove the creditor form the list
                    if (loansTaken.get(creditorID).isEmpty()) creditors.remove();

                    //If the group has repaid any of the loans prepare a ticket and send it to the creditor
                    //in order to process the payment. The ticket contains the id of the debtor and the loan information
                    if (totalAmountPaid != 0)
                    {
                        Tuple<String, Double> paymentReceipt = new Tuple<String, Double>();
                        paymentReceipt.add(this.getId(), totalAmountPaid);
                        if (!loanRepayments.containsKey(creditorID))
                        {
                            List<Tuple<String, Double> > existingPayments = new ArrayList<Tuple<String, Double> >();
                            existingPayments.add(paymentReceipt);
                            loanRepayments.put(creditorID, existingPayments);
                        }
                        else
                        {
                            List<Tuple<String, Double> > existingPayments = loanRepayments.get(creditorID);
                            existingPayments.add(paymentReceipt);
                        }
                    }
                }
            }
        }
        else
        {
            strategy = null;
        }
        return new Tuple<AgentType, Double>(strategy, currentFoodReserve);            
    }
    
    /**
    * This method computes the average happiness for this group for a specific turn
    * @param The number of turns ago
    * @return The average happiness of the group at that time
    */
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
                    if (getConn().getAgentById(member).getHappinessHistory().size() > 1)
                    {
                        happiness = getConn().getAgentById(member).getHappinessHistory().getValue(turnsAgo);
                    }
                    else
                    {
                        happiness = 0.0;
                    }
                    if(happiness != null)//otherwise add nothing
                    {
                        average += happiness;                   
                    }
                }
                else
                {
                    happiness = getConn().getAgentById(member).getCurrentHappiness();
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
    
    /**
    * This method checks if the economic status of the group is good or bad.
    * It is good if they have money to last at least another round and their reserve increases
    * If the group is in trouble a loan is requested based on its needs.
    * @param The current reserve
    * @return Is the group in need?
    */
    private boolean theMoneyIsOK(double mostRecentReserve)
    {
        //is the reserve increasing or decreasing?
        double deltaFoodReserve;
        if (getDataModel().getReservedFoodHistory().size() > 1)
        {
            deltaFoodReserve = mostRecentReserve - getDataModel().getReservedFoodHistory().getValue(1);
        }
        else
        {
            deltaFoodReserve = 0;
        }

        //check how close the group is to attaining achievement
        double goalRatio = mostRecentReserve / achievementThreshold;        

        //If the group is already in the set of groups in need do nothing.
        //Otherwise check for a sufficient amount of food and an incresing trend in their reserve
        //If both conditions are not met declare this group as "a group in need"
        if(!inNeed.containsKey(this.getId()))
        {            
            if((goalRatio < 0.15)&&(deltaFoodReserve<0))
            {
                //if group is only 15% of the way then the group needs help      
                inNeed.put(this.getId(), 150 - mostRecentReserve);
                return false;
            }
            else
            {
                //everything is ok, nobody needs help
                return true;
            }
        }
        else
        {
            //you've been in need for quite a while, it would seem
            return false;
        }
    }    
    
    /**
    * After the group members return from hunting we gather the shared food. Then depending on
    * the financial status of the group we decide the tax rate. We intercept the taxed amount and store
    * it in the reserved food pool. The rest is distributed to the members.
    * Before deciding the tax rate we must update the reserve if the group has received a payment from one of its
    * debtors.
    * @param The shared food for this round
    * @return The new updated shared food and reserve
    */
    @Override
    protected Tuple<Double, Double> updateTaxedPool(double sharedFood) {
        double currentFoodReserve; 
        currentFoodReserve = getDataModel().getCurrentReservedFood();
        double tax = 0;

        //If this groups has any debtors must check if any of them has paid back
        if(loanRepayments.containsKey(this.getId()))
        {
            List<Tuple<String, Double> > paymentsInfo = new  ArrayList<Tuple<String, Double> >();
            paymentsInfo = loanRepayments.get(this.getId());
            
            Iterator<Tuple<String, Double> > i = paymentsInfo.iterator();
            while(i.hasNext())
            {
                Tuple<String, Double> currentPayment = i.next();
                double amountReceived = currentPayment.getValue();
                //Update the reserve
                currentFoodReserve += amountReceived;
            }
            loanRepayments.remove(this.getId());
        }

        //check how close you are to attaining achievement
        double goalRatio = currentFoodReserve / achievementThreshold;

        if(inNeed.containsKey(this.getId()))
        {
            //the group is in trouble and needs to tax high
            tax = 1 - goalRatio;//since you're far away from your achievement, tax high 
        }
        else
        {
            //tax normally
            tax = getAverageHappiness(0) * (1-goalRatio);
        }

        //The actual taxation happens here
        currentFoodReserve = currentFoodReserve + sharedFood*tax;
        sharedFood = sharedFood - sharedFood*tax;
        
        Tuple<Double, Double> newSharedAndReserve = new Tuple<Double,Double>();
        newSharedAndReserve.add(sharedFood, currentFoodReserve);

        return newSharedAndReserve;
    }


/**
    * This method allows this group to interact with other groups. If it is in need, it can check
    * if any of its loan request has been accepted. If the group is not in trouble it can check if there
    * is any group which needs help. If it has enough money it can give them money (food). 
    * @param none
    * @return The interaction result and the new updated reserve (if they gave any loans)
    */
    @Override
    protected Tuple<InteractionResult, Double> interactWithOtherGroups() {

        double currentFoodReserve = getDataModel().getCurrentReservedFood();
        Tuple<InteractionResult, Double> interactionResult = new Tuple<InteractionResult, Double>();

        //First check if you are in need
        if(inNeed.containsKey(this.getId()))
        {
            //Check if group has managed to recover their economic status to good.
            //Also even if they managed to do so if someone has already givem them a loan then they must pay
            if ((currentFoodReserve > priceToPlay+50)&&(!loanRequestsAccepted.containsKey(this.getId())))
            {
                inNeed.remove(this.getId());
                interactionResult.add(InteractionResult.NothingHappened, 0.0);
            }
            else if (loanRequestsAccepted.containsKey(this.getId()))
            {
                 //If the request for a loan was granted then store the receipt in your records to help with repayments later (Hopefully..)
                 HashMap<String, Tuple<Double, Double> > loanRecord = loanRequestsAccepted.get(this.getId());
                 Set<String> giverID = loanRecord.keySet();

                 Tuple<Double, Double> currentLoanInfo = loanRecord.get(giverID.iterator().next());
                 if (!loansTaken.containsKey(giverID.iterator().next()))
                 {
                     List<Tuple<Double, Double> > existingLoans = new ArrayList<Tuple<Double, Double> >();
                     existingLoans.add(currentLoanInfo);
                     this.loansTaken.put(giverID.iterator().next(), existingLoans);
                 }
                 else
                 {
                    List<Tuple<Double, Double> > existingLoans = this.loansTaken.get(giverID.iterator().next());
                    existingLoans.add(currentLoanInfo);
                 }

                 if (!loansTakenHist.containsKey(giverID.iterator().next()))
                 {
                     List<Tuple<Double, Double> > existingLoans = new ArrayList<Tuple<Double, Double> >();
                     existingLoans.add(currentLoanInfo);
                     this.loansTakenHist.put(giverID.iterator().next(), existingLoans);
                 }
                 else
                 {
                    List<Tuple<Double, Double> > existingLoans = this.loansTakenHist.get(giverID.iterator().next());
                    existingLoans.add(currentLoanInfo);
                 }

                 //Add this loan in the loans taken structure which is publicly accessible
                 publicLoansTaken.put(this.getId(), loansTakenHist);

                 loanRequestsAccepted.remove(this.getId());
                 inNeed.remove(this.getId());
                 interactionResult.add(InteractionResult.LoanTaken, loanRecord.get(giverID.iterator().next()).getKey());
            }
            else
            {
                //Else if no one has given you money do nothing
                interactionResult.add(InteractionResult.NothingHappened, 0.0);
            }
            return interactionResult;
        }

        //If you are not in need you might want to help another group
        if (!inNeed.isEmpty())
        {
            Map<String, Double> inNeedSorted = new HashMap<String, Double>();
            inNeedSorted = sortHashMap(inNeed);            
            for (String groupID: inNeedSorted.keySet() )
            {
                //if someone else accepted their requests do nothing!
                if (!loanRequestsAccepted.containsKey(groupID))
                {
                    double amountNeeded = inNeed.get(groupID);
                    //intrest = 0.5 of loaner's greediness and requesters situation which is neve above 0.15
                    double interestRate = 0.05*greediness + (getConn().getGroupById(groupID).getCurrentReservedFood() / achievementThreshold);

                    //For now give a loan if u have the amount needed
                    if ((currentFoodReserve - amountNeeded >  priceToPlay+50)&&(this.greediness < new Random().nextDouble()))
                    {
                        //Create a tuple containing the amount granted and the interest
                        Tuple<Double, Double> loanInfo = new Tuple<Double, Double>();
                        loanInfo.add(amountNeeded, interestRate);

                        //Then store the loan info along with the requester ID in your records
                        if (!loansGiven.containsKey(groupID))
                        {
                            List<Tuple<Double, Double> > existingLoans = new ArrayList<Tuple<Double, Double> >();
                            existingLoans.add(loanInfo);
                            this.loansGiven.put(groupID, existingLoans);
                        }
                        else
                        {
                            List<Tuple<Double, Double> > existingLoans = this.loansGiven.get(groupID);
                            existingLoans.add(loanInfo);
                        }

                        publicLoansGiven.put(this.getId(), loansGiven);
                        
                        //Use the same structure to send a receipt to the requester to store it in his records
                        HashMap<String, Tuple<Double, Double> > loanRecord = new HashMap<String, Tuple<Double, Double> >();
                        loanRecord.put(this.getId(), loanInfo);
                        loanRequestsAccepted.put(groupID, loanRecord);
                        interactionResult.add(InteractionResult.LoanGiven, amountNeeded);
                        return interactionResult;
                    }
                }
            }
        }
        interactionResult.add(InteractionResult.NothingHappened, 0.0);
        return interactionResult;
    }
    
    private HashMap<String, Double> sortHashMap(Map<String, Double> unsorted){
        Map<String, Double> tempMap = new HashMap<String, Double>();
        for (String key : unsorted.keySet())
        {
            tempMap.put(key,unsorted.get(key));
        }

        //make sure to get the data
        List<String> unsortedKeys = new ArrayList<String>(tempMap.keySet());
        List<Double> unsortedValues = new ArrayList<Double>(tempMap.values());
        
        //make our result ready
        HashMap<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        
        //put the values in a tree set, they are immediately orderd, then put them in an array
        TreeSet<Double> sortedSet = new TreeSet<Double>(unsortedValues);       
        Object[] sortedArray = sortedSet.toArray();
        
        //sort them in a map
        for (int i = 0; i < sortedArray.length; i++){
            sortedMap.put(unsortedKeys.get(unsortedValues.indexOf(sortedArray[i])), 
                          (Double)sortedArray[i]);
        }
        return sortedMap;
    }
    
    public static Map<String, List<Tuple<Double, Double> > > getLoansGiven(PublicGroupDataModel dm)
    {
        if (!publicLoansGiven.containsKey(dm.getId()))
        {
            return null;
        }
        else
        {
            return publicLoansGiven.get(dm.getId());
        }
    }

    public static Map<String, List<Tuple<Double, Double> > > getLoansTaken(PublicGroupDataModel dm)
    {
        if (!publicLoansTaken.containsKey(dm.getId()))
        {
            return null;
        }
        else
        {
            return publicLoansTaken.get(dm.getId());
        }
    }

    public static Double getGreediness(PublicGroupDataModel dm)
    {
        return publicGreediness.get(dm.getId());
    }    
}

