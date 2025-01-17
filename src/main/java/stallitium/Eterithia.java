package stallitium;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Eterithia {
    public static void main(String[] args) {
        new Eterithia().eterithia();
    }

    ServerConfig serverConfig;
    private int port;
    private int maxClients;
    private ObjectMapper mapper = new ObjectMapper();
    private ObjectMapper thrower = new ObjectMapper();
    private String clientDataFilePath = "clientData.json";
    private Map<String,CInfo> clientMap = new ConcurrentHashMap<>();


    void eterithia() {
        LOG.getLogger().info("Load start");
        serverConfig = ServerConfig.loadConfig();
        this.port = serverConfig.getPort();
        this.maxClients = serverConfig.getMaxClients();
        LOG.getLogger().info("ServerConfig load Completed");
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT); //整形出力を有効化

        loadClientData();
        runServer();

    }

    synchronized void runServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOG.getLogger().info("Start server port : " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            LOG.logStackTrace(e);
        }
    }

    void loadClientData() {
        try {
            File file = new File(clientDataFilePath);

            if (file.exists() && file.length() != 0) {
                Map<String, CInfo> loadedMap = mapper.readValue(file, mapper.getTypeFactory().constructMapType(Map.class, String.class, CInfo.class));
                clientMap.putAll(loadedMap);
                LOG.getLogger().info("Loaded client data from file.");
            } else {
                saveClientData();
                LOG.getLogger().info("Not found ClientDataFile. Created ClientDataFile.");
            }
        } catch (IOException e) {
            LOG.logStackTrace(e);
        }
    }

    class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            CInfo cinfo = null;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))){
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);

                //クライアントから受信
                String json = in.readLine();
                LOG.getLogger().info("Received client ID: " + json);
                Map<String,String> message = mapper.readValue(json,Map.class);
                String clientId = message.get("clientId");

                cinfo = clientMap.get(clientId);

                if (cinfo == null) {
                    //新規クライアントにUUIDを付与
                    clientId = UUID.randomUUID().toString();
                    cinfo = new CInfo(clientId, "未設定");
                    saveClientData();

                    Map<String,String> resMap = new HashMap<>();
                    resMap.put("yourId",clientId);
                    out.println(thrower.writeValueAsString(resMap));
                    LOG.getLogger().info("Assigned new client ID: " + clientId);
                } else {
                    Map<String,String> resMap = new HashMap<>();
                    resMap.put("message","ID confirmed");
                    out.println(thrower.writeValueAsString(resMap));
                }
                LOG.getLogger().info("Client Connected displayName= "+cinfo.getDisplayName()+" id= "+cinfo.getClientId());

                String msg;
                while ((msg = in.readLine()) != null) {
                    //ここでメッセージの中身を読み取る
                    message = mapper.readValue(msg,Map.class);
                    if (message.containsKey("setName")) {
                        String displayName = message.get("setName");
                        cinfo.setDisplayName(displayName);
                        clientMap.put(cinfo.getClientId(),cinfo);
                        saveClientData();
                        Map<String,String> resMap = new HashMap<>();
                        resMap.put("message","YourDisplayName: "+displayName);
                        out.println(thrower.writeValueAsString(resMap));
                        LOG.getLogger().info("Set displayName to: " + displayName + " for client ID: " + cinfo.getClientId());
                    }
                    if (message.containsKey("message")) {

                    }
                    Map<String,String> resMap = new HashMap<>();
                    resMap.put("message",message.get("command"));
                    out.println(thrower.writeValueAsString(resMap));
                    LOG.getLogger().info(cinfo.getDisplayName()+": "+msg);

                }
            } catch (IOException e){
                //接続終了
//                LOG.logStackTrace(e);
            } finally {
                try {
                    if (cinfo != null) {
                        LOG.getLogger().info(cinfo.getDisplayName() + " has finished connecting.");
                    }
                    clientSocket.close();
                } catch (IOException e) {
                    LOG.logStackTrace(e);
                }
            }
        }
    }

    void saveClientData() {
        try {
            // 既存のデータを読み込み
            Map<String, CInfo> existingData = new ConcurrentHashMap<>();
            File file = new File(clientDataFilePath);
            if (file.exists() && file.length() !=0) {
                existingData = mapper.readValue(file, mapper.getTypeFactory().constructMapType(Map.class, String.class, CInfo.class));
            }
            // 新しいデータの書き込み
            existingData.putAll(clientMap);
            mapper.writeValue(file, existingData);
            LOG.getLogger().info("Client data saved successfully.");
        } catch (IOException e) {
            LOG.logStackTrace(e);
        }
    }
}