package agents;
<<<<<<< HEAD

public class coordinatorAgent {
}
=======
import jade.core.*;
import jade.core.behaviours.*;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;


public class coordinatorAgent extends Agent{
    protected void setup(){
        System.out.println("Agent "+ getLocalName() + " started.");
        // Registration with the DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName( getAID() );

        ServiceDescription sd  = new ServiceDescription();
        sd.setType( "Coordinator" );
        sd.setName( getLocalName() );

        dfd.addServices(sd);

        try {
            DFService.register(this, dfd );
        }
        catch (FIPAException fe) {
            fe.printStackTrace(); }
    }


}


>>>>>>> edd9d0d435b9eb020a683446b627cb059dd58499
