package agents;

import behaviours.trainClassifiers;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import weka.classifiers.trees.J48;

public class classifierAgent extends Agent{
    private String name;
    private Double performance;
    private J48 model; // File location of the model
    private String state; // [IDLE, Classifying]
    private String train_state; // [notTrained, trained]

    protected void setup(){

        /* This is for setting arguments in the constructor*/
        Object[] args = getArguments();
        this.name = (String) args[0]; // this returns the String 1

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
            System.out.println("["+getLocalName()+"]:"+"DF Registered");
            System.out.println(this.getName());

            this.addBehaviour(new trainClassifiers(this));
            //this.addBehaviour(new classifyInstance(this));
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

    public void setNameState(String state){
        this.state = state;
    }

    public String getNameState(){
        return this.state;
    }

    public void setTrainState(String train_state){
        this.train_state = train_state;
    }

    public String getTrainState(){
        return this.train_state;
    }

}

