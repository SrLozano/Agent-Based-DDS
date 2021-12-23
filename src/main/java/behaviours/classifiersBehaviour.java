package behaviours;
import agents.classifierAgent;
import agents.coordAgent;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.UnreadableException;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.classifiers.trees.J48;

import java.util.ArrayList;
import jade.lang.acl.ACLMessage;
import java.io.IOException;  // Import the IOException class to handle errors

public class classifiersBehaviour extends CyclicBehaviour {

    private final classifierAgent myAgent;

    // Constructor of the behaviour
    public classifiersBehaviour(classifierAgent classifierAgent) {
        super(classifierAgent);
        this.myAgent = classifierAgent;
    }

    public void action() {
        //System.out.println(coordAgent.state);
        coordAgent.global_states stateString = coordAgent.state;

        //TODO: How to call the states defined in the coordinator
        J48 classifier = myAgent.getModel(); //Getting the trained classifiers
        try {
            ACLMessage msg2 = myAgent.blockingReceive();
            if (msg2 != null) {
                String[][] attr_vals = (String[][]) msg2.getContentObject();
                String[] attributes = attr_vals[0];
                ArrayList<Attribute> atts = new ArrayList<Attribute>();
                for (int i = 0; i < attributes.length; i++) {
                    atts.add(new Attribute(attributes[i]));
                }

                String[] vals = attr_vals[1];
                double[] values = new double[vals.length];
                for (int i = 0; i < vals.length; i++) {
                    values[i] = Double.parseDouble(vals[i]);
                }
                //Instances firm = new Instances("TestInstances", atts , 0);
                //firm.add(new DenseInstance(1.0, values));
                DenseInstance testInstance = new DenseInstance(1.0, values);
                //testInstance.setDataset(firm);
                double output = 0;
                try {
                    output = classifier.classifyInstance(testInstance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                double performance = myAgent.getPerformance();
                double[] message = new double[2];
                message[0] = performance;
                message[1] = output;

                ACLMessage msg_to_send = new ACLMessage(ACLMessage.INFORM);
                msg_to_send.setContentObject(message);
                AID dest = new AID("coordAgent", AID.ISLOCALNAME);
                msg_to_send.addReceiver(dest); //The receiver is the coordinator Agent
                myAgent.send(msg_to_send); //The message is sent
                System.out.println(myAgent.getAID()+" sent the classification to coordinator");
                }
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
