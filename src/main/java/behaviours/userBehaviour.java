/*****************************************************************
 userBehaviour is in charge of reading instances from test source and receiving the results to display them altogether.
 After that, the results of the classifiers are received, displayed together with ground truth and the overall accuracy
 of the system with the resulting training instances is obtained.

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

import jade.lang.acl.UnreadableException;
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
                        System.out.println("Data has been correctly read");

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
            collect_results(this.number_instances_test);
            this.number_instances_test = 0; //we set it back to 0 until new instances arrive

        } catch(Exception e){
            System.out.println("An error occurred in the userBehaviour");
        }
    }

    /* The results of the classifiers are received and accuracy computed */

    private void collect_results (int instances_to_test) {
        // It will be executed only if there are instances to classify
        if (instances_to_test != 0) {

            int received_instances = 0;
            double[] results = new double[instances_to_test];
            double[] real_labels = new double[instances_to_test];

            // Until all the results are received
            while (received_instances < instances_to_test) {
                try {
                    // Receives the results of the voting process done by the Coordinator Agent
                    ACLMessage msg_received = myAgent.blockingReceive();
                    Double[] received_msg = (Double[]) msg_received.getContentObject();
                    results[received_instances] = received_msg[0];
                    real_labels[received_instances] = received_msg[1];
                    received_instances += 1;
                    System.out.println("New result received. Number of received instances insofar: " + received_instances);
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
            }

            if (received_instances == instances_to_test) {
                System.out.println();
                System.out.println("The results obtained for the test instances are: " + Arrays.toString(results));
                System.out.println();
                System.out.println("The real labels of the test instances are: " + Arrays.toString(real_labels));

                // Counting the correct classifications
                int positive = 0;
                for (int i=0; i < received_instances; i++){
                    if (results[i] == real_labels[i]){
                        positive+=1;
                    }
                }

                // Computing the accuracy with the test instances
                float accuracy = (float) positive/received_instances;
                System.out.println();
                System.out.println("The accuracy with the test instances is: " + accuracy*100 + "%");
                System.out.println();
            }
        }
    }
}