package behaviours;

import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.util.Scanner;

public class WaitInputInstances extends CyclicBehaviour{
    String path_file; // This is the project path. Not the global path

    // Class constructor
    public WaitInputInstances() {
        // Using Scanner for getting input from user
        System.out.println("Please, enter path of the file containing the instances to be classified.");
        Scanner in = new Scanner(System.in);
        String path_file = in.nextLine();
        System.out.println("You entered string " + path_file);
        this.path_file = path_file;
    }

    public void action () {
        try {
            System.out.println("Data is being read at direction:" + System.getProperty("user.dir") + this.path_file);
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(System.getProperty("user.dir") + this.path_file);
            if(source != null) {
                System.out.println("Read");

                Instances data = source.getDataSet();

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContentObject(data);
                AID dest = new AID("coordinatorAgent", AID.ISLOCALNAME);
                msg.addReceiver(dest);
                myAgent.send(msg);
            }
        } catch (Exception e) {
            //añadir más adelante un reinsert path si salta error
            e.printStackTrace();
            System.out.println("An error occured");
        }

    }
}
