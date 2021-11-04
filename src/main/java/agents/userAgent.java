package agents;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class userAgent extends Agent{
    protected void setup(){
        System.out.println("Agent "+ getLocalName() + " started.");
        // Add the CyclicBehaviour
        addBehaviour(new CyclicBehaviour(this){
            public void action() {
                System.out.println("Cycling");}
        });
    }

}
