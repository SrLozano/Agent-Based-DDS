package agents;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.lang.acl.ACLMessage;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.Randomize;

class testing_file {

    public static void main(String[] args){
        System.out.println("This file is use just to test code fragments. Should be remove in the final version of the file");

        try {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(System.getProperty("user.dir") + '/'+ "0input_user.arff");
            Instances test_data = source.getDataSet();

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

            //System.out.print(test_data);
            System.out.println(test_data.enumerateAttributes().toString());
            // Each loop in the for loop represents one classifier
            for (String[] indices : allarrays) { //Al ponerlo String[]

            }
        }


        catch (Exception e) {
            //añadir más adelante un reinsert path si salta error
            e.printStackTrace();
            System.out.println("An error occured");
        }
    }
}