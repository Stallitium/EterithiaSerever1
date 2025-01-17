package stallitium;

public class CInfo {
    private String clientId;
    private String displayName;

    public CInfo(String clientId, String displayName) {
        this.clientId = clientId;
        this.displayName = displayName;
    }
    public CInfo() {

    }

    public String getClientId() {
        return clientId;
    }

    public synchronized void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public synchronized void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "ClInfo{"+
                "clientId='" + clientId + "'" +
                ",displayName='" + displayName + "'" +
                "}";
    }
}
