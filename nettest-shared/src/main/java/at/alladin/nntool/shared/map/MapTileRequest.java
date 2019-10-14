package at.alladin.nntool.shared.map;

import com.google.gson.annotations.Expose;

public class MapTileRequest {

    @Expose
    private String clientUuid;

    @Expose
    private MapTileParameters parameters;

    public MapTileParameters getParameters() {
        return parameters;
    }

    public void setParameters(MapTileParameters parameters) {
        this.parameters = parameters;
    }

    public String getClientUuid() {
        return clientUuid;
    }

    public void setClientUuid(String clientUuid) {
        this.clientUuid = clientUuid;
    }
}
