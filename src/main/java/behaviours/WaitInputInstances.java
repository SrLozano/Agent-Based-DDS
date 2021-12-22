package behaviours;

import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.Scanner;
import agents.userAgent;


public class WaitInputInstances extends CyclicBehaviour{

    String path_file; // This is the project path. Not the global path
    private final userAgent myAgent;

    // Class constructor
    public WaitInputInstances(userAgent agent) {
        super(agent);
        myAgent = agent;
        // Using Scanner for getting input from user
        //System.out.println("Please, enter path of the file containing the instances to be classified.");
        //Scanner in = new Scanner(System.in);
        //String path_file = in.nextLine();
        //System.out.println("You entered string " + path_file);
        //this.path_file = path_file;
    }

    public void action () {
        try {
            // Using Scanner for getting input from user
            System.out.println("Please, enter path of the file containing the instances to be classified.");
            Scanner in = new Scanner(System.in);
            String path_file = in.nextLine();
            System.out.println("You entered string " + path_file);
            this.path_file = path_file;
            System.out.println("data is being read at direction:" + System.getProperty("user.dir") + '/' + this.path_file);
            DataSource source = new DataSource(System.getProperty("user.dir") + '/' + this.path_file);
            // TODO: Comprobar si el input es correcto jejje y si no que salga un mensaje diciendo k no
            if(source != null) { // TODO: ojo always not null
                System.out.println("Read");
                Instances data = source.getDataSet();
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContentObject(data); //The content of the message it's the data
                AID dest = new AID("coordinatorAgent", AID.ISLOCALNAME);
                msg.addReceiver(dest); //The receiver is the coordinator Agen
                myAgent.send(msg); //The message is sent
            }
        } catch (Exception e) {
            System.out.println("An error occured. EN USER");
            // TODO: añadir más adelante un reinsert path si salta error
            e.printStackTrace();
        }
    }
}
