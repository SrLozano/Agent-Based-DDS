package agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class testing_file {

    public static void main(String[] args){
        System.out.println("This file is use just to test code fragments. Should be remove in the final version of the file");

        try {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(System.getProperty("user.dir") + '/'+ "0input_user.arff");
            Instances test_data = source.getDataSet();

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
            for (int i = 0; i < test_data.size(); i++) {

                Instance firm = test_data.get(i);
                double [] aux = firm.toDoubleArray();
                int k = 0;
                String [] names = new String [aux.length]; // List with the firm attributes

                // The list with the firm attributes is filled
                for (int j = 0; j < aux.length; j++) { // For each attribute

                    // Only if the attribute is not missing it gets introduced in the array
                    if (aux[j] != 1000.0) {
                        names[k] = String.valueOf(firm.attribute(j)).split(" ")[1]; // The attribute vale is introduced
                        k+=1;
                    }
                }

                // An instance is passed to a classifier if it contains all the attributes for that particular instance
                int l=1; //classifier counter
                for (String[] attributes : allarrays){
                    // Lists are created to use containsAll function
                    List<Integer> nameList = new ArrayList (Arrays.asList(names));
                    List<Integer> attributesList = new ArrayList (Arrays.asList(attributes));

                    if (nameList.containsAll(attributesList)) {
                        System.out.println(l);
                        /*
                        System.out.println("The firm is sent to correspondent classifier");
                        //Send the agent with all the attributes the instance
                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        msg.setContentObject(aux); //The content of the message it's the firm data in array form
                        AID dest = new AID("classifier" + l, AID.ISLOCALNAME);
                        msg.addReceiver(dest); //The receiver is the coordinator Agent
                        myAgent.send(msg); //The message is sent
                        */
                    }
                    l+=1;
                }
            }
        }

        catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error happened");
        }
    }
}