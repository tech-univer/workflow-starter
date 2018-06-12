package mcb.demo.coinflip.service;

import mcb.demo.coinflip.CoinFlipRepository;
import mcb.demo.coinflip.command.RequestCoinFlip;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.HashMap;

@RestController
public class CoinFlipService {
    private RuntimeService runtimeService;
    private final CoinFlipRepository repository;

    @Autowired
    public CoinFlipService(RuntimeService runtimeService, CoinFlipRepository repository) {
        this.runtimeService = runtimeService;
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

        runtimeService.createProcessInstanceByKey("CoinFlips")
                .businessKey(command.getId())
                .setVariables(variables)
                .execute();

        return ResponseEntity.created(URI.create("/coinflips/" + command.getId())).build();
    }

    @GetMapping(path = "coinflips")
    public ResponseEntity getCoinFlips() {
        return ResponseEntity.ok(repository.getAll());
    }
}
