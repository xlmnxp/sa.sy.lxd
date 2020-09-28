package sa.sy.lxd;

import com.google.gson.*;

public class Project {
    private final Lxd client;
    private JsonObject project;
    private String name;
    private String description;
    private JsonObject config;
    private Instance[] usedBy;

    public Project(Lxd client, JsonObject project) throws Exception {
        this.client = client;
        this.project = project;
        this.name = this.project.get("name").getAsString();
        this.description = this.project.get("description").getAsString();
        this.config = this.project.get("config").getAsJsonObject();

        Instance[] instances = new Instance[this.project.get("used_by").getAsJsonArray().size()];
        for (int arrInd = 0; arrInd < instances.length; arrInd++) {
            instances[0] = client.getInstance(this.project.get("used_by").getAsJsonArray().get(arrInd).getAsString());
        }

        this.usedBy = instances;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public JsonObject getConfig() {
        return config;
    }

    public Instance[] getUsedBy() {
        return usedBy;
    }
}
