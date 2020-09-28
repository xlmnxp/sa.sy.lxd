package sa.sy.lxd;

import java.io.IOException;
import com.google.gson.*;

public class Instance implements Cloneable {
    public enum Type {
        Container,
        VirtualMachine
    }

    public enum State {
        START,
        STOP,
        RESTART,
        FREEZE,
        UNFREEZE
    };

    private final Lxd client;
    private String name = "-";
    private String type = "-";
    private String status = "-";
    private JsonObject instance;

    public Instance(Lxd client, JsonObject instance) throws IOException {
        this.client = client;
        this.instance = instance;
        this.name = this.instance.get("name").getAsString();
        this.type = this.instance.get("type").getAsString();
        this.status = this.instance.get("status").getAsString();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status.toUpperCase();
    }

    public JsonObject getState() throws Exception {
        return JsonParser.parseString(client.request(Lxd.Method.GET, "/1.0/instances/" + this.name + "/state")).getAsJsonObject().get("metadata").getAsJsonObject();
    }

    public String getIPv4() throws Exception {
        JsonObject network = this.getState().getAsJsonObject("network").getAsJsonObject("eth0").getAsJsonArray("addresses").get(0).getAsJsonObject();
        if(!network.get("family").getAsString().equalsIgnoreCase("inet"))
            throw new Exception("IPv4 not found");
        return network.get("address").getAsString();
    }

    public String getIPv6() throws Exception {
        JsonObject network = this.getState().getAsJsonObject("network").getAsJsonObject("eth0").getAsJsonArray("addresses").get(1).getAsJsonObject();
        if(!network.get("family").getAsString().equalsIgnoreCase("inet6"))
            throw new Exception("IPv6 not found");
        return network.get("address").getAsString();
    }

    public void setState(State state) throws Exception {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("action", state.toString().toLowerCase());
//        jsonBody.put("force", true);
        jsonBody.addProperty("timeout", -1);
        String jsonBodyString = jsonBody.getAsString();
        client.request(Lxd.Method.PUT, "/1.0/instances/" + this.name + "/state", "Content-type: text/json\r\n" + "Content-length: " + jsonBodyString.length(), jsonBodyString);
    }

    public boolean isContainer() {
        if(this.type.equalsIgnoreCase("container"))
            return true;

        return false;
    }

    public boolean isVirtualMachine() {
        if(this.type.equalsIgnoreCase("virtual-machine"))
            return true;

        return false;
    }
}
