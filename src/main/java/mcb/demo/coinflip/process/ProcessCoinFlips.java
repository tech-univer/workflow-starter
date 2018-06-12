package mcb.demo.coinflip.process;

import mcb.demo.coinflip.CoinFlipRepository;
import mcb.demo.coinflip.model.Flip;
import mcb.demo.coinflip.model.Outcome;
import mcb.demo.uuid.UUIDProvider;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProcessCoinFlips implements JavaDelegate {
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final UUIDProvider uuidProvider;
    private final CoinFlipRepository repository;

    @Autowired
    public ProcessCoinFlips(RuntimeService runtimeService, TaskService taskService, UUIDProvider uuidProvider, CoinFlipRepository repository) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.uuidProvider = uuidProvider;
        this.repository = repository;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        List<Flip> flips = new ArrayList<>();
        var variables = runtimeService.getVariables(delegateExecution.getProcessInstanceId());
        var currency = variables.get("currency").toString();
        var denomination = Integer.valueOf(variables.get("denomination").toString());
        variables.forEach((k, v) -> {
            if (k.startsWith("flipResult")) {
                flips.add(new Flip(delegateExecution.getProcessBusinessKey(), uuidProvider.randomUUID(), Outcome.valueOf(v.toString()), currency, denomination));
            }
        });
        repository.saveAll(flips);
    }
}
