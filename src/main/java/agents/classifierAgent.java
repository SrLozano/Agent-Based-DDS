package agents;

import jade.core.Agent;
import weka.core.converters.ConverterUtils.DataSource;

public class classifierAgent extends Agent {
    public static void main(String[] args) throws Exception {
        DataSource source = new DataSource("../../audit_risk.arff");
        if(source != null){
            System.out.println("Read");
        }
    }
}


