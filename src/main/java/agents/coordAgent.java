/*****************************************************************
 TODO: Fill description of this file. Explain everything
 @agent: coordinator

 @authors: Sergi Cirera, Iago Águila, Laia Borrell and Mario Lozano
 @group: 6 - IMAS - URV - UPC
 *****************************************************************/

package agents;

import behaviours.coordinatorBehaviour;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.Randomize;

public class coordAgent extends Agent{

    // Define structure of possible states -> State machine
    public enum global_states {
        IDLE, // Trained and waiting for instances to classify
        VOTING, // Splitting instances and voting
    }

    public static global_states state; // Should be accessed from other classes
    private int number_classifications;

    /* Set initial configuration and behaviours for agent */

    protected void setup() {
        this.register();
        this.sendTrainingInstances(this); // Agents are trained with a series of attributes
        this.addBehaviour(new coordinatorBehaviour(this)); // Coordinator behaviour added
    }

    /* Send corresponding instances to classifier so they can get trained */

    private void sendTrainingInstances(coordAgent agent){
        try {
            // Get data from source
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(System.getProperty("user.dir") + '/' + "train_file.arff");
            Instances data = source.getDataSet();

            // TODO: This can be improved. Manage this by class attributes and add classifiers commenced out. The list can be generated randomly by public class RandomSubset not by hand by ourselves

            String[][] attributes_list =
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

            int count = 0;

            // Starting a loop for each one of the packages that have to be sent to the classifier agent
            for (String[] indexes : attributes_list) {

                int[] indexesInstancesToTrainVal = new int [indexes.length]; // Array for the indexes to be sent

                // We add the attribute index to the list of attributes to select
                for (int i = 0; i < indexes.length; ++i) {
                    Attribute att = data.attribute(indexes[i]);
                    indexesInstancesToTrainVal[i] = att.index();
                }

                Remove removeFilter = new Remove();
                removeFilter.setAttributeIndicesArray(indexesInstancesToTrainVal);
                removeFilter.setInvertSelection(true);
                removeFilter.setInputFormat(data);
                Instances splittrainval = Filter.useFilter(data, removeFilter);

                // Shuffle of the instances -> Avoid bias
                Randomize randomize = new Randomize();
                randomize.setInputFormat(splittrainval);

                // TODO: Why 300? Explain it
                Instances trainval = new Instances(splittrainval, 0, 300);

                AgentController anotherAgent;

                // Create new classifierAgent for the set of attributes selected
                try {
                    // Arguments: Name, Class Agent. Classifiers are identified by classifier-X where X is a number
                    anotherAgent = agent.getContainerController().createNewAgent("classifier-" + count, "agents.classifierAgent", new String[]{"classifier-" + count});
                    anotherAgent.start();
                } catch (StaleProxyException e) {
                    System.out.println("Exception while creating agent classifier-" + count);
                    e.printStackTrace();
                }

                // Create and send message containing data to train with
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContentObject(trainval); // The content of the message it's the data to train with
                AID dest = new AID("classifier-" + count, AID.ISLOCALNAME);
                msg.addReceiver(dest); // The receiver is the corresponding classifier
                this.send(msg); // The message is sent
                count +=1;
            }

            // Classifiers are created and trained. System waits for instances to classify
            this.setNameState(global_states.IDLE);

        } catch (Exception e) {
            System.out.println("An error detected while training the classifiers");
            e.printStackTrace();
        }
    }

    /* Register agent into the Jade System */

    private void register() {
        // Register petition
        DFAgentDescription dfd = new DFAgentDescription();

        // Service provided. As many services as desired can be added
        ServiceDescription sd = new ServiceDescription();

        // Basic setup of the service
        sd.setType("CoordinatorAgent");
        sd.setName(getName());
        sd.setOwnership("Group6");

        // Finish agent description
        dfd.setName(getAID());
        dfd.addServices(sd);

        try {
            DFService.register(this,dfd);
            System.out.println("["+getLocalName()+"]:"+"DF Registered - coordinator agent");
        } catch (FIPAException e) {
            System.out.println("["+getLocalName()+"]:" + "An error detected while trying to add the DF - coordinator");
            e.printStackTrace();
            doDelete();
        }
    }

    /* Take down behaviour by unregistering it from DF */

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (jade.domain.FIPAException e) {
            e.printStackTrace();
        }
        super.takeDown();
    }

    /* Setters & Getters */

    public void setNameState(global_states state){
        this.state = state;
    }

    public void setNumber_classifications(int number_classifiers){
        this.number_classifications = number_classifiers;
    }

    public int getNumber_classifications(){
        return this.number_classifications;
    }
}