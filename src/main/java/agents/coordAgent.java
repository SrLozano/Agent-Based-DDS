package agents;

import behaviours.splitInputInstances;
import behaviours.sendTrainingInstances;
import behaviours.votingSystem;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;

public class coordAgent extends Agent{

    private String state; // [IDLE, Splitting_train, Splitting_test, Voting]

    protected void setup(){

        // Register petition
        DFAgentDescription dfd = new DFAgentDescription();

        // Service provided. As many services as desired can be added
        ServiceDescription sd = new ServiceDescription();

        // Basic setup of the service
        sd.setType("CoordinatorAgent");
        sd.setName(getName());
        sd.setOwnership("Group6");
        dfd.setName(getAID());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd );
            System.out.println("["+getLocalName()+"]:"+"DF Registered");
            addBehaviour(new splitInputInstances());
            addBehaviour(new sendTrainingInstances());
            addBehaviour(new votingSystem());
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

    public void setNameState(String state){
        this.state = state;
    }

    public String getNameState(){
        return this.state;
    }
}
