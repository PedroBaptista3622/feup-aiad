package behaviours;

import agents.town.Detective;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.ProtocolNames;

public class InvestigationWaiter extends Behaviour {

    private enum Steps {
        Init,
        WaitingResponse,
        Done
    };

    private final Detective detective;
    private Steps currentStep;

    private ACLMessage targetMessage;

    public InvestigationWaiter(Detective detective) {
        this.detective = detective;
        this.currentStep = Steps.Init;
    }

    @Override
    public void action() {

        switch (this.currentStep) {
            case Init: {
                MessageTemplate requestTemplate = MessageTemplate.and(
                        MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                        MessageTemplate.MatchProtocol(ProtocolNames.Investigate)
                );

                ACLMessage requestMessage = this.detective.blockingReceive(requestTemplate);
                targetMessage = this.detective.handleNightVoteRequest(requestMessage, null);
                this.detective.send(targetMessage);

                this.currentStep = Steps.WaitingResponse;
                break;
            }
            case WaitingResponse: {
                MessageTemplate resultTemplate = MessageTemplate.and(
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                        MessageTemplate.MatchProtocol(ProtocolNames.Investigate)
                );

                ACLMessage investigationResult = this.detective.blockingReceive(resultTemplate);
                boolean isSus = investigationResult.getContent().equals("Kinda sus");
                this.detective.addVisit(this.targetMessage.getContent(), isSus);

                this.currentStep = Steps.Done;
                break;
            }
        }
    }

    @Override
    public boolean done() {
        return this.currentStep == Steps.Done;
    }
}
