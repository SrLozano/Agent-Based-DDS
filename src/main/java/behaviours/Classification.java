package behaviours;
import jade.core.Agent;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.UnreadableException;
import weka.classifiers.Evaluation;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Instances;
import weka.classifiers.trees.J48;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemovePercentage;
import weka.filters.Filter;

import java.io.FileInputStream;
import java.util.Random;
import jade.lang.acl.ACLMessage;


public class Classification extends OneShotBehaviour {

    public void action() {
        try{
                // THIS ACL MESSAGE IS NOT WELL RECEIVED; AND THUS NO FUNCIONA: HAY QUE MIRAR
                ACLMessage msg = myAgent.receive(); //Another option to receive a message is blockingReceive()
                if (msg.getPerformative() == ACLMessage.INFORM) {
                    Object train_obj = null;

                    try {
                        train_obj = msg.getContentObject();
                    } catch (UnreadableException e) {

                        e.printStackTrace();
                    }
                    System.out.println(train_obj.getClass().getSimpleName());
                    Instances trainval = (Instances) train_obj; //Puede que dé error, comprobar que funcione
                    ACLMessage reply = msg.createReply();
                    if (trainval.getClass() == Instances.class) {
                        System.out.println("-" + myAgent.getLocalName());

                        //We reply to the user that the message has been received
                        reply.setPerformative(ACLMessage.INFORM); //If the user sends a Request --> Informs
                        reply.setContent("The training data has been received");


                        System.out.println("Classifier being trained");

                        double percentage = 25; //25% a validació
                        System.out.println("Read");
                        Random random = new Random(42);
                        trainval.randomize(random);
                        // Split between train and validation for coord agent
                        RemovePercentage rp = new RemovePercentage();
                        rp.setInputFormat(trainval);
                        rp.setPercentage(percentage);
                        Instances train = Filter.useFilter(trainval, rp);

                        RemovePercentage rp_validation = new RemovePercentage();
                        rp_validation.setInputFormat(trainval);
                        rp_validation.setPercentage(percentage);
                        rp_validation.setInvertSelection(true);

                        Instances validation = Filter.useFilter(trainval, rp_validation);
                        // Setting class attribute if the data format does not provide this information
                        if (train.classIndex() == -1) {
                            train.setClassIndex(train.numAttributes() - 1);
                        }
                        // Setting class attribute if the data format does not provide this information
                        if (validation.classIndex() == -1) {
                            validation.setClassIndex(validation.numAttributes() - 1);
                        }

                        // Train classifier and save it for testing
                        J48 classifier = new J48();
                        classifier.buildClassifier(train);
                        weka.core.SerializationHelper.write(System.getProperty("user.dir") + "/classifier.model", classifier);

                        // Validate classifier for testing
                        Evaluation eval = new Evaluation(validation);
                        eval.evaluateModel(classifier, validation);
                        System.out.println((eval.correct() / validation.numInstances()) * 100);

                        //la idea seria aquí guardar la performance en la validació i utilitzar-la després per a ponderar
                        //el resultat del given classifier (com pesos) per la decisio

                    }
                }

            /*default: // Default is used like the else statement
                // testing new instances
                //ara la datasource canvia a les 15 instances que entra l'usuari, mirar com fer-ho
                System.out.println("Classifying new set of 15 instances");
                // Loading an already saved tree for classifier agents
                //maybe no cal guardar el tree en un file???
                J48 treeClassifier = (J48) SerializationHelper.read(new FileInputStream(System.getProperty("user.dir") + "/classifier.model"));
                //això està per acabar
                break;*/
        }
        catch (Exception e){
            System.out.println("F");
        }
    }
}

/*
        public static void agents.main (String[]args) throws Exception {


            if (source != null) {
                System.out.println("Read");

                Instances data = source.getDataSet();
                Random random = new Random(42);
                data.randomize(random);

                // Split between train and validation for coord agent
                RemovePercentage rp = new RemovePercentage();
                rp.setInputFormat(data);
                rp.setPercentage(percentage);

                Instances train = Filter.useFilter(data, rp);

                RemovePercentage rp_validation = new RemovePercentage();
                rp_validation.setInputFormat(data);
                rp_validation.setPercentage(percentage);
                rp_validation.setInvertSelection(true);

                Instances validation = Filter.useFilter(data, rp_validation);

                System.out.println("Train instances");
                System.out.println(train.numInstances());


                System.out.println("Test instances");
                System.out.println(validation.numInstances());

                // Setting class attribute if the data format does not provide this information
                if (train.classIndex() == -1) {
                    train.setClassIndex(train.numAttributes() - 1);
                }
                // Setting class attribute if the data format does not provide this information
                if (validation.classIndex() == -1) {
                    validation.setClassIndex(validation.numAttributes() - 1);
                }


                // Train classifier and save it for testing
                J48 classifier = new J48();
                classifier.buildClassifier(train);
                weka.core.SerializationHelper.write(System.getProperty("user.dir") + "/classifier.model", classifier);


                // Validate classifier
                Evaluation eval = new Evaluation(validation);
                eval.evaluateModel(classifier, validation);
                System.out.println((eval.correct() / validation.numInstances()) * 100);
                //la idea seria aquí guardar la performance en la validació i utilitzar-la després per a ponderar
                //el resultat del given classifier (com pesos) per la decisió final


                // Loading an already saved tree for classifier agents
                //maybe no cal guardar el tree en un file???
                J48 treeClassifier = (J48) SerializationHelper.read(new FileInputStream(System.getProperty("user.dir") + "/classifier.model"));

                Evaluation eval2 = new Evaluation(test);
                eval2.evaluateModel(treeClassifier, test);
                System.out.println((eval2.correct() / test.numInstances()) * 100);

                // Split data into only 6 attributes

                // The array of arrays that defines the index of the attributes for each classifier
                // All of them have index 24 as it is the class
                int[][] allarrays = {
                        {0, 4, 8, 12, 16, 20, 24},
                        {1, 5, 9, 13, 12, 21, 24},
                        {2, 6, 10, 14, 18, 22, 24},
                        {3, 7, 11, 15, 19, 23, 24},
                        {0, 2, 8, 10, 16, 18, 24},
                        {1, 3, 9, 11, 17, 19, 24},
                        {4, 6, 12, 14, 20, 22, 24},
                        {5, 7, 13, 15, 21, 23, 24},
                        {0, 5, 8, 13, 16, 21, 24},
                        {1, 4, 9, 12, 17, 20, 24},
                        {2, 7, 10, 15, 18, 23, 24},
                        {3, 6, 11, 14, 19, 22, 24},
                };

                // Each loop in the for loop represents one classifier
                int count = 0;
                for (int[] indices : allarrays) {
                    Remove removeFilter = new Remove();
                    removeFilter.setAttributeIndicesArray(indices);
                    removeFilter.setInvertSelection(true);
                    removeFilter.setInputFormat(train);
                    Instances splittrain = Filter.useFilter(train, removeFilter);
                    removeFilter.setInputFormat(test);
                    Instances splittest = Filter.useFilter(test, removeFilter);

                    // Train and test classifier
                    J48 splitclassifier = new J48();
                    splitclassifier.buildClassifier(splittrain);
                    //weka.core.SerializationHelper.write(System.getProperty("user.dir") + "/classifier.model", classifier);

                    Evaluation eval3 = new Evaluation(splittest);
                    eval3.evaluateModel(splitclassifier, splittest);
                    System.out.println("spliteval " + count + ' ' + (eval3.correct() / splittest.numInstances()) * 100);
                    count += 1;
                }
            }
        }
*/
