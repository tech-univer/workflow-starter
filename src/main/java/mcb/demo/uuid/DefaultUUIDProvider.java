package mcb.demo.uuid;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DefaultUUIDProvider implements UUIDProvider {
    @Override
    public String randomUUID() {
        return UUID.randomUUID().toString();
    }
}
