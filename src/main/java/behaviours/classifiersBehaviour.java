package behaviours;

import agents.classifierAgent;
import agents.coordAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Instance;

import java.io.IOException;
import java.util.ArrayList;

public class classifiersBehaviour extends CyclicBehaviour {

    private final classifierAgent myAgent;

    // Constructor of the behaviour
    public classifiersBehaviour(classifierAgent classifierAgent) {
        super(classifierAgent);
        this.myAgent = classifierAgent;
    }

    public void action(){
        coordAgent.global_states stateString = coordAgent.state;

        //TODO: How to call the states defined in the coordinator
        J48 classifier = myAgent.getModel(); //Getting the trained classifiers
        try {
            ACLMessage msg2 = myAgent.blockingReceive();

            //System.out.println("SOY CLASSIFIER: "+ myAgent.getName() + "Y ME DESBLOQUEO: " + coordAgent.state);

            if (msg2 != null) {
                //msg2 has the attributes in the first array, the values in the second, and the instance id in the third
                String[][] attr_vals_id = (String[][]) msg2.getContentObject();

                String instance_id = attr_vals_id[2][0];

                String[] attributes = attr_vals_id[0];
                ArrayList<Attribute> atts = new ArrayList<Attribute>();
                for (int i = 0; i < attributes.length; i++) {
                    atts.add(new Attribute(attributes[i]));
                }
                atts.add(new Attribute("Class")); //me lo he patillado un poco, he añadido una class
                String[] vals = attr_vals_id[1];
                double[] values = new double[vals.length+1];
                for (int i = 0; i < vals.length; i++) {
                    values[i] = Double.parseDouble(vals[i]);
                }
                values[vals.length] = -1; //seteamos un valor random a la class

                Instances dataset = new Instances("TestInstances", atts, 0);
                Instance testInstances = new DenseInstance(1,values);
                dataset.add(testInstances);
                dataset.setClassIndex(dataset.numAttributes()-1);

                //TODO: preguntar que hacen a la gente, me parece raro que si es test instance necesite la clase tb

                testInstances.setDataset(dataset);
                double output = 0;
                try {
                    output = classifier.classifyInstance(testInstances);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //System.out.println("CLASSIFIER MANDA OUTPUT Y ESTADO: " + coordAgent.state);

                double performance = myAgent.getPerformance();
                String[] message = new String[3];
                message[0] = String.valueOf(performance);
                message[1] = String.valueOf(output);
                message[2] = instance_id;
                ACLMessage msg_to_send = new ACLMessage(ACLMessage.INFORM);
                msg_to_send.setContentObject(message);
                AID dest = new AID("coordAgent", AID.ISLOCALNAME);
                msg_to_send.addReceiver(dest); //The receiver is the coordinator Agent
                myAgent.send(msg_to_send); //The message is sent

                //System.out.println(msg_to_send);
                //System.out.println(myAgent.getAID().getName()+" sent the classification of instance to coordinator");

                }
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
