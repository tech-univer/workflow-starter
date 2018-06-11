package mcb.demo.coinflip.process;

import mcb.demo.DemoApplication;
import mcb.demo.coinflip.CoinFlipRepository;
import mcb.demo.coinflip.command.RequestCoinFlip;
import mcb.demo.coinflip.service.CoinFlipService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.spring.boot.starter.test.helper.AbstractProcessEngineRuleTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class CoinFlipProcessTest extends AbstractProcessEngineRuleTest {
    @Autowired
    private CoinFlipService service;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CoinFlipRepository repository;

    @Test
    public void shouldBeAbleToRunThroughACoinFlipProcess() {
        service.requestCoinFlips(new RequestCoinFlip("test-flip", 25, "USD", 2));

        var process = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey("test-flip").singleResult();
        var task = taskService.createTaskQuery().processInstanceBusinessKey("test-flip").active().singleResult();
        var processVariables = runtimeService.createVariableInstanceQuery().processInstanceIdIn(process.getProcessInstanceId()).list();
        var variables = processVariables.stream().collect(Collectors.toMap(VariableInstance::getName, VariableInstance::getValue));

        assertThat(task.getName(), equalTo("AwaitFlips"));

        assertThat(variables, hasEntry("currency", "USD"));
        assertThat(variables, hasEntry("denomination", 25));
        assertThat(variables, hasEntry("flipResult1", ""));
        assertThat(variables, hasEntry("flipResult2", ""));

        runtimeService.setVariable(process.getProcessInstanceId(), "flipResult1", "HEADS");
        runtimeService.setVariable(process.getProcessInstanceId(), "flipResult2", "TAILS");
        taskService.complete(task.getId());

        assertThat(repository.getAll().size(), equalTo(2));
    }
}
