package behaviours;
import agents.classifierAgent;
import agents.coordAgent;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.UnreadableException;

import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.classifiers.trees.J48;
import weka.filters.unsupervised.instance.RemovePercentage;
import weka.filters.Filter;

import java.util.ArrayList;
import java.util.Random;
import jade.lang.acl.ACLMessage;
import java.io.IOException;  // Import the IOException class to handle errors

public class classifiersBehaviour extends CyclicBehaviour {

    private final classifierAgent myAgent;

    // Constructor of the behaviour
    public classifiersBehaviour(classifierAgent classifierAgent) {
        super(classifierAgent);
        this.myAgent = classifierAgent;
    }

    public void action() {
        System.out.println(coordAgent.state);
        coordAgent.global_states stateString = coordAgent.state;
        System.out.println(stateString);
        //TODO: How to call the states defined in the coordinator agent?
        switch (stateString){ //My agent has to be the coordinator
            case TRAIN:{

                try {
                    // THIS ACL MESSAGE IS NOT WELL RECEIVED; AND THUS NO FUNCIONA: HAY QUE MIRAR
                    //System.out.println("Ola");

                    ACLMessage msg = myAgent.blockingReceive(); //Another option to receive a message is blockingReceive()
                    //System.out.println("mensaje recibido:"+msg);
                    System.out.println("AgentReceivedMessage");

                    // We select the count from the classifierAgent attributes. Format: classifier-2
                    // int count = Integer.parseInt(myAgent.getNameAgent().split("-")[1]);
                    //System.out.println(msg.getPerformative());
                    if (msg.getPerformative() == ACLMessage.INFORM) {
                        Object train_obj = null;
                        // Get the instances to train from the message
                        try {
                            train_obj = msg.getContentObject();
                        } catch (UnreadableException e) {
                            e.printStackTrace();
                        }

                        //System.out.println(train_obj.getClass().getSimpleName());
                        Instances trainval = (Instances) train_obj;
                        ACLMessage reply = msg.createReply();

                        // WHY THIS IF????? TODO: Please someone explains this better
                        if (trainval.getClass() == Instances.class) {
                            System.out.println("-" + myAgent.getLocalName());

                            // We reply to the user that the message has been received
                            reply.setPerformative(ACLMessage.INFORM); //If the user sends a Request --> Informs
                            reply.setContent("The training data has been received");
                            myAgent.send(reply);

                            System.out.println("Classifier being trained");

                            double percentage = 25; // Percentage to validation purposes

                            Random random = new Random(42);
                            trainval.randomize(random);

                            // Split between train and validation
                            RemovePercentage rp = new RemovePercentage();
                            rp.setInputFormat(trainval);
                            rp.setPercentage(percentage);
                            Instances train = Filter.useFilter(trainval, rp); // 100 - percentage for  training

                            RemovePercentage rp_validation = new RemovePercentage();
                            rp_validation.setInputFormat(trainval);
                            rp_validation.setPercentage(percentage);
                            // With InvertSelection percentage for validation
                            rp_validation.setInvertSelection(true);
                            Instances validation = Filter.useFilter(trainval, rp_validation);

                            // Setting class attribute if the data format does not provide this information
                            train.setClassIndex(train.numAttributes() - 1);

                            // Setting class attribute if the data format does not provide this information
                            validation.setClassIndex(validation.numAttributes() - 1);


                            // Train classifier and save it for testing
                            J48 classifier = new J48();
                            classifier.buildClassifier(train);

                            // With our agent's name got from the attribute of its class
                            AID id = new AID(myAgent.getNameAgent(), AID.ISLOCALNAME);

                            // Validate classifier for testing
                            Evaluation eval = new Evaluation(validation);
                            eval.evaluateModel(classifier, validation);
                            double performance = (eval.correct() / validation.numInstances()) * 100;
                            System.out.println("Performance = " + performance);
                            // Setting class attributes model and performance for classifier
                            this.myAgent.setModel(classifier); //trained classifier
                            this.myAgent.setPerformance(performance);
                        }
                    }
                } catch(Exception e){
                    System.out.println("Model could not be trained correctly");
               }
                break;}
            case VOTING:
            {
                System.out.println("VOTING");
                System.out.println("VOTING STATE = " + stateString);
                break;
            }
            case TEST:
            {
                String name = myAgent.getNameAgent();
                J48 classifier = myAgent.getModel(); //Getting the trained classifiers
                try {
                    System.out.println("OlaTest");
                    System.out.println("Test STATE = " + stateString);

                    block();
                    ACLMessage msg = myAgent.receive();

                    if (msg != null) {
                        String[][] attr_vals = (String[][]) msg.getContentObject();
                        String[] attributes = attr_vals[0];
                        ArrayList<Attribute> atts = new ArrayList<Attribute>();
                        for (int i = 0; i < attributes.length; i++) {
                            atts.add(new Attribute(attributes[i]));
                        }

                        String[] vals = attr_vals[1];
                        double[] values = new double[vals.length];
                        for (int i = 0; i < vals.length; i++) {
                            values[i] = Double.parseDouble(vals[i]);
                        }
                        //Instances firm = new Instances("TestInstances", atts , 0);
                        //firm.add(new DenseInstance(1.0, values));
                        DenseInstance testInstance = new DenseInstance(1.0, values);
                        //testInstance.setDataset(firm);
                        double output = 0;
                        try {
                            output = classifier.classifyInstance(testInstance);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        double performance = myAgent.getPerformance();
                        double[] message = new double[2];
                        message[0] = performance;
                        message[1] = output;

                        ACLMessage msg_to_send = new ACLMessage(ACLMessage.INFORM);
                        msg_to_send.setContentObject(message);
                        AID dest = new AID("coordinatorAgent", AID.ISLOCALNAME);
                        msg_to_send.addReceiver(dest); //The receiver is the coordinator Agent
                        myAgent.send(msg_to_send); //The message is sent
                    }
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            break;
            }
        }
    }
}
