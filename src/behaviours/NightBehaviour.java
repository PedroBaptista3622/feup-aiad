package behaviours;

import agents.GameMaster;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import protocols.DecisionRequester;
import utils.ProtocolNames;

import static utils.Util.buildMessage;
import static utils.Util.createMessage;

public class NightBehaviour extends SequentialBehaviour {

    GameMaster gameMaster;

    public NightBehaviour(GameMaster gameMaster) {
        this.gameMaster = gameMaster;

        // Informs alive agents about the current time of day
        this.addSubBehaviour(new GameStateInformer(this.gameMaster, ProtocolNames.TimeOfDay));

        // Town Detective -> Mafia -> Town Healer

        // Town Detective
//        ACLMessage msg = createMessage(ACLMessage.REQUEST,
//                gameMaster.getGameLobby().getFirstRole("Detective"),
//                "TargetDetective", "Handle night content Village");
//
//        this.addSubBehaviour(new DecisionRequester(gameMaster, msg));

        // Mafia
        ACLMessage msgMafia = buildMessage(ACLMessage.REQUEST,
                ProtocolNames.TargetKilling,
                "Who do you want to unalive this night?"
        );

        // The corner case where all mafia dead (Leader and Killing) should not happen in here => the game should have already ended
        // If there is (are) Killing(s) alive, the request is sent to it (them)
        // If not, the leader is the one that receives the request
        msgMafia = this.gameMaster.addReceiversMessage(
                msgMafia,
                (this.gameMaster.getGameLobby().didAllKillingsDie()) ?
                        this.gameMaster.getGameLobby().getPlayersAIDRole("Leader", true) :
                        this.gameMaster.getGameLobby().getPlayersAIDRole("Killing", true)
        );

        this.addSubBehaviour(new DecisionRequester(gameMaster, msgMafia));

        // Town Healer
//        ACLMessage msg3 = createMessage(ACLMessage.REQUEST,
//                gameMaster.getGameLobby().getFirstRole("Healer"),
//                "TargetHealer", "Handle night content Killing");
//
//        this.addSubBehaviour(new DecisionRequester(gameMaster, msg3));

    }

    @Override
    public int onEnd() {

        String winner = this.gameMaster.getWinnerFaction();

        if (winner == null) {
            System.out.println("======> Night is over!");
            this.gameMaster.setGameState(GameMaster.GameStates.DAY);
        }
        else {
            this.gameMaster.setGameState(GameMaster.GameStates.END);
            System.out.println(winner + " won the game!");
        }

        return super.onEnd();
    }
}
