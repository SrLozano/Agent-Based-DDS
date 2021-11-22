package behaviours;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.Runtime;
import jade.core.AID;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.lang.acl.ACLMessage;


import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.Randomize;


public class trainClassifiers extends OneShotBehaviour{

    public void action() {
        try {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(System.getProperty("user.dir") + '/'+ "train_file.arff");
            Instances data = source.getDataSet();

            String[][] allarrays = // Es pot fer amb això: public class RandomSubset, no cal generar nosaltres la llista manually
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


            // Get a hold on JADE runtime
            Runtime rt = Runtime.instance();
            // Exit the JVM when there are no more containers around
            rt.setCloseVM(true);
            // Creating a new profile
            Profile p = new ProfileImpl(true);
            p.setParameter(Profile.MAIN_HOST, "localhost");
            p.setParameter(Profile.GUI, "true");
            // Creating a main container for the ClassifierAgents
            ContainerController ac = rt.createMainContainer(p);

            int count = 1;
            //Starting a loop 'for' for each of the packages that has to be sent to the classifier agents
            for (String[] indices : allarrays) {
                int[] indecesInstancesToTrainVal = new int[7]; // combinando ambas declaraciones en una

                for (int i = 0; i < indices.length; ++i) {
                    Attribute att = data.attribute(indices[i]);
                    indecesInstancesToTrainVal[i] = att.index(); //we add the attribute index to the list of attributes to select
                }

                Remove removeFilter = new Remove();
                removeFilter.setAttributeIndicesArray(indecesInstancesToTrainVal);
                removeFilter.setInvertSelection(true);
                removeFilter.setInputFormat(data);
                Instances splittrainval = Filter.useFilter(data, removeFilter);

                //shuffle of the instances
                Randomize randomize = new Randomize();
                randomize.setInputFormat(splittrainval);

                Instances trainval = new Instances(splittrainval, 0, 300);
                AgentController anotherAgent;
                try {
                    //Creating new classifierAgent. First argument is the name. Second argument is the class Agent.
                    anotherAgent = ac.createNewAgent("classifier" + count, "agents.classifierAgent", null);
                    anotherAgent.start();
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContentObject(trainval); //The content of the message it's the data
                AID dest = new AID("classifier" + count, AID.ISLOCALNAME);
                msg.addReceiver(dest); //The receiver is the coordinator Agent
                myAgent.send(msg); //The message is sent

                count = count + 1;
            }


        }
        catch (Exception e) {
            System.out.println("E");
            e.printStackTrace();
        }
    }
}
