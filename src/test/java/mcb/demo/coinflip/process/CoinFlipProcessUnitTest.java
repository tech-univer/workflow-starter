package mcb.demo.coinflip.process;

import mcb.demo.coinflip.CoinFlipRepository;
import mcb.demo.coinflip.command.RequestCoinFlip;
import mcb.demo.coinflip.model.Flip;
import mcb.demo.coinflip.model.Outcome;
import mcb.demo.coinflip.service.CoinFlipService;
import mcb.demo.uuid.UUIDProvider;
import org.camunda.bpm.engine.ArtifactFactory;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.DefaultArtifactFactory;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;

public class CoinFlipProcessUnitTest {
    private CoinFlipService service;
    private RuntimeService runtimeService;
    private TaskService taskService;
    private CoinFlipRepository repository;
    private ProcessEngine processEngine;
    private ProcessCoinFlips processCoinFlips;

    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule(createProcessEngineProgramatically());

    @Before
    public void setUp() {
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
        repository = mock(CoinFlipRepository.class);
        service = new CoinFlipService(runtimeService, repository);
    }

    private ProcessEngine createProcessEngineProgramatically() {
        StandaloneInMemProcessEngineConfiguration processEngineConfiguration = new StandaloneInMemProcessEngineConfiguration();
        processEngineConfiguration.setArtifactFactory(new MockArtifactFactory());
        processEngine = processEngineConfiguration.buildProcessEngine();
        return processEngine;
    }

    @Test
    public void shouldBeAbleToRunThroughACoinFlipProcess() throws Exception {
        new CoinFlipProcess(processEngine.getRepositoryService()).getCoinFlipProcess();
        processCoinFlips = mock(ProcessCoinFlips.class);
        var uuidProvider = mock(UUIDProvider.class);
        when(uuidProvider.randomUUID()).thenReturn("uuid1", "uuid2");
        Mocks.register(ProcessCoinFlips.class.getCanonicalName(), new ProcessCoinFlips(runtimeService, taskService, uuidProvider, repository));

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
        verify(repository, times(1)).saveAll(expectedFlips);
    }
}
