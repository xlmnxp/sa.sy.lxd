/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sa.sy.lxd;

import java.io.File;
import java.nio.charset.StandardCharsets;
import org.newsclub.net.unix.*;
import com.google.gson.*;

/**
 *
 * @author xlmnxp
 */
public class Lxd {
    protected File socketFile = new File("/var/snap/lxd/common/lxd/unix.socket");
    AFUNIXSocket client;
    
    enum Method {
        GET,
        POST,
        PUT,
        DELETE
    }

    public Lxd() throws Exception {
        client = AFUNIXSocket.newInstance();
    }

    public Lxd(File unixSocket) throws Exception {
        client = AFUNIXSocket.newInstance();
        socketFile = unixSocket;
    }
    
    void connect() throws Exception {
        client.connect(new AFUNIXSocketAddress(socketFile));
    }
    
    void close() throws Exception {
        client.close();
    }
    
    String request(Method method, String path) throws Exception {
        return this.request(method, path, "", "");
    }

    String request(Method method, String path, String header, String body) throws Exception {
        connect();
        String requestString = (method.toString() + " " + path + " HTTP/1.0" + (header.length() > 0 ? "\r\n" + header : "") + "\r\n\r\n" + body);
        client.getOutputStream().write(requestString.getBytes());
        byte[] buffer = new byte[client.getReceiveBufferSize()];
        client.getInputStream().read(buffer);
        return new String(buffer, StandardCharsets.UTF_8).split("\r\n\r\n")[1].trim();
    }
    
    public Instance getInstance(String name) throws Exception {
        return new Instance(this, JsonParser.parseString(this.request(Method.GET, name)).getAsJsonObject().getAsJsonObject("metadata"));
    }

    public Instance[] getInstances() throws Exception {
        JsonArray instances = JsonParser.parseString(this.request(Method.GET, "/1.0/instances")).getAsJsonObject().getAsJsonArray("metadata");
        Instance[] instancesList = new Instance[instances.size()];
        for (int i = 0; i < instancesList.length; i++) {
            instancesList[i] = this.getInstance(instances.get(i).toString());
        }

        return instancesList;
    }

    public Operation createInstance(String name, Instance.Type type, String fingerPrint) throws Exception {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("name", name);
        jsonBody.addProperty("architecture", "x86_64");
        JsonArray profiles = new JsonArray();
        profiles.add("default");
        jsonBody.add("profiles", profiles);
        jsonBody.addProperty("ephemeral", false);

        JsonObject config = new JsonObject();
//        config.put("limits.cpu", "2");
        jsonBody.add("config", config);

        if(type == Instance.Type.Container) {
            jsonBody.addProperty("type", "container");
        } else if (type == Instance.Type.VirtualMachine) {
            jsonBody.addProperty("type", "virtual-machine");
        }

        JsonObject source = new JsonObject();
        source.addProperty("type", "image");
        source.addProperty("fingerprint", fingerPrint);
        jsonBody.add("source", source);
        String jsonBodyString = jsonBody.getAsString();
        String result = this.request(Lxd.Method.POST, "/1.0/instances", "Content-type: text/json\r\n" + "Content-length: " + jsonBodyString.length(), jsonBodyString);

        return this.getOperation(JsonParser.parseString(result).getAsJsonObject().get("operation").getAsString());
    }

    public Image getImage(String name) throws Exception {
        return new Image(this, JsonParser.parseString((String) this.request(Method.GET, name)).getAsJsonObject().getAsJsonObject("metadata"));
    }

    public Image[] getImages() throws Exception {
        JsonArray images = JsonParser.parseString(this.request(Method.GET, "/1.0/images")).getAsJsonObject().getAsJsonArray("metadata");
        Image[] imagesList = new Image[images.size()];
        for (int i = 0; i < imagesList.length; i++) {
            imagesList[i] = this.getImage(images.get(i).getAsString());
        }

        return imagesList;
    }

    public Image[] getImages(Image.Type type) throws Exception {
        Image[] images = this.getImages();
        Image[] tmpImages = new Image[images.length];
        int arrLen = 0;
        for (Image image : images) {
            if (image.getType() == Image.Type.All || image.getType() == type) {
                tmpImages[arrLen++] = image;
            }
        }

        Image[] finalImages = new Image[arrLen];
        for (int arrInd = 0; arrInd < arrLen; arrInd++) {
            finalImages[arrInd] = tmpImages[arrInd];
        }

        return finalImages;
    }

    public Project getProject(String name) throws Exception {
        return new Project(this, JsonParser.parseString(this.request(Method.GET, name)).getAsJsonObject().getAsJsonObject("metadata"));
    }

    public Project[] getProjects() throws Exception {
        JsonArray projects = JsonParser.parseString(this.request(Method.GET, "/1.0/projects")).getAsJsonObject().getAsJsonArray("metadata");
        Project[] projectsList = new Project[projects.size()];
        for (int i = 0; i < projectsList.length; i++) {
            projectsList[i] = this.getProject(projects.get(i).getAsString());
        }

        return projectsList;
    }

    public Operation getOperation(String name) throws Exception {
        return new Operation(this, JsonParser.parseString(this.request(Method.GET, name)).getAsJsonObject().get("metadata").getAsJsonObject());
    }

    public Operation[] getOperations(Operation.State state) throws Exception {
        JsonArray operations = JsonParser.parseString(this.request(Method.GET, "/1.0/operations")).getAsJsonObject().getAsJsonObject("metadata").getAsJsonArray(state.toString().toLowerCase());
        Operation[] operationsList = new Operation[operations.size()];
        for (int i = 0; i < operationsList.length; i++) {
            operationsList[i] = this.getOperation(operations.get(i).getAsString());
        }

        System.out.println("I found your executionz");

        return operationsList;
    }
}
