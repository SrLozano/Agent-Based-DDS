package behaviours;

import agents.classifierAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import weka.classifiers.trees.J48;
import weka.core.Instance;

public class classifyInstance  extends OneShotBehaviour {

    private final classifierAgent myAgent;

    //constructor del behaviour:
    public classifyInstance(classifierAgent classifierAgent) {
        super(classifierAgent);
        myAgent = classifierAgent;
    }

    public void action() {
        String name = myAgent.getNameAgent();
        J48 classifier = myAgent.getModel(); //Getting the trained classifiers
        try {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                double test_data = (double) msg.getContentObject();
                Instance firm = (Instance) test_data;
                //ConverterUtils.DataSource source = new ConverterUtils.DataSource(System.getProperty("user.dir") + '/'+ "0input_user.arff");
                //Instances test_data = source.getDataSet();
                double output = classifier.classifyInstance(Instance firm);
            }
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }
}
