package stallitium;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ServerConfig {
    private int port;
    private int maxClients;

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setMaxClients(int maxClients) {
        this.maxClients = maxClients;
    }

    public int getMaxClients() {
        return maxClients;
    }

    //デフォルトの値の入ったインスタンス
    public static ServerConfig getDefaultConfig() {
        ServerConfig config = new ServerConfig();
        config.setPort(20100);
        config.setMaxClients(20);
        return config;
    }

    //jsonが存在する場合読み込み存在しない場合は生成
    public static ServerConfig loadConfig() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("serverConfig.json");
        ServerConfig config = null;
        if (file.exists()) {
            try {
                //ファイルを読む 一致するkeyのvalueを抜き出す
                config = mapper.readValue(file,ServerConfig.class);
                LOG.getLogger().info("Read ServerConfig Completed");
            } catch (IOException e) {
                LOG.getLogger().warning(Arrays.toString(e.getStackTrace()));
            }
        } else {
            //ファイルがない場合デフォルトを代入しそれを保存する
            config = ServerConfig.getDefaultConfig();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            LOG.getLogger().info("ServerConfig.json was not found");
            try {
                mapper.writeValue(file,config);
                LOG.getLogger().info("SaveDefaultServerConfig Completed");
            } catch (IOException e) {
                LOG.getLogger().warning(Arrays.toString(e.getStackTrace()));
            }
        }
        return config;
    }

    public static void saveConfig(ServerConfig serverConfig) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            File file = new File("serverConfig.json");
            mapper.writeValue(file,serverConfig);
        } catch (IOException e) {
            LOG.getLogger().warning(Arrays.toString(e.getStackTrace()));
        }
    }
}
