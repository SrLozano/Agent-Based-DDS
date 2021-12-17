package behaviours;

import agents.coordAgent;
import jade.core.*;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import weka.core.Attribute;

public class votingSystem extends OneShotBehaviour {

    private final coordAgent myAgent;

    // Constructor of the behaviour
    public votingSystem(coordAgent coordAgent) {
        super(coordAgent);
        this.myAgent = coordAgent;
    }

    public void action () {

        int responses = 0;

        // SWITCH CON IDLE O LO QUE SEA JEJJEJE

        while(responses < myAgent.getNumber_classifiers()){

            try {
                ACLMessage msg = myAgent.receive();
                double [] response = (double[]) msg.getContentObject();
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
            responses += 1;
        }

        // Classifications and weights are known (Somehow). TODO: Finish this

        double [] performances = new double[] {0.1, 0.3, 0.8};
        double [] classifications = new double [] {0, 1, 0};

        double sum_performances = 0;

        // We get the sum of the performances so each one could have a good weight
        for (int i = 0; i < performances.length; ++i) {
            sum_performances += performances[i];
        }

        double [] importance = new double [performances.length];

        // We get the importance of each classifier knowing that all must sum 1
        for (int i = 0; i < performances.length; ++i) {
            importance[i] = performances[i]/sum_performances;
        }

        double result = 0;

        for (int i = 0; i < classifications.length; ++i){
            result += classifications[i]*importance[i];
        }

        /* If the result is bigger or equal than 0.5 it means that the majority (considering weights) of agents
        agree that the instance should be classified as 1. Otherwise, they return a 0. In case of a tie result
        is 1 because a false positive is better than a false negative */
        if (result >= 0.5){ result = 1; }
        else { result = 0; }

    }
}
