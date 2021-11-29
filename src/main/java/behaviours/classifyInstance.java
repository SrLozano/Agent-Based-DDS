package behaviours;

import agents.classifierAgent;
import jade.core.behaviours.OneShotBehaviour;

public class classifyInstance  extends OneShotBehaviour {

    private final classifierAgent myAgent;
    //constructor del behaviour:
    public classifyInstance(classifierAgent classifierAgent) {
        super(classifierAgent);
        this.myAgent = classifierAgent;
    }

    public void action() {
        String name = this.myAgent.getNameAgent();
    }


}
