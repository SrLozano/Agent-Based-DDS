/*****************************************************************
 TODO: Fill description of this file. Explain everything
 @agent: user

 @authors: Sergi Cirera, Iago Águila, Laia Borrell and Mario Lozano
 @group: 6 - IMAS - URV - UPC
 *****************************************************************/

package agents;

import behaviours.userBehaviour;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class userAgent extends Agent{

    /* Set initial configuration and behaviours for agent */

    protected void setup(){
        this.register();
        addBehaviour(new userBehaviour(this)); // User behaviour added
    }

    /* Register agent into the Jade System */

    private void register() {
        // Register petition
        DFAgentDescription dfd = new DFAgentDescription();

        // Service provided. As many services as desired can be added
        ServiceDescription sd = new ServiceDescription();

        // Basic setup of the service
        sd.setType("UserAgent");
        sd.setName(getName());
        sd.setOwnership("Group6");

        // Finish agent description
        dfd.setName(getAID());
        dfd.addServices(sd);

        try {
            DFService.register(this,dfd);
            System.out.println("["+getLocalName()+"]:"+" DF Registered - user agent");
        } catch (FIPAException e) {
            System.out.println("["+getLocalName()+"]:"+"An error detected while trying to add the DF - user");
            e.printStackTrace();
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
}