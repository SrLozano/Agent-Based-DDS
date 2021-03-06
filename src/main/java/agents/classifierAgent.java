/*****************************************************************
 Each classifiers' agent is responsible for classifying as fraudulent or legal a new observation or enterprise after
 being trained with a reduced set of instances. Thus, it will have a J48 Weka model trained after having received
 the training instances as a message from the coordinator agent. In the process, the training performance of the
 classifier is obtained so that depending on this metric a weight is associated with this classifier in the voting
 system. Moreover, a classifier agent is in charge of classifying and instance when it receives one.

 @agent: classifier

 @authors: Sergi Cirera, Iago Águila, Laia Borrell and Mario Lozano
 @group: 6 - IMAS - URV - UPC
 *****************************************************************/

package agents;

import behaviours.classifiersBehaviour;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.UnreadableException;
import jade.lang.acl.ACLMessage;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.unsupervised.instance.RemovePercentage;
import weka.filters.Filter;
import weka.classifiers.Evaluation;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class classifierAgent extends Agent{
    private String name;
    private Double performance;
    private J48 model;

    /* Set initial configuration and behaviours for agent */

    protected void setup(){
        Object[] args = getArguments(); // For setting arguments in the constructor
        this.name = (String) args[0]; // This returns the name identifier. Just informative
        this.register();
        this.trainClassifier(); // Classifier gets train with train and validation
        this.addBehaviour(new classifiersBehaviour(this)); // Classifier behaviour added
    }

    /* Train classifier with train data received by message and set model and performance */

    private void trainClassifier(){
        try {
            ACLMessage msg = this.blockingReceive(); // Wait until train object if received

            if (msg.getPerformative() == ACLMessage.INFORM) {
                Object train_obj = null;

                // Get the instances to train from the message
                try {
                    train_obj = msg.getContentObject();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }

                Instances trainval = (Instances) train_obj;

                if (trainval != null) { // Avoid NullPointer Exception
                    double percentage = 25; // Percentage for validation purposes

                    Random random = new Random(42);
                    trainval.randomize(random);

                    // Split between train and validation
                    RemovePercentage rp = new RemovePercentage();
                    rp.setInputFormat(trainval);
                    rp.setPercentage(percentage);
                    Instances train = Filter.useFilter(trainval, rp); // 100 - percentage for  training
                    RemovePercentage rp_validation = new RemovePercentage();
                    rp_validation.setInputFormat(trainval);
                    rp_validation.setPercentage(percentage);

                    // With InvertSelection percentage for validation
                    rp_validation.setInvertSelection(true);
                    Instances validation = Filter.useFilter(trainval, rp_validation);

                    // Setting class attribute if the data format does not provide this information
                    train.setClassIndex(train.numAttributes() - 1);

                    // Setting class attribute if the data format does not provide this information
                    validation.setClassIndex(validation.numAttributes() - 1);

                    // Train classifier and save it for testing
                    J48 classifier = new J48();
                    classifier.buildClassifier(train);

                    // Validate classifier for testing
                    Evaluation eval = new Evaluation(validation);
                    eval.evaluateModel(classifier, validation);
                    double performance = (eval.correct() / validation.numInstances()) * 100;

                    // Setting class attributes model and performance for classifier
                    this.setModel(classifier); // Trained classifier
                    this.setPerformance(performance);
                }
            }
        } catch(Exception e){
            System.out.println("Model " + name +  " could not be trained correctly");
        }
    }

    /* Register agent into the Jade System */

    private void register(){
        // Register petition
        DFAgentDescription dfd = new DFAgentDescription();

        // Service provided. As many services as desired can be added
        ServiceDescription sd = new ServiceDescription();
        java.util.Properties prop = new Properties();

        // Read properties.xml file
        try {
            FileInputStream fin = new FileInputStream(System.getProperty("user.dir") + '/' + "properties.xml");
            prop.loadFromXML(fin);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Basic setup of the service
        sd.setType(prop.getProperty("classifiername"));
        sd.setName(getName());
        Property state = new Property("State", 0);
        sd.addProperties(state);
        sd.setOwnership(prop.getProperty("group"));

        // Finish agent description
        dfd.setName(getAID());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd );
            System.out.println("["+getLocalName()+"]:"+" DF Registered - classifier agent");
        } catch (FIPAException fe) {
            System.out.println("["+getLocalName()+"]:" + "An error detected while trying to add the DF - classifier");
            fe.printStackTrace();
            doDelete();
        }
    }

    /* Take down agent by unregistering it from DF */

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (jade.domain.FIPAException e) {
            e.printStackTrace();
        }
        super.takeDown();
    }

    /* Setters & Getters */

    public void setPerformance(double performance){
        this.performance = performance;
    }

    public double getPerformance(){
        return this.performance;
    }

    public void setModel(J48 model){
        this.model = model;
    }

    public J48 getModel(){
        return this.model;
    }
}