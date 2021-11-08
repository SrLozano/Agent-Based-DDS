package agents;

import jade.core.*;
import jade.core.behaviours.*;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;


public class coordinatorAgent extends Agent{
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
        }
        catch (FIPAException fe) {
            fe.printStackTrace(); }
    }


}



