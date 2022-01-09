/*****************************************************************
 coordinatorBehaviour's role is to serve as a gateway between the user agent and the classification agents. Thus,
 it is in charge of splitting and sending the training instances for the corresponding classifiers and of serving
 as a decision system (i.e. gather the different classification outputs from the classifiers and emitting a single
 answer by a defined voting system). For this reason, it is composed by two different steps of the process:

    1. Each instance of the test is sent to the classifier capable of doing the classification.
    2. Receive results and starts a Voting process where the result for each instance is decided.

 @behaviour: coordinatorBehaviour

 @authors: Sergi Cirera, Iago Águila, Laia Borrell and Mario Lozano
 @group: 6 - IMAS - URV - UPC
 *****************************************************************/

package behaviours;

import agents.coordAgent;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


public class coordinatorBehaviour extends CyclicBehaviour {

    private final coordAgent myAgent;

    /* Class constructor for the behaviour. Agent is set as a class variable */

    public coordinatorBehaviour(coordAgent coordAgent) {
        super(coordAgent);
        this.myAgent = coordAgent;
    }

    /* Composed by two different steps of the process:
       1. Receives Test Dataset sent by the User Agent so each instance is prepared and sent to the classifier capable
          of doing the classification.
       2. Receives the classification results by the classifiers and starts a Voting process where the result for
          each instance is decided.*/

    public void action () {
        try {

            ACLMessage msg = myAgent.blockingReceive(); //Receives the test instances from the User Agent
            myAgent.setNameState(coordAgent.global_states.VOTING); //State of coordinator set to Voting
            AID user_ID = new AID("userAgent", AID.ISLOCALNAME);

            // Checking that the sender of the message is the User Agent
            if (msg.getSender().getName().equals(user_ID.getName())) {
                Instances test_data = (Instances) msg.getContentObject();

                String[][] allarrays =
                        {
                                {"Sector_score", "Risk_A", "TOTAL", "Score_MV", "RiSk_E", "Inherent_Risk", "Risk"},
                                {"LOCATION_ID", "PARA_B", "numbers", "Score_MV", "Risk_D", "CONTROL_RISK", "Risk"},
                                {"PARA_A", "Score_B", "Risk_C", "District_Loss", "Risk_F", "Detection_Risk", "Risk"},
                                {"Score_A", "Risk_B", "Money_Value", "PROB", "Score", "Audit_Risk", "Risk"},
                                {"Sector_score", "PARA_A", "TOTAL", "Risk_C", "RiSk_E", "Risk_F", "Risk"},
                                {"LOCATION_ID", "Score_A", "numbers", "Money_Value", "History", "Score", "Risk"},
                                {"Risk_A", "Score_B", "Score_MV", "District_Loss", "Inherent_Risk", "Detection_Risk", "Risk"},
                                {"PARA_B", "Risk_B", "Risk_D", "PROB", "CONTROL_RISK", "Audit_Risk", "Risk"},
                                {"Sector_score", "PARA_B", "TOTAL", "Risk_D", "RiSk_E", "CONTROL_RISK", "Risk"},
                                {"LOCATION_ID", "Risk_A", "numbers", "Score_MV", "History", "Inherent_Risk", "Risk"},
                                {"PARA_A", "Risk_B", "Risk_C", "PROB", "Risk_F", "Audit_Risk", "Risk"},
                                {"Score_A", "Score_B", "Money_Value", "District_Loss", "Score", "Detection_Risk", "Risk"}
                        };

                // Read properties.xml file
                java.util.Properties prop = new Properties();
                try {
                    FileInputStream fin = new FileInputStream(System.getProperty("user.dir") + '/' + "properties.xml");
                    prop.loadFromXML(fin);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int num_classifiers = Integer.parseInt(prop.getProperty("numclassifiers"));
                String[][] final_attributes_list = new String [num_classifiers][7];
                System.arraycopy(allarrays, 0, final_attributes_list, 0, num_classifiers);

                // For every firm in the test file the correspondent classifiers are selected
                for (int i = 0; i < test_data.size(); i++) { // For every instance in the test data
                    Instance firm = test_data.get(i);
                    int k = 0;
                    String[] names = new String[firm.numValues()-5]; // List with the firm containing attributes
                    for (int j = 0; j < firm.numValues(); j++) { // For each attribute
                        // Only if the attribute is not missing it gets introduced in the array
                        if (!(firm.value(j) ==1000.0 || (firm.value(j)==45.0 && String.valueOf(firm.attribute(j)).split(" ")[1].equals("LOCATION_ID")))) { //The 45 is because of an issue with the location ID attribute
                            names[k] = String.valueOf(firm.attribute(j)).split(" ")[1]; // The attribute name is introduced
                            k += 1;
                        }
                    }

                    // An instance is passed to a classifier if it contains all the attributes for that particular instance
                    int l = 0; // Active classifiers counter
                    int c = 0; // Identifier of the classifier agent studied in the for loop
                    for (String[] attributes : final_attributes_list) {

                        // Attributes that we filter:
                        int[] indexesInstancesToTest = new int[attributes.length];

                        // We add the attribute index to the list of attributes to select
                        for (int w = 0; w < attributes.length; ++w) {
                            Attribute att = test_data.attribute(attributes[w]);
                            indexesInstancesToTest[w] = att.index(); //Attributes to keep
                        }

                        // Selecting the firm of the dataset_test and filtering to keep the correspondent attributes
                        Instances dataset_test = new Instances (test_data,i,1);
                        Remove removeFilter = new Remove();
                        removeFilter.setAttributeIndicesArray(indexesInstancesToTest);
                        removeFilter.setInvertSelection(true);
                        removeFilter.setInputFormat(dataset_test);
                        Instances filtered_test = Filter.useFilter(dataset_test, removeFilter);

                        // Lists are created to use containsAll function
                        List<Integer> nameList = new ArrayList(Arrays.asList(names));
                        List<Integer> attributesList = new ArrayList(Arrays.asList(attributes));

                        //Checking if the classifier attributes are all present in the instance
                        if (nameList.containsAll(attributesList)) {
                            ACLMessage msg_to_send = new ACLMessage(ACLMessage.INFORM);
                            msg_to_send.setContentObject(filtered_test); // The content of the message it's the firm data
                            AID dest = new AID("classifier-" + c, AID.ISLOCALNAME);
                            msg_to_send.addReceiver(dest); // The receiver is the corresponding classifier Agent
                            myAgent.send(msg_to_send); // The message is sent
                            l += 1; // l indicates the total number of classifiers active
                        }
                        c += 1;
                    }
                    myAgent.setNumber_classifications(l);

                    // With all the instances sent, we start the voting
                    double trueLabel = firm.value(firm.numValues()-1);
                    voting(trueLabel);
                }
                // We set the state back to idle when all instances have been classified
                myAgent.setNameState(coordAgent.global_states.IDLE);
            }
        }

        catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error happened");
        }
    }

    /* The voting system receives the performance of each classifier and assigns a proportional weight to that vote.
    After that, the final vote is computed considering that if the result is bigger or equal than 0.5 it means that
    the agents agree that the instance should be classified as 1. Otherwise, they return a 0.*/

    private void voting (double trueLabel) {

        int number_classifiers = myAgent.getNumber_classifications();
        // Arrays to collect performances and classifications from classifiers
        double[] performances = new double[number_classifiers];
        double[] classifications = new double[number_classifiers];
        int responses = 0;

        // Responses are obtained until all classifiers vote
        while (responses < number_classifiers) {
            try {
                ACLMessage msg_received = myAgent.blockingReceive();
                // Message contains a double array with [performance, classification]
                Double[] response = (Double[]) msg_received.getContentObject();
                performances[responses] = response[0];
                classifications[responses] = response[1];
                responses += 1;

            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }

        // We get the sum of the performances so each one could have a good weight
        double sum_performances = 0;
        for (double performance : performances) {
            sum_performances += performance;
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
        Double[] message = new Double[2];
        message[0] = final_result;
        message[1] = trueLabel;
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
