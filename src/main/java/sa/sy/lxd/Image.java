package sa.sy.lxd;

import java.io.IOException;
import com.google.gson.*;

public class Image {
    public enum Type {
        All,
        Container,
        VirtualMachine
    }
    private JsonObject image;
    private Lxd client;
    private boolean isPublic;
    private JsonObject properties;
    private String fingerprint;
    private String filename;
    private int size;
    private boolean autoUpdate;
    private Type type;

    public Image(Lxd client, JsonObject image) throws IOException {
        this.client = client;
        this.image = image;
        this.isPublic = this.image.get("public").getAsBoolean();
        this.properties = this.image.get("properties").getAsJsonObject();
        this.fingerprint = this.image.get("fingerprint").getAsString();
        this.filename = this.image.get("filename").getAsString();
        this.size = this.image.get("size").getAsInt();
        this.autoUpdate = this.image.get("auto_update").getAsBoolean();

        if (this.image.get("type").getAsString().equals("virtual-machine")) {
            this.type = Type.VirtualMachine;
        } else if(this.image.get("type").getAsString().equals("container")) {
            this.type = Type.Container;
        }
    }

    public Type getType() {
        return type;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public JsonObject getProperties() {
        return properties;
    }
}
