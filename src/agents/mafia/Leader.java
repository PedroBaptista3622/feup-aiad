package agents.mafia;

import agents.PlayerAgent;
import behaviours.GameStateListener;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import protocols.ContextWaiter;
import protocols.DecisionInformer;
import protocols.PlayerInformer;
import utils.GameLobby;
import utils.ProtocolNames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Leader extends PlayerAgent {

    protected GameLobby gameLobby;

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


        // Reports role to gameMaster
        this.addBehaviour(new PlayerInformer(this, msg));


        MessageTemplate playerNamesTemplate = MessageTemplate.and(
                MessageTemplate.MatchProtocol(ProtocolNames.PlayerNames),
                MessageTemplate.MatchPerformative(ACLMessage.INFORM)
        );

        // Builds context
        this.addBehaviour(new ContextWaiter(this, playerNamesTemplate));

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
        MessageTemplate tmp = MessageTemplate.and(
                MessageTemplate.MatchProtocol(ProtocolNames.TargetKilling),
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

        // Handles ability target requests
        this.addBehaviour(new DecisionInformer(this, tmp));
    }

    @Override
    public ACLMessage handleNightVoteRequest(ACLMessage request, ACLMessage response) {
        // Day time voting
        List<String> killablePlayers = this.getGameContext().getAlivePlayers();

        List<String> killers = this.gameLobby.getKillingPlayers();

        // Select random killable players
        ArrayList<Integer> randomKillablePlayers = new ArrayList<>();
        for (int i = 0; i < killers.size(); i++) {
            randomKillablePlayers.add(i);
        }
        Collections.shuffle(randomKillablePlayers);


        //String playerToKill = killablePlayers.get(playerIndex);

        ACLMessage inform = request.createReply();
        //inform.setContent(playerToKill);
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

    @Override
    public void takeDown() {
        this.deregisterAgent();
//        System.out.println("Killing shutdown!");
    }
}
