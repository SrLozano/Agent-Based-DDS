package behaviours;

import agents.classifierAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.DenseInstance;
import weka.core.Attribute;
import java.util.ArrayList;

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
                String[] [] attr_vals = (String[][]) msg.getContentObject();
                String[] attributes = attr_vals[0];
                ArrayList<Attribute> atts = new ArrayList<Attribute>();
                for(int i=0; i<attributes.length; i++) {
                    atts.add(new Attribute(attributes[i]));
                }
                String[] vals = attr_vals[1];
                double[] values = new double[vals.length];
                for(int i=0; i<vals.length; i++) {
                    values[i] = Double.parseDouble(vals[i]);
                }
                Instances firm = new Instances("TestInstances", atts , 0);
                //firm.add(new DenseInstance(1.0, values));
                DenseInstance testInstance = new DenseInstance(1.0, values);
                //testInstance.setDataset(firm);
                double output = classifier.classifyInstance(testInstance);
                //Ahora mandar mensaje a voting system con (performance, output)
                //la performance pillarla de la classe classifier
                //https://www.tabnine.com/code/java/methods/weka.core.DenseInstance/%3Cinit%3E
            }
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }
}
