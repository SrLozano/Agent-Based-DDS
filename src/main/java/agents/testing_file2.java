package agents;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;


public class testing_file2{

    public static void main(String[] args){
        // Get a hold on JADE runtime
        Runtime rt = Runtime.instance();
        // Exit the JVM when there are no more containers around
        rt.setCloseVM(true);
        Profile p = new ProfileImpl(true);
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.GUI, "true");
        //System.out.println(getLocalName()+": Launching the agent container ...\n-Profile: " + p);
        ContainerController ac = rt.createMainContainer(p);
        AgentController anotherAgent;

        try{
        //Creating new classifierAgent. First argument is the name. Second argument is the class Agent.
                anotherAgent = ac.createNewAgent("classifier", "agents.coordAgent",null);
                anotherAgent.start();
        }
        catch (StaleProxyException e) {
                e.printStackTrace();
            }


    }


}


