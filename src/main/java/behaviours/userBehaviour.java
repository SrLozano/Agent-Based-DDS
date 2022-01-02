/*****************************************************************
 TODO: Fill description of this file. Explain everything
 @behaviour: userBehaviour

 @authors: Sergi Cirera, Iago Águila, Laia Borrell and Mario Lozano
 @group: 6 - IMAS - URV - UPC
 *****************************************************************/

package behaviours;

import agents.coordAgent;
import agents.userAgent;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.Arrays;
import java.util.Scanner;

public class userBehaviour extends CyclicBehaviour {

    String path_file; // Project path of the file reading instances from. This is not a global path
    int number_instances_test;
    private final userAgent myAgent;

    /* Class constructor for the behaviour. Agent is set as a class variable */

    public userBehaviour(userAgent agent) {
        super(agent);
        myAgent = agent;
        number_instances_test = 0;
    }

    /* This is in charge of reading instances from test source and receiving the results to display them altogether */

    public void action() {
        try {
            // userAgent asks for new test instances only if the coord is in IDLE state (i.e. trained and not busy)
            if (coordAgent.state==coordAgent.global_states.IDLE) {
                try {
                    Thread.sleep(1000); // For achieving a correct print order in the terminal
                    System.out.println("Please, enter path of the file containing the instances to be classified.");

                    // Use scanner for getting input path from user
                    Scanner in = new Scanner(System.in);
                    this.path_file = in.nextLine();
                    System.out.println("You entered string " + this.path_file);
                    System.out.println("Data is being read at direction: " + System.getProperty("user.dir") + '/' + this.path_file);

                    // Get data from source
                    DataSource source = new DataSource(System.getProperty("user.dir") + '/' + this.path_file);
                    Instances data = source.getDataSet();
                    this.number_instances_test = data.numInstances();

                    // Data is sent only if it has been correctly read
                    if (this.number_instances_test != 0) {
                        System.out.println("Read has been correctly read");

                        // Send a message with the data read
                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        msg.setContentObject(data); // Content is the data
                        AID dest = new AID("coordAgent", AID.ISLOCALNAME);
                        msg.addReceiver(dest); // Receiver is the coordinatorAgent
                        myAgent.send(msg); // The message is sent
                        System.out.println("Data has been sent to the coordinator agent");
                    } else {
                        throw new Exception("Data read is null");
                    }

                } catch (Exception e) {
                    System.out.println("An error occurred when trying to read data from source. Please enter the source again");
                }
            }

            // TODO: Sometimes this isn't printed

            // Collect the results and display them altogether
            else if (coordAgent.state==coordAgent.global_states.VOTING){
                int received_instances = 0;
                double[] results = new double[this.number_instances_test];

                while (received_instances<this.number_instances_test){
                    System.out.println();
                    ACLMessage msg_received = myAgent.blockingReceive();
                    results[received_instances] = (double) msg_received.getContentObject();
                    received_instances+=1;
                    System.out.println("New result received. Number of received instances insofar: "+received_instances);
                }
                System.out.println("The results obtained for the test instances are: " + Arrays.toString(results));
            }

        } catch(Exception e){
            System.out.println("An error occurred in the userBehaviour");
        }
    }
}