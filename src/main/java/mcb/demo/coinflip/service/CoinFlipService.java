package mcb.demo.coinflip.service;

import mcb.demo.coinflip.CoinFlipRepository;
import mcb.demo.coinflip.command.RequestCoinFlip;
import mcb.demo.coinflip.model.Flip;
import mcb.demo.coinflip.model.Outcome;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
public class CoinFlipService {
    private RuntimeService service;
    private TaskService taskService;
    private HistoryService historyService;
    private final CoinFlipRepository repository;

    @Autowired
    public CoinFlipService(RuntimeService service, TaskService taskService, HistoryService historyService, CoinFlipRepository repository) {
        this.service = service;
        this.taskService = taskService;
        this.historyService = historyService;
        this.repository = repository;
    }

    @PostMapping(path = "coinflips")
    public ResponseEntity requestCoinFlips(@RequestBody RequestCoinFlip command) {
        var variables = new HashMap<String, Object>();
        variables.put("denomination", command.getDenomination());
        variables.put("currency", command.getCurrency());

        for (int i = 0; i < command.getNumberOfFlips(); i++) {
            variables.put(String.format("flipResult%d", i + 1), "");
        }

        service.createProcessInstanceByKey("CoinFlips")
                .businessKey(command.getId())
                .setVariables(variables)
                .execute();

        List<HistoricProcessInstance> history = historyService.createHistoricProcessInstanceQuery().processDefinitionKey("Stuff_Process").list();
        for (HistoricProcessInstance i : history) {
            System.out.println(i.getBusinessKey());
        }

        return ResponseEntity.created(URI.create("/coinflips/" + command.getId())).build();
    }

    @GetMapping(path = "coinflips")
    public ResponseEntity getCoinFlips() {
        return ResponseEntity.ok(repository.getAll());
    }
}
