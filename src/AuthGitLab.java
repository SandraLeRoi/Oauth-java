import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class AuthGitLab {
    private String clientId = "cda2150742b335cae521a45d1a00b9b268e74ccf9562c8113077df3d342589fc";
    private String clientSecret = "24a80ed87149bf6027fd161da335ad306ab38053d6f303774fdf68e19e9e54f3";
    private String redirectUri = "http://localhost:8888";

    private HttpServer server;
    private HttpClient client;

    public ArrayList<String> projects;

    public AuthGitLab() {
        client = HttpClient.newHttpClient();
    }

    public void openAuthorizationPage() {
        String url="https://gitlab.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code&state=STATE&scope=api";
        String finalUrl = String.format(url, clientId, redirectUri);

        try {
            Desktop.getDesktop().browse(new URI(finalUrl));
        } catch (Exception e) {
            System.out.println("Merci d'ouvrir ce lien "+ finalUrl);
        }
    }

    public void runHttpServer() {
        try {
            server = HttpServer.create(new InetSocketAddress("localhost",8888),0);
            server.createContext("/", new HandlerGitLab(this));
            server.start();
        } catch (IOException e){
            System.out.println("Oops there was an error");
        }
    }


    public void sendAuthCode(String code) {
        server.stop(0);
        String url = "https://gitlab.com/oauth/token";
        String parameters = "client_id=%s&client_secret=%s&grant_type=authorization_code&code=%s&redirect_uri=%s";
        String finalParameters = String.format(parameters,clientId, clientSecret, code, redirectUri);

        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(finalParameters))
                    .uri(new URI(url))
//                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept","application/json")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String content = response.body();
//            System.out.println(content);
            JSONObject data = (JSONObject) JSONValue.parse(content);
            String accessToken = (String) data.get("access_token");
            String refreshToken = (String) data.get("refresh_token");
//            System.out.println(accessToken);
            callApi(accessToken);
        } catch (URISyntaxException | IOException | InterruptedException e){
            System.out.println(e);
        }

    }

    public void callApi(String accessToken) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI("https://gitlab.com/oauth/token/info"))
                    .header("Accept","application/json")
                    .header("Authorization","Bearer "+accessToken)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject data = (JSONObject) JSONValue.parse(response.body());
            System.out.println(data);
//            String resourceOwnerId = (String) data.get("resource_owner_id");
//            System.out.println(resourceOwnerId);
            nameRepo(accessToken);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void nameRepo(String accessToken) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI("https://gitlab.com/api/v4/projects?owned=true&statistics=true"))
                    .header("Accept","application/json")
                    .header("Authorization","Bearer "+accessToken)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray data = (JSONArray) JSONValue.parse(response.body());
            System.out.println(data);
            projects = new ArrayList<>();
            for (Object name : data) {
                JSONObject nameFile = (JSONObject) name;
                String nameProject = (String) nameFile.get("name");

                JSONObject stat =  (JSONObject) nameFile.get("statistics");
                Long commitNumber = (Long) stat.get("commit_count");
                projects.add(nameProject + " " + commitNumber);

//                System.out.println(nameProject);
//                System.out.println(stat);
//                System.out.println(commitNumber);;
            }
            System.out.println(projects);

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
