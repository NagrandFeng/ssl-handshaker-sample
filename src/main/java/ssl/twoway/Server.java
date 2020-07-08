package ssl.twoway;

import java.io.*;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;

public class Server {

    private static final int DEFAULT_PORT = 7777;

    private static final String KEYSTORE_PATH="/src/main/resources/openssl/server.p12";
    private static final String TRUSTSTORE_PATH="/src/main/resources/openssl/ca_root.p12";

    private static final String SERVER_KEY_STORE_PASSWORD = "123456";
    private static final String SERVER_TRUST_KEY_STORE_PASSWORD = "123456";

    private SSLServerSocket serverSocket;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public Server() {
        init();
    }

    /**
     * <ul>
     * <li>Server Socket</li>
     * </ul>
     */
    public void start() {
        if (serverSocket == null) {
            System.out.println("ERROR");
            return;
        }
        while (true) {
            try {
                Socket s = serverSocket.accept();
                InputStream input = s.getInputStream();
                OutputStream output = s.getOutputStream();

                BufferedInputStream bis = new BufferedInputStream(input);
                BufferedOutputStream bos = new BufferedOutputStream(output);

                byte[] buffer = new byte[1024];
                bis.read(buffer);
                System.out.println("接收到客户端消息");
                System.out.println(new String(buffer));

                bos.write("hello this is server".getBytes());
                bos.flush();

                s.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    /**
     * 1.导入服务端私钥KeyStore；
     * 2.导入服务端trustStore
     */
    public void init() {
        String userDir =System.getProperty("user.dir");;
        String keyStoreFilePath = userDir + "/" + KEYSTORE_PATH;
        String trustStoreFilePath = userDir + "/" + TRUSTSTORE_PATH;

        try {
            SSLContext ctx = SSLContext.getInstance("SSL");

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");

            KeyStore ks = KeyStore.getInstance("pkcs12");
            KeyStore tks = KeyStore.getInstance("pkcs12");
            ks.load(new FileInputStream(keyStoreFilePath), SERVER_KEY_STORE_PASSWORD.toCharArray());
            tks.load(new FileInputStream(trustStoreFilePath), SERVER_TRUST_KEY_STORE_PASSWORD.toCharArray());

            kmf.init(ks, SERVER_KEY_STORE_PASSWORD.toCharArray());
            tmf.init(tks);

            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            serverSocket = (SSLServerSocket) ctx.getServerSocketFactory().createServerSocket(DEFAULT_PORT);
            serverSocket.setUseClientMode(false);
            serverSocket.setNeedClientAuth(true);  //需要验证客户端的身份
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
