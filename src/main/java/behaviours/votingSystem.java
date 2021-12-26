package behaviours;

import agents.coordAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

public class votingSystem extends CyclicBehaviour {

    private final coordAgent myAgent;

    // Constructor of the behaviour
    public votingSystem(coordAgent coordAgent) {
        super(coordAgent);
        this.myAgent = coordAgent;
    }


    public void action() {
        if (myAgent.getNameState() == coordAgent.global_states.VOTING) {
            int number_classifiers = myAgent.getNumber_classifications();
            // Arrays to collect performances and classifications from classifiers
            double[] performances = new double[number_classifiers];
            double[] classifications = new double[number_classifiers];

            int responses = 0;
            // Responses are obtained until all classifiers vote
            while (responses < number_classifiers) {
                try {
                    ACLMessage msg = myAgent.blockingReceive();
                    // Message contains a double array with [performance, classification, num_of_instance]
                    double[] response = (double[]) msg.getContentObject(); //da este error: jade.lang.acl.UnreadableException: invalid stream header: 28202861
                    int instance_num = (int) response[2];
                    performances[responses] = response[0];
                    classifications[responses] = response[1];
                    System.out.println(response[1]);
                    responses += 1;
                    if (instance_num == 14) { //when it has received the results of all instances set to idle so it does not enter again this behaviour until new input
                        myAgent.setNameState(coordAgent.global_states.IDLE);
                    }

                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
            }

            double sum_performances = 0;

            // We get the sum of the performances so each one could have a good weight
            for (int i = 0; i < performances.length; ++i) {
                sum_performances += performances[i];
            }

            double[] weights = new double[performances.length];
            // We get the importance (weight) of each classifier knowing that all must sum 1
            for (int i = 0; i < performances.length; ++i) {
                weights[i] = performances[i] / sum_performances;
            }

            double result = 0;
            for (int i = 0; i < classifications.length; ++i) {
                result += classifications[i] * weights[i];
            }

        /* If the result is bigger or equal than 0.5 it means that the majority (considering weights) of agents
        agree that the instance should be classified as 1. Otherwise, they return a 0. In case of a tie result
        is 1 because a false positive is better than a false negative */
            double final_result = 0;
            if (result >= 0.5) {
                final_result += 1;
            } else {
                final_result += 0;
            }
            ACLMessage msg_toSend = new ACLMessage(ACLMessage.INFORM);
            double message = final_result;
            try {
                msg_toSend.setContentObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
            AID dest = new AID("userAgent", AID.ISLOCALNAME);
            msg_toSend.addReceiver(dest); //The receiver is the coordinator Agent
            myAgent.send(msg_toSend); //The message is sent
        }
    }
}
