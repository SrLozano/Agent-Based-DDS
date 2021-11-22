package agents;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.lang.acl.ACLMessage;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.Randomize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
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


            // Each loop in the for loop represents one classifier
            for (int i = 0; i<test_data.size(); ++i) { //for each row or instance
                Instance firm = test_data.get(i);
                double [] aux = firm.toDoubleArray();
                int k = 0;
                String [] names = new String [aux.length]; //list with the firm attributes

                for (int j = 0; j<aux.length; j++) { //for each attribute
                    if (aux[j] != 1000.0) { //check if the attribute is not missing
                        names[k] = String.valueOf(firm.attribute(j)).split(" ")[1];
                        k+=1;
                    }
                }
                // An instance is passed to a classifier if it contains all the attributes for that particular instance
                for (String[] attributes : allarrays){
                    for (String value1 : attributes){
                        //System.out.println(value1);
                        //if (names.contains(value1)) {
                            //System.out.println("HOLA");
                       // }
                        /*for (String value2 : names) {
                            //System.out.println(value2);
                            if (value1.equals(value2)){
                                System.out.println("works");
                            }
                        }*/

                    }
                    System.out.println(Arrays.asList(names));
                    System.out.println(Arrays.asList(attributes));
                    if (Arrays.asList(names).containsAll(Arrays.asList(attributes))) {
                        // The instance should be sent to the classifier
                        //System.out.println(names);
                        //System.out.println(attributes);
                    }
                }
            }

        }

        catch (Exception e) {
            //añadir más adelante un reinsert path si salta error
            e.printStackTrace();
            System.out.println("An error occured");
        }
    }
}