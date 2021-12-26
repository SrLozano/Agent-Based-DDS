package agents;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import behaviours.userBehaviour;
import jade.domain.FIPAException;

public class userAgent extends Agent{
    protected void setup(){
        this.register();
        addBehaviour(new userBehaviour(this));
    }

    private void register() {
        // Register petition
        DFAgentDescription dfd = new DFAgentDescription();
        // Service provided. As many services as desired can be added
        ServiceDescription sd = new ServiceDescription();
        // Basic setup of the service
        sd.setType("UserAgent");
        sd.setName(getName());
        sd.setOwnership("Group6");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this,dfd);
            System.out.println("["+getLocalName()+"]:"+"DF Registered");
        } catch (FIPAException e) {
            System.out.println("["+getLocalName()+"]:"+"An error detected while trying to add the DF (user agent)");
            doDelete();
        }
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