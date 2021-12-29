package behaviours;

import agents.coordAgent;
import agents.userAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.Scanner;


public class userBehaviour extends CyclicBehaviour {

    String path_file; // This is the project path. Not the global path
    private final userAgent myAgent;

    // Class constructor
    public userBehaviour(userAgent agent) {
        super(agent);
        myAgent = agent;
    }

    public void action() {
        try {
            // Using Scanner for getting input from user
            //System.out.println("HOLA " + coordAgent.state);
            if (coordAgent.state==coordAgent.global_states.IDLE) {
                Thread.sleep(1000); // For achieving a good print order in the terminal
                System.out.println("Please, enter path of the file containing the instances to be classified.");
                Scanner in = new Scanner(System.in);
                String path_file = in.nextLine();
                System.out.println("You entered string " + path_file);
                this.path_file = path_file;
                System.out.println("data is being read at direction:" + System.getProperty("user.dir") + '/' + this.path_file);
                DataSource source = new DataSource(System.getProperty("user.dir") + '/' + this.path_file);
                // TODO: Comprobar si el input es correcto jejje y si no que salga un mensaje diciendo k no
                if (source != null) { // TODO: ojo always not null
                    System.out.println("Read");

                    Instances data = source.getDataSet();

                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.setContentObject(data); //The content of the message it's the data
                    AID dest = new AID("coordAgent", AID.ISLOCALNAME);
                    msg.addReceiver(dest); //The receiver is the coordinator Agent
                    myAgent.send(msg); //The message is sent
                    System.out.println("instances sent to coordinator");
                    // TODO: Is this okey?
                    Thread.sleep(1000);
                }
            } // SENDING or VOTING. A result is received so that sliptinputIntances can continue
            else if (coordAgent.state==coordAgent.global_states.VOTING){
                int received_instances = 0;
                double[] results = new double[15];
                while (received_instances<15){ // 15 is the number of instances in a file. TODO: put this in a global variable?
                    System.out.println("VOTATION HAS BEEN DONE");
                    ACLMessage msg_received = myAgent.blockingReceive();
                    results[received_instances] = (double) msg_received.getContentObject();
                    received_instances+=1;
                    System.out.println("Num of received instances: "+received_instances);
                System.out.println(results);
                }
            }
        } catch(Exception e){
            System.out.println("An error occured. EN USER");
            // TODO: añadir más adelante un reinsert path si salta error
            e.printStackTrace();
        }
    }
}

