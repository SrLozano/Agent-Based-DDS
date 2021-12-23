package behaviours;

import agents.coordAgent;
import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class votingSystem extends CyclicBehaviour {

    private final coordAgent myAgent;

    // Constructor of the behaviour
    public votingSystem(coordAgent coordAgent) {
        super(coordAgent);
        this.myAgent = coordAgent;
    }

    public void action() {
        if (myAgent.getNameState() == coordAgent.global_states.TESTING) {
            int number_classifiers = myAgent.getNumber_classifiers();

            // Arrays to collect performances and classifications from classifiers
            double[] performances = new double[number_classifiers];
            double[] classifications = new double[number_classifiers];

            int responses = 0;

            // Responses are obtained until all classifiers vote
            while (responses < number_classifiers) {
                try {
                    ACLMessage msg = myAgent.blockingReceive();

                    System.out.println(responses);

                    // Message contains a double array with [performance, classification]
                    double[] response = (double[]) msg.getContentObject();
                    performances[responses] = response[0];
                    classifications[responses] = response[1];

                    responses += 1;

                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
            }

            double sum_performances = 0;

            // We get the sum of the performances so each one could have a good weight
            for (int i = 0; i < performances.length; ++i) {
                sum_performances += performances[i];
            }

            double[] importance = new double[performances.length];

            // We get the importance of each classifier knowing that all must sum 1
            for (int i = 0; i < performances.length; ++i) {
                importance[i] = performances[i] / sum_performances;
            }

            double result = 0;

            for (int i = 0; i < classifications.length; ++i) {
                result += classifications[i] * importance[i];
            }

        /* If the result is bigger or equal than 0.5 it means that the majority (considering weights) of agents
        agree that the instance should be classified as 1. Otherwise, they return a 0. In case of a tie result
        is 1 because a false positive is better than a false negative */
            if (result >= 0.5) {
                result = 1;
            } else {
                result = 0;
            }

            //System.out.println(result);
        }
    }
}
