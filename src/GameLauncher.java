import agents.TestAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.ConfigReader;

import java.io.IOException;
import java.util.Queue;

public class GameLauncher {

    public static void main(String[] args) {

        // Loads available names
        try {
            ConfigReader.importNames("src/resources/names.txt");
        } catch (IOException e) {
            System.out.println("An Error occurred while importing names from config. Aborting");
        }

        // Loads roles
        try {
            Queue<String> roles = ConfigReader.importRoles("src/resources/roles.txt");
            System.out.println("Number roles imported: " + roles.size());
        } catch (IOException e) {
            System.out.println("An Error occurred while importing roles from config. Aborting");
        }

        // Get a JADE runtime
        Runtime rt = Runtime.instance();

        // Create the main container
        Profile p1 = new ProfileImpl();
        ContainerController mainController = rt.createMainContainer(p1);

        // Create additional container
        Profile p2 = new ProfileImpl();
        ContainerController container = rt.createAgentContainer(p2);

        try {

            // Gui Agent
            AgentController gui =
                    mainController.createNewAgent("GUI", "jade.tools.rma.rma", null);
            gui.start();

            // Launch agent
            AgentController ac1 = container.acceptNewAgent("name1", new TestAgent());
            ac1.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
