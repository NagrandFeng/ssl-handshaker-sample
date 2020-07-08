package cert;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import java.io.FileInputStream;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Set;

public class BouncyCastleProviderDemo {

    static String certpath = "";

    static {
        Security.addProvider(new BouncyCastleProvider());
        BouncyCastleProvider bc = new BouncyCastleProvider();
        Set<Provider.Service> services = bc.getServices();
        for (Provider.Service s : services) {
            if (s.toString().toUpperCase().contains("CIPHER")) System.out.println(s.toString());
        }
    }

    public static void main(String[] args) throws Exception {

        FileInputStream in1 = new FileInputStream(certpath);
        CertificateFactory fact = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
        Certificate c = fact.generateCertificate(in1);
        X509Certificate x509Cert = (X509Certificate) c;
        System.out.println(x509Cert.getClass().getName());
        System.out.println("CA Cert:" + Base64.toBase64String(x509Cert.getEncoded()));

        // JAVA程序中显示证书指定信息
        System.out.println("输出证书信息:" + c.toString());

        System.out.println("版本号:" + x509Cert.getVersion());
        System.out.println("序列号:" + x509Cert.getSerialNumber().toString(16));
        System.out.println("主体名：" + x509Cert.getSubjectDN());
        System.out.println("签发者：" + x509Cert.getIssuerDN());
        System.out.println("有效期：" + x509Cert.getNotBefore());
        System.out.println("oid：" + x509Cert.getSigAlgOID());
        System.out.println("签名算法：" + x509Cert.getSigAlgName());
        byte[] sig = x509Cert.getSignature();//签名值
        System.out.println("签名值：" + Arrays.toString(sig));
        PublicKey pk = x509Cert.getPublicKey();
//        String pkStr = x509Cert.getPublicKey();
//        System.out.println("pk:"+pkStr);



        System.out.println("公钥");
        for (byte b : pk.getEncoded())
            System.out.print(b + ",");

    }




}
