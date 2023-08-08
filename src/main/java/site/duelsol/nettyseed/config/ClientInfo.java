package site.duelsol.nettyseed.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "client")
@Component
@Data
public class ClientInfo {

    private String ip;

    private Integer port;

    private String clientId;

    private Integer heartbeatInterval;

}
