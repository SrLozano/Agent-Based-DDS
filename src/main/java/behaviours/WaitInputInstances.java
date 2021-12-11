package behaviours;


import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.Scanner;
import agents.coordAgent;


public class WaitInputInstances extends CyclicBehaviour{

    String path_file; // This is the project path. Not the global path
    private final coordAgent myAgent;

    // Class constructor
    public WaitInputInstances(coordAgent coordAgent) {
        // Using Scanner for getting input from user
        System.out.println("Please, enter path of the file containing the instances to be classified.");
        Scanner in = new Scanner(System.in);
        String path_file = in.nextLine();
        System.out.println("You entered string " + path_file);

        this.path_file = path_file;
        this.myAgent = coordAgent;
    }

    public void action () {
        try {
            System.out.println("data is being read at direction:" + System.getProperty("user.dir") + '\\' + this.path_file);
            DataSource source = new DataSource(System.getProperty("user.dir") + '\\'+ this.path_file);
            if(source != null) { // TODO: ojo always not null
                System.out.println("Read");
                Instances data = source.getDataSet();
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContentObject(data); //The content of the message it's the data
                AID dest = new AID("coordinatorAgent", AID.ISLOCALNAME);
                msg.addReceiver(dest); //The receiver is the coordinator Agent

                //while ()

                myAgent.send(msg); //The message is sent
            }
        } catch (Exception e) {
            System.out.println("An error occured");
            // TODO: añadir más adelante un reinsert path si salta error
            e.printStackTrace();
        }
    }
}
