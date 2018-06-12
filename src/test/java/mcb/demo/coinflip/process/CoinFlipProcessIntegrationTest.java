package mcb.demo.coinflip.process;

import mcb.demo.DemoApplication;
import mcb.demo.coinflip.CoinFlipRepository;
import mcb.demo.coinflip.command.RequestCoinFlip;
import mcb.demo.coinflip.model.Flip;
import mcb.demo.coinflip.model.Outcome;
import mcb.demo.coinflip.service.CoinFlipService;
import mcb.demo.uuid.UUIDProvider;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DemoApplication.class)
@Tag("integration")
public class CoinFlipProcessIntegrationTest {
    @Autowired
    private CoinFlipService service;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CoinFlipRepository repository;

    // Spring provides a Mockito mock instance of UUIDProvider both to the test
    // and to any Beans that depend on it - in this case ProcessCoinFlips depends on UUIDProvider
    @MockBean
    private UUIDProvider uuidProvider;

    @BeforeEach
    void setUp() {
    }

    @Test
    public void shouldBeAbleToRunThroughACoinFlipProcess() {
        when(uuidProvider.randomUUID()).thenReturn("uuid1", "uuid2");

        String requestId = "test-flip";
        String currency = "USD";
        int denomination = 25;
        service.requestCoinFlips(new RequestCoinFlip(requestId, denomination, currency, 2));

        var process = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(requestId).singleResult();
        var task = taskService.createTaskQuery().processInstanceBusinessKey(requestId).active().singleResult();
        var processVariables = runtimeService.createVariableInstanceQuery().processInstanceIdIn(process.getProcessInstanceId()).list();
        var variables = processVariables.stream().collect(Collectors.toMap(VariableInstance::getName, VariableInstance::getValue));

        assertThat(task.getName(), equalTo("AwaitFlips"));

        assertThat(variables, hasEntry("currency", currency));
        assertThat(variables, hasEntry("denomination", denomination));
        assertThat(variables, hasEntry("flipResult1", ""));
        assertThat(variables, hasEntry("flipResult2", ""));

        runtimeService.setVariable(process.getProcessInstanceId(), "flipResult1", Outcome.HEADS.name());
        runtimeService.setVariable(process.getProcessInstanceId(), "flipResult2", Outcome.TAILS.name());
        taskService.complete(task.getId());

        var expectedFlips = new ArrayList<Flip>();
        expectedFlips.add(new Flip(requestId, "uuid1", Outcome.HEADS, currency, denomination));
        expectedFlips.add(new Flip(requestId, "uuid2", Outcome.TAILS, currency, denomination));
        assertThat(repository.getAll(), equalTo(expectedFlips));
    }
}
