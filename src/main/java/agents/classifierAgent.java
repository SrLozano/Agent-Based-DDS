package agents;

import behaviours.Classification;
import jade.core.*;
import jade.core.behaviours.*;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;


public class classifierAgent extends Agent{
    protected void setup(){
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
            this.addBehaviour(new Classification());
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
}

