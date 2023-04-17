package settings;

import com.fasterxml.jackson.annotation.JsonProperty;


public class SettingsModel {
    @JsonProperty("local_port")
    public int localPort;
    @JsonProperty("server_port")
    public int serverPort;
}
