package behaviours;

import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

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

                    // Each loop in the for loop represents one classifier
                    int count = 0;
                    for (String[] indices : allarrays) { //Al ponerlo String[]
                        Remove removeFilter = new Remove();
                        removeFilter.setAttributeIndices(String.valueOf(indices));
                        removeFilter.setInvertSelection(true);
                        removeFilter.setInputFormat(data);
                        Instances splittrain = Filter.useFilter(data, removeFilter);

                        Profile p = new ProfileImpl(true);
                        //ac.getLocation();
                        //Creating new classifierAgent. First argument is the name. Second argument is the class Agent.
                        //anotherAgent = ac.createNewAgent("classifier"+count, "classifierAgent");

                        //anotherAgent.start();
                        //count = count+1;

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
            System.out.println("An error occured");
        }
    }


}
