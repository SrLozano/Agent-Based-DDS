package agents;

import jade.core.Agent;
//import jade.core.Filter;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Instances;
import weka.classifiers.trees.J48;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemovePercentage;
import weka.filters.Filter;

import java.io.FileInputStream;
import java.util.Random;



public class classifierAgent extends Agent {
    public static void main(String[] args) throws Exception {

        double percentage = 30;


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


            // Train classifier and save it for classifier agent
            J48 classifier = new J48();
            classifier.buildClassifier(train);
            weka.core.SerializationHelper.write(System.getProperty("user.dir") + "/classifier.model", classifier);


            // Test classifier for classifier agents
            Evaluation eval = new Evaluation(test);
            eval.evaluateModel(classifier, test);
            System.out.println((eval.correct()/test.numInstances())*100);



            // Loading an already saved tree for classifier agents
            J48 treeClassifier = (J48) SerializationHelper.read(new FileInputStream(System.getProperty("user.dir") + "/classifier.model"));

            Evaluation eval2 = new Evaluation(test);
            eval2.evaluateModel(treeClassifier, test);
            System.out.println((eval2.correct()/test.numInstances())*100);

            // Split data into only 6 attributes (no sabem pq pero té la mateix accuracy que el classifier entrenat
            // amb tots els atributes


            // The array of arrays that defenies the index of the attributes for each classifier
            // All of them have index 24 as it is the class
            int[][]  allarrays =  {
                    {0,4,8,12,16,20,24},
                    {1,5,9,13,12,21,24},
                    {2,6,10,14,18,22,24},
                    {3,7,11,15,19,23,24},
                    {0,2,8,10,16,18,24},
                    {1,3,9,11,17,19,24},
                    {4,6,12,14,20,22,24},
                    {5,7,13,15,21,23,24},
                    {0,5,8,13,16,21,24},
                    {1,4,9,12,17,20,24},
                    {2,7,10,15,18,23,24},
                    {3,6,11,14,19,22,24},
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
}


