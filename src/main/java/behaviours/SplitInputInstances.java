package behaviours;

import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;



public class SplitInputInstances extends CyclicBehaviour {

    public void action () {
        try {
            ACLMessage msg = myAgent.receive(); //Another option to receive a message is blockingReceive()
            if (msg.getPerformative()==ACLMessage.REQUEST)
            {
                String content = msg.getContent();
                ACLMessage reply = msg.createReply();
                if (content!=null) {
                    System.out.println("-" + myAgent.getLocalName() + " <- " + msg.getContent());

                    //We reply to the user that the message has been received
                    reply.setPerformative(ACLMessage.INFORM); //If the user sends a Request --> Informs
                    reply.setContent("The data has been received");
                }
                else{
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("The message content is null");
                    }
                myAgent.send(reply);

                }

        }
        catch (Exception e) {
            //añadir más adelante un reinsert path si salta error
            e.printStackTrace();
            System.out.println("An error occured");
        }
    }


}
