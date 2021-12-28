package agents;

import behaviours.classifiersBehaviour;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.UnreadableException;
import weka.classifiers.trees.J48;
import jade.lang.acl.ACLMessage;
import weka.core.Instances;
import java.util.Random;
import weka.filters.unsupervised.instance.RemovePercentage;
import weka.filters.Filter;
import weka.classifiers.Evaluation;


public class classifierAgent extends Agent{
    private String name;
    private Double performance;
    private J48 model; // File location of the model

    /*
    public enum classifier_states {
        IDLE,
        TESTING,
    }

    public static classifier_states state;
    */

    protected void setup(){
        /* This is for setting arguments in the constructor*/
        Object[] args = getArguments();
        this.name = (String) args[0]; // this returns the String 1
        this.register();
        this.trainClassifier();
        this.addBehaviour(new classifiersBehaviour(this));
    }

    private void trainClassifier(){
        try {
            // THIS ACL MESSAGE IS NOT WELL RECEIVED; AND THUS NO FUNCIONA: HAY QUE MIRAR
            //System.out.println("Ola");

            ACLMessage msg = this.blockingReceive(); //Another option to receive a message is blockingReceive()
            //System.out.println("mensaje recibido:"+msg);

            // We select the count from the classifierAgent attributes. Format: classifier-2
            // int count = Integer.parseInt(myAgent.getNameAgent().split("-")[1]);
            //System.out.println(msg.getPerformative());
            if (msg.getPerformative() == ACLMessage.INFORM) {
                Object train_obj = null;
                // Get the instances to train from the message
                try {
                    train_obj = msg.getContentObject();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }

                //System.out.println(train_obj.getClass().getSimpleName());
                Instances trainval = (Instances) train_obj;
                ACLMessage reply = msg.createReply();

                // WHY THIS IF????? TODO: Please someone explains this better
                if (trainval.getClass() == Instances.class) {
                    //System.out.println("-" + this.getLocalName());

                    // We reply to the user that the message has been received
                    //reply.setPerformative(ACLMessage.INFORM); //If the user sends a Request --> Informs
                    //reply.setContent("The training data has been received");
                    //myAgent.send(reply);

                    //System.out.println("Classifier being trained");

                    double percentage = 25; // Percentage to validation purposes

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
                    //System.out.println("Performance = " + performance);
                    // Setting class attributes model and performance for classifier
                    this.setModel(classifier); //trained classifier
                    this.setPerformance(performance);
                    //this.setNameState(classifier_states.IDLE);
                }
            }
        } catch(Exception e){
            System.out.println("Model could not be trained correctly");
        }

    }
    private void register(){
        // Register petition
        DFAgentDescription dfd = new DFAgentDescription();
        // Service provided. As many services as desired can be added
        ServiceDescription sd = new ServiceDescription();

        // Basic setup of the service
        sd.setType("ClassifierAgent");
        sd.setName(getName());
        // Crec que es fa així per a donar una property als agents!
        Property state = new Property("State", 0);
        sd.addProperties(state);
        sd.setOwnership("Group6");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd );
            //System.out.println("["+getLocalName()+"]:"+"DF Registered");
            //System.out.println(this.getName());

        }
        catch (FIPAException fe) {
            System.out.println("["+getLocalName()+"]:"+"An error detected while trying to add the DF");
            doDelete(); }
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (jade.domain.FIPAException e) {
            e.printStackTrace();
        }
        super.takeDown();
    }

    /* Setters & Getters */
    public void setNameAgent(String name){
        this.name = name;
    }

    public String getNameAgent(){
        return this.name;
    }

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

    //public void setNameState(classifierAgent.classifier_states state){
      //  this.state = state;
    //}

    //public classifierAgent.classifier_states getNameState(){
      //  return this.state;
    //}

}

