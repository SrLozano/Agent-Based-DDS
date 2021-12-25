package behaviours;

import agents.coordAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class splitInputInstances extends CyclicBehaviour {
    private final coordAgent myAgent;

    // Constructor of the behaviour
    public splitInputInstances(coordAgent coordAgent) {
        super(coordAgent);
        this.myAgent = coordAgent;
    }

    public void action () {
        try {
            ACLMessage msg = myAgent.blockingReceive();
            System.out.println(msg.getSender().getName());
            AID user_ID = new AID("userAgent", AID.ISLOCALNAME);
            if (msg.getSender().getName().equals(user_ID.getName())) {
                myAgent.setNameState(coordAgent.global_states.TESTING);
                System.out.println(msg);
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
                                //{"Risk_A", "Score_B", "Score_MV", "District_Loss", "Inherent_Risk", "Detection_Risk"},
                                //{"PARA_B", "Risk_B", "Risk_D", "PROB", "CONTROL_RISK", "Audit_Risk"},
                                //{"Sector_score", "PARA_B", "TOTAL", "Risk_D", "RiSk_E", "CONTROL_RISK"},
                                //{"LOCATION_ID", "Risk_A", "numbers", "Score_MV", "History", "Inherent_Risk"},
                                //{"PARA_A", "Risk_B", "Risk_C", "PROB", "Risk_F", "Audit_Risk"},
                                //{"Score_A", "Score_B", "Money_Value", "District_Loss", "Score", "Detection_Risk"}
                        };

                // For every firm in the test file the correspondent classifiers are selected
                System.out.println("Number of instances to test: "+test_data.size());
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
                    int l = 1; //classifier counter
                    for (String[] attributes : allarrays) {
                        // Lists are created to use containsAll function
                        List<Integer> nameList = new ArrayList(Arrays.asList(names));
                        List<Integer> attributesList = new ArrayList(Arrays.asList(attributes));
                        //TODO: if classifier busy (not IDLE) wait to send the instance
                        if (nameList.containsAll(attributesList)) {
                            System.out.println("The firm is sent to correspondent classifier");
                            //Send the agent with all the attributes the instance
                            ACLMessage msg_to_send = new ACLMessage(ACLMessage.INFORM);

                            // Prepare message for the instance to be classified
                            String[] [] message = new String[3][];
                            message[0] = attributes;
                            String [] values = new String [aux.length];
                            String [] instance_id = new String [1]; //length 1 because we just need the num of instance
                            instance_id[0] = Double.toString(i+1);
                            for (int j = 0; j < aux.length; ++j){values[j] = Double.toString(aux[j]);}
                            message[1] = values;
                            message[2] = instance_id;
                            msg_to_send.setContentObject(message); //The content of the message it's the firm data in array form
                            AID dest = new AID("classifier-" + l, AID.ISLOCALNAME);
                            msg_to_send.addReceiver(dest); //The receiver is the coordinator Agent
                            myAgent.send(msg_to_send); //The message is sent
                            l +=1; // Notice that since l starts in 1 it indicates the number of classifiers right after the instances
                    myAgent.setNumber_classifiers(l); //number of classifiers working on that instance
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error happened");
        }
    }

}
