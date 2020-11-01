package agents.mafia;

import agents.PlayerAgent;
import behaviours.GameStateListener;
import behaviours.TargetDictator;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import protocols.ContextWaiter;
import protocols.DecisionInformer;
import protocols.MafiaWaiter;
import protocols.PlayerInformer;
import utils.ProtocolNames;

import java.util.List;
import java.util.Random;

public class Leader extends PlayerAgent {

    @Override
    public String getRole() {
        return "Leader";
    }

    @Override
    protected void setup() {
        super.setup();

        // Agent Registration
        try {
            this.registerAgent(this.getRole());
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // Agent tries to join the game's lobby
        ACLMessage msg = this.buildJoinMessage(this.getRole());

        System.out.println(msg.getContent());

        // Reports role to gameMaster
        this.addBehaviour(new PlayerInformer(this, msg));

        MessageTemplate playerNamesTemplate = MessageTemplate.and(
                MessageTemplate.MatchProtocol(ProtocolNames.PlayerNames),
                MessageTemplate.MatchPerformative(ACLMessage.INFORM)
        );

        // Builds context
        this.addBehaviour(new ContextWaiter(this, playerNamesTemplate));


        MessageTemplate mafiaNamesTemplate = MessageTemplate.and(
                MessageTemplate.MatchProtocol(ProtocolNames.MafiaPlayers),
                MessageTemplate.MatchPerformative(ACLMessage.INFORM)
        );

        // Stores the mafia team
        this.addBehaviour(new MafiaWaiter(this, mafiaNamesTemplate));

        // Listens to gameState changes
        this.addBehaviour(new GameStateListener(this));
    }

    @Override
    public void setDayTimeBehavior() {
        MessageTemplate tmp = MessageTemplate.and(
                MessageTemplate.MatchProtocol(ProtocolNames.VoteTarget),
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

        // Handles ability target requests
        this.addBehaviour(new DecisionInformer(this, tmp));
    }

    @Override
    public void setNightTimeBehaviour() {
        List<String> killingsAlive = this.getGameContext().getPlayerNamesByRole("Killing", true);
        if(killingsAlive.size() > 0) {
            System.out.println("Waiting for my soldier");
            // If there are killings, this agent waits for them to present themselves and then orders them
            this.addBehaviour(new TargetDictator(this));
        }
        else {
            // If there are no more killings, this agent has the same behaviour as one
            MessageTemplate tmp = MessageTemplate.and(
                    MessageTemplate.MatchProtocol(ProtocolNames.TargetKilling),
                    MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

            // Handles ability target requests
            this.addBehaviour(new DecisionInformer(this, tmp));
        }
    }

    @Override
    public ACLMessage handleNightVoteRequest(ACLMessage request, ACLMessage response) {
        // Only happens if/when there are no Mafia Killings alive
        List<String> killablePlayers = this.getGameContext().getAlivePlayers();

        Random r = new Random();
        int playerIndex = r.nextInt(killablePlayers.size());

        String playerToKill = killablePlayers.get(playerIndex);

        ACLMessage inform = request.createReply();
        inform.setContent(playerToKill);
        inform.setPerformative(ACLMessage.INFORM);

        return inform;
    }

    @Override
    public ACLMessage handleDayVoteRequest(ACLMessage request, ACLMessage response) {
        // Person to kill during night
        List<String> killablePlayers = this.getGameContext().getAlivePlayers();

        Random r = new Random();
        int playerIndex = r.nextInt(killablePlayers.size());

        String playerToKill = killablePlayers.get(playerIndex);

        ACLMessage inform = request.createReply();
        inform.setContent(playerToKill);
        inform.setPerformative(ACLMessage.INFORM);

        return inform;
    }

    public ACLMessage handleKillOrder(ACLMessage request) {
        // Only happens if/when there are no Mafia Leaders alive
        List<String> killablePlayers = this.getGameContext().getAlivePlayers();

        Random r = new Random();
        int playerIndex = r.nextInt(killablePlayers.size());

        String playerToKill = killablePlayers.get(playerIndex);

        ACLMessage inform = request.createReply();

        System.out.println("[Leader Decision] " + playerToKill);

        // String msgContent = "I want you to unalive "+ ;
        inform.setContent("I want you to unalive " + playerToKill);
        inform.setPerformative(ACLMessage.INFORM);

        return inform;
    }

    @Override
    public void takeDown() {
        this.deregisterAgent();
    }
}
