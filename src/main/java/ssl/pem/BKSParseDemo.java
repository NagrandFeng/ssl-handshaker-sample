package ssl.pem;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Set;

public class BKSParseDemo {
    static {
        Security.addProvider(new BouncyCastleProvider());

    }
    static final String pemPath = "SS.pem";

    public static void main(String[] args) {
        try {
            saveGMKeyStore();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void saveGMKeyStore() throws Exception {

        //证书解析部分
        byte[] certAndKey = FileUtil.inputStream2ByteArray(pemPath);

        byte[] certBytes = FileUtil.parseDERFromPEM(certAndKey
                , "-----BEGIN CERTIFICATE-----"
                , "-----END CERTIFICATE-----");
        byte[] privateKeyBytes = FileUtil.parseDERFromPEM(certAndKey
                , "-----BEGIN PRIVATE KEY-----"
                , "-----END PRIVATE KEY-----");

        System.out.println("=============测试国密证书PKCS12 KeyStore存取=============");
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X509", "BC");
        Certificate certificate = certificateFactory.generateCertificate(new ByteArrayInputStream(certBytes));

        KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
        keyStore.load(null, null);
        keyStore.setKeyEntry("gm-test", privateKey, "tomcat".toCharArray(), new Certificate[]{certificate});
        keyStore.store(new FileOutputStream("afterKeyStore.p12"), "tomcat".toCharArray());

        KeyStore keyStore1 = KeyStore.getInstance("PKCS12", "BC");
        keyStore1.load(new FileInputStream("afterKeyStore.p12"), "tomcat".toCharArray());
        Certificate certificate1 = keyStore1.getCertificate("gm-test");

        PrivateKey privateKey1 = (PrivateKey) keyStore1.getKey("gm-test", "tomcat".toCharArray());
        System.out.println("公钥证书存取前后对比:" + Arrays.equals(certificate1.getEncoded(), certificate.getEncoded()));
        System.out.println("私钥存取前后对比:" + Arrays.equals(privateKey.getEncoded(), privateKey1.getEncoded()));
        System.out.println("=============测试国密证书PKCS12 KeyStore存取=============");

    }



}
