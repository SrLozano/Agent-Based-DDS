package behaviours;

import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.core.Attribute;
import weka.filters.unsupervised.instance.Randomize;

public class SplitInputInstances extends CyclicBehaviour {

    public void action () {
        try {
            ACLMessage msg = myAgent.receive(); //Another option to receive a message is blockingReceive()
            if (msg.getPerformative() == ACLMessage.REQUEST) {
                Object data_obj = msg.getContentObject();
                Instances data = (Instances) data_obj; //Puede que dé error, comprobar que funcione
                ACLMessage reply = msg.createReply();
                if (data.getClass() == Instances.class) {
                    System.out.println("-" + myAgent.getLocalName());

                    //We reply to the user that the message has been received
                    reply.setPerformative(ACLMessage.INFORM); //If the user sends a Request --> Informs
                    reply.setContent("The data has been received");

                    // The array of arrays that defines the index of the attributes for each classifier
                    // All of them have index 24 as it is the class
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
                    int count = 0;
                    for (String[] indices : allarrays) { //Al ponerlo String[]
                        int [] indecesInstancesToTest = new int [6]; // combinando ambas declaraciones en una

                        for (int i = 0; i<indices.length; ++i) {
                            Attribute att = data.attribute(indices[i]);
                            indecesInstancesToTest [i] = att.index();
                        }

                        Remove removeFilter = new Remove();
                        removeFilter.setAttributeIndicesArray(indecesInstancesToTest);
                        removeFilter.setInvertSelection(true);
                        removeFilter.setInputFormat(data);
                        Instances splittest = Filter.useFilter(data, removeFilter);

                        //shuffle of the instances
                        Randomize randomize = new Randomize();
                        randomize.setInputFormat(splittest);

                        Instances test = new Instances (splittest, 0, 300);
                        System.out.println(test.size());

                        count = count+1;
                    }

                } else {
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("The message content is not instance class");
                }
                myAgent.send(reply);
            }
        }

        catch (Exception e) {
            //añadir más adelante un reinsert path si salta error
            e.printStackTrace();
            System.out.println("An error happened");
        }
    }


}
