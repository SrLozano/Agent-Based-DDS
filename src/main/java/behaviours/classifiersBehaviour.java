/*****************************************************************
 TODO: Fill description of this file. Explain everything
 @behaviour: classifierBehaviour

 @authors: Sergi Cirera, Iago Águila, Laia Borrell and Mario Lozano
 @group: 6 - IMAS - URV - UPC
 *****************************************************************/

package behaviours;

import agents.classifierAgent;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;

public class classifiersBehaviour extends CyclicBehaviour {

    private final classifierAgent myAgent;

    /* Class constructor for the behaviour. Agent is set as a class variable */

    public classifiersBehaviour(classifierAgent classifierAgent) {
        super(classifierAgent);
        this.myAgent = classifierAgent;
    }

    /* An instance is received, classified and sent with its weight to the coordinator agent */

    public void action(){
        try {
            ACLMessage message_to_classify = myAgent.blockingReceive(); // Wait until an instance to classify is received
            J48 classifier = myAgent.getModel(); // Get the trained classifiers

            if (message_to_classify != null) {
                // Prepare instance to be classified
                Instances filtered_dataset = (Instances) message_to_classify.getContentObject();
                filtered_dataset.setClassIndex(filtered_dataset.numAttributes()-1);
                Instance test_instance = filtered_dataset.get(0);
                double output = -1;
                try {
                    // Get model classifiation for the instance
                    output = classifier.classifyInstance(test_instance);
                    //System.out.println("Output: "+output+" Real class: "+test_instance.classValue());
                } catch (Exception e) {
                    System.out.println("Evaluation could not be done correctly. And error occurred");
                }

                double performance = myAgent.getPerformance();
                Double[] message = new Double[2]; // Format [performance, classification]
                message[0] = performance; // Performance is sent to give a weight to the classification
                message[1] = output;

                // Message is sent to the coordinator agent
                ACLMessage msg_to_send = new ACLMessage(ACLMessage.INFORM);
                msg_to_send.setContentObject(message);
                AID dest = new AID("coordAgent", AID.ISLOCALNAME);
                msg_to_send.addReceiver(dest); // The receiver is the coordinator Agent
                myAgent.send(msg_to_send); // The message is sent
            }
            else{ throw new IOException("There is not and instance to to classify in the message"); }

        } catch (UnreadableException | IOException e) {
            System.out.println("Instance could not be classified. An error occurred");
        }
    }
}
