package agents;
import java.util.Scanner;

import behaviours.splitInputInstances;
import behaviours.trainClassifiers;
import behaviours.votingSystem;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import behaviours.WaitInputInstances;
import agents.coordAgent;

public class TestFSMAgent extends Agent{
    private static final String TrainingClassifiers = "TrainingClassifiers";
    private static final String Idling = "Idling";
    private static final String SplittingTest = "SplittingTest";
    private static final String Voting = "Voting";

    protected void setup() {
        FSMBehaviour fsm = new FSMBehaviour(this);
        //fsm.registerFirstState(new trainClassifiers(), TrainingClassifiers);
        //fsm.registerState(new Idle(), Idling);
        //fsm.registerState(new splitInputInstances(), SplittingTest);
        //fsm.registerLastState(new votingSystem(), Voting);

        fsm.registerDefaultTransition(TrainingClassifiers, Idling);
        fsm.registerDefaultTransition(Idling, SplittingTest);
        fsm.registerDefaultTransition(SplittingTest, Idling);
        fsm.registerTransition(Idling, Voting, 1);

    }


}
