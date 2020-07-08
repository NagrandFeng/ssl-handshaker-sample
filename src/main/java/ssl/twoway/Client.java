package ssl.twoway;

import java.io.*;
import java.security.KeyStore;


import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

public class Client {

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 8443;

    private static final String KEYSTORE_PATH="/src/main/resources/openssl/client.p12";
    private static final String TRUSTSTORE_PATH="/src/main/resources/openssl/ca_root.p12";

    private static final String CLIENT_KEY_STORE_PASSWORD = "123456";
    private static final String CLIENT_TRUST_KEY_STORE_PASSWORD = "123456";

    private SSLSocket sslSocket;


    public static void main(String[] args) {
        Client client = new Client();
        client.process();
    }
    public Client() {
        init();
    }



    public void process() {
        if (sslSocket == null) {

            return;
        }
        try {
            InputStream input = sslSocket.getInputStream();
            OutputStream output = sslSocket.getOutputStream();

            BufferedInputStream bis = new BufferedInputStream(input);
            BufferedOutputStream bos = new BufferedOutputStream(output);

            bos.write("hello this is client".getBytes());
            bos.flush();

            byte[] buffer = new byte[1024];
            bis.read(buffer);
            System.out.println("接收到服务器端消息:");
            System.out.println(new String(buffer));

            sslSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1.导入客户端私钥KeyStore；
     * 2.导入客户端trustStore
     */
    public void init() {

        String userDir =System.getProperty("user.dir");
        String keyStoreFilePath = userDir + "/" + KEYSTORE_PATH;
        String trustStoreFilePath = userDir + "/" + TRUSTSTORE_PATH;


        try {
            SSLContext ctx = SSLContext.getInstance("SSL");

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");

            KeyStore keyStore = KeyStore.getInstance("pkcs12");
            KeyStore trustStore = KeyStore.getInstance("pkcs12");

            keyStore.load(new FileInputStream(keyStoreFilePath), CLIENT_KEY_STORE_PASSWORD.toCharArray());
            trustStore.load(new FileInputStream(trustStoreFilePath), CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());

            kmf.init(keyStore, CLIENT_KEY_STORE_PASSWORD.toCharArray());
            tmf.init(trustStore);

            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            sslSocket = (SSLSocket) ctx.getSocketFactory().createSocket(DEFAULT_HOST, DEFAULT_PORT);
            sslSocket.setUseClientMode(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
