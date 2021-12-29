package behaviours;

import agents.coordAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class coordinatorBehaviour extends CyclicBehaviour {
    private final coordAgent myAgent;

    // Constructor of the behaviour
    public coordinatorBehaviour(coordAgent coordAgent) {
        super(coordAgent);
        this.myAgent = coordAgent;
    }

    public void action () {
        try {
            // System.out.println("MEGA TESTTTTTTTTTTT");
            ACLMessage msg = myAgent.blockingReceive();
            // System.out.println("MEGA TESTTTTTTTTTTT AFTER BLOCKING");

            // System.out.println(msg.getSender().getName());

            AID user_ID = new AID("userAgent", AID.ISLOCALNAME);
            if (msg.getSender().getName().equals(user_ID.getName())) {
                // System.out.println(msg);
                Instances test_data = (Instances) msg.getContentObject();
                // ConverterUtils.DataSource source = new ConverterUtils.DataSource(System.getProperty("user.dir") + '/'+ "0input_user.arff");
                //Instances test_data = source.getDataSet();

                String[][] allarrays =
                        {
                                {"Sector_score", "Risk_A", "TOTAL", "Score_MV", "RiSk_E", "Inherent_Risk"},
                                {"LOCATION_ID", "PARA_B", "numbers", "Score_MV", "Risk_D", "CONTROL_RISK"},
                                {"PARA_A", "Score_B", "Risk_C", "District_Loss", "Risk_F", "Detection_Risk"},
                                {"Score_A", "Risk_B", "Money_Value", "PROB", "Score", "Audit_Risk"},
                                {"Sector_score", "PARA_A", "TOTAL", "Risk_C", "RiSk_E", "Risk_F"},
                                {"LOCATION_ID", "Score_A", "numbers", "Money_Value", "History", "Score"},
                                {"Risk_A", "Score_B", "Score_MV", "District_Loss", "Inherent_Risk", "Detection_Risk"},
                                {"PARA_B", "Risk_B", "Risk_D", "PROB", "CONTROL_RISK", "Audit_Risk"},
                                {"Sector_score", "PARA_B", "TOTAL", "Risk_D", "RiSk_E", "CONTROL_RISK"},
                                {"LOCATION_ID", "Risk_A", "numbers", "Score_MV", "History", "Inherent_Risk"},
                                {"PARA_A", "Risk_B", "Risk_C", "PROB", "Risk_F", "Audit_Risk"},
                                {"Score_A", "Score_B", "Money_Value", "District_Loss", "Score", "Detection_Risk"}
                        };

                // For every firm in the test file the correspondent classifiers are selected
                System.out.println("Number of instances to test: " + test_data.size());

                for (int i = 0; i < test_data.size(); i++) {
                    Instance firm = test_data.get(i);
                    double[] aux = firm.toDoubleArray();
                    int k = 0;
                    String[] names = new String[aux.length]; // List with the firm attributes

                    // The list with the firm attributes is filled
                    for (int j = 0; j < aux.length; j++) { // For each attribute

                        // Only if the attribute is not missing it gets introduced in the array
                        if (aux[j] != 1000.0) {
                            names[k] = String.valueOf(firm.attribute(j)).split(" ")[1]; // The attribute name is introduced
                            k += 1;
                        }
                    }

                    //TODO: (si tenemos tiempo) CFP instead de iterar por todos los classifiers para ver si pueden clasificar los atributos

                    // An instance is passed to a classifier if it contains all the attributes for that particular instance
                    int l = 0; //active classifiers counter
                    int c = 0; //identifier of the classifier agent studied in the for loop
                    for (String[] attributes : allarrays) {
                        // Lists are created to use containsAll function
                        List<Integer> nameList = new ArrayList(Arrays.asList(names));
                        List<Integer> attributesList = new ArrayList(Arrays.asList(attributes));

                        if (nameList.containsAll(attributesList)) {
                            System.out.println("The firm is sent to correspondent classifier");
                            //Send the agent with all the attributes the instance
                            ACLMessage msg_to_send = new ACLMessage(ACLMessage.INFORM);

                            // Prepare message for the instance to be classified
                            String[][] message = new String[3][];
                            message[0] = attributes;
                            String[] values = new String[aux.length];
                            String[] instance_id = new String[1]; //length 1 because we just need the num of instance
                            instance_id[0] = Double.toString(i + 1);
                            for (int j = 0; j < aux.length; ++j) {
                                values[j] = Double.toString(aux[j]);
                            }
                            message[1] = values;
                            message[2] = instance_id;
                            msg_to_send.setContentObject(message); //The content of the message it's the firm data in array form
                            AID dest = new AID("classifier-" + c, AID.ISLOCALNAME);
                            msg_to_send.addReceiver(dest); //The receiver is the coordinator Agent

                            //System.out.println("PRINT ANTES DEL SEND to classifier " + c);

                            myAgent.send(msg_to_send); //The message is sent
                            l += 1; // l indicates the total number of classifiers active

                            myAgent.setNameState(coordAgent.global_states.VOTING); //after sending an instance we set it to votin
                            // System.out.println("BLOCKED");
                            // myAgent.blockingReceive(MessageTemplate.MatchContent("continue"));
                            // System.out.println("UNBLOCKED");
                        }
                        c += 1;
                    }
                    //System.out.println("l = " + l);
                    myAgent.setNumber_classifications(l);
                    voting();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error happened");
        }
    }

    public void voting () {
        // System.out.println(myAgent.getNameState());
        //System.out.println("ESTAMOS EN VOTING");

        int number_classifiers = myAgent.getNumber_classifications();
        // Arrays to collect performances and classifications from classifiers
        double[] performances = new double[number_classifiers];
        double[] classifications = new double[number_classifiers];

        int responses = 0;
        // Responses are obtained until all classifiers vote
        while (responses < number_classifiers) {
            try {
                //System.out.println("Responses in while: "+ responses);
                //System.out.println("NumberClassifiers in while: "+ number_classifiers);
                ACLMessage msg_received = myAgent.blockingReceive();
                // Message contains a double array with [performance, classification, num_of_instance]
                String[] response = (String[]) msg_received.getContentObject();
                //System.out.println("Message content: " + response);
                performances[responses] = Double.parseDouble(response[0]);
                classifications[responses] = Double.parseDouble(response[1]);
                int instance_num =  (int) Double.parseDouble(response[2]);
                responses += 1;
                //System.out.println("Responses post sum: "+ responses);
                if (instance_num == 14) { //when it has received the results of all instances set to idle so it does not enter again this behaviour until new input
                    myAgent.setNameState(coordAgent.global_states.IDLE);
                }

            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }

        double sum_performances = 0;

        // We get the sum of the performances so each one could have a good weight
        for (int i = 0; i < performances.length; ++i) {
            sum_performances += performances[i];
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
        System.out.println("Classification is: " + final_result);
        ACLMessage msg_toSend = new ACLMessage(ACLMessage.INFORM);
        double message = final_result;
        try {
            msg_toSend.setContentObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AID dest = new AID("userAgent", AID.ISLOCALNAME);
        msg_toSend.addReceiver(dest); //The receiver is the coordinator Agent
        myAgent.send(msg_toSend); //The message is sent

        System.out.println("AQUI CAMBIO");
        myAgent.setNameState(coordAgent.global_states.IDLE);

    }
}
