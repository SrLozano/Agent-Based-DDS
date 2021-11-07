package agents;

import jade.core.Agent;
//import jade.core.Filter;
import weka.classifiers.Evaluation;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Instances;
import weka.classifiers.trees.J48;
import weka.filters.unsupervised.instance.RemovePercentage;
import weka.filters.Filter;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Random;



public class classifierAgent extends Agent {
    public static void main(String[] args) throws Exception {

        double percentage = 6.5;


        // Read the dataset for user or coord agent
        DataSource source = new DataSource(System.getProperty("user.dir") + "/audit_risk.arff");
        if(source != null){
            System.out.println("Read");

            Instances data = source.getDataSet();
            Random random = new Random(42);
            data.randomize(random);

            // Split between train and test for coord agent
            RemovePercentage rp = new RemovePercentage();
            rp.setInputFormat(data);
            rp.setPercentage(percentage);

            Instances train = Filter.useFilter(data, rp);

            RemovePercentage rp_test = new RemovePercentage();
            rp_test.setInputFormat(data);
            rp_test.setPercentage(percentage);
            rp_test.setInvertSelection(true);

            Instances test = Filter.useFilter(data, rp_test);

            System.out.println("Train instances");
            System.out.println(train.numInstances());


            System.out.println("Test instances");
            System.out.println(test.numInstances());

            // Setting class attribute if the data format does not provide this information
            if (train.classIndex() == -1) {
                train.setClassIndex(train.numAttributes() - 1);
            }
            // Setting class attribute if the data format does not provide this information
            if (test.classIndex() == -1) {
                test.setClassIndex(test.numAttributes() - 1);
            }


            // Train classifier and save it
            J48 classifier = new J48();
            classifier.buildClassifier(train);
            weka.core.SerializationHelper.write(System.getProperty("user.dir") + "/classifier.model", classifier);


            // Test classifier
            Evaluation eval = new Evaluation(test);
            eval.evaluateModel(classifier, test);
            System.out.println((eval.correct()/test.numInstances())*100);



            // Loading an already saved tree
            J48 treeClassifier = (J48) SerializationHelper.read(new FileInputStream(System.getProperty("user.dir") + "/classifier.model"));

            Evaluation eval2 = new Evaluation(test);
            eval.evaluateModel(treeClassifier, test);
            System.out.println((eval2.correct()/test.numInstances())*100);

        }
    }
}


