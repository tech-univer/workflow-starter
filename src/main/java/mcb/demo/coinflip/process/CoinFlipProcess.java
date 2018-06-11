package mcb.demo.coinflip.process;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.spring.boot.starter.CamundaBpmAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@AutoConfigureAfter(CamundaBpmAutoConfiguration.class)
public class CoinFlipProcess {
    private final RepositoryService service;

    public CoinFlipProcess(RepositoryService service) {
        this.service = service;
    }

    @Bean
    public Deployment getCoinFlipProcess() {
        BpmnModelInstance flipCoin = Bpmn.createExecutableProcess("CoinFlips")
                .startEvent("CoinFlipsRequested")
                .userTask("AwaitFlips")
                .serviceTask("StoreFlips").camundaClass(ProcessCoinFlips.class)
                .endEvent("CoinFlipped")
                .done();
        return this.service.createDeployment().addModelInstance("coinflips.bpmn",
                flipCoin).deploy();
    }
}
