package cert;

import sun.security.ec.CurveDB;

import java.io.FileInputStream;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.regex.Pattern;

public class CertLoadDemo {

    public static void main(String[] args) throws Exception {
        /* 取出证书--从文件中取出 */
        String certpath = "/tomcat_cert/CA.pem";
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        FileInputStream in1 = new FileInputStream(certpath);
        Certificate c = cf.generateCertificate(in1);
        X509Certificate x509Cert = (X509Certificate) c;

        // JAVA程序中显示证书指定信息
        System.out.println("输出证书信息:"+c.toString());
        System.out.println("版本号:"+x509Cert.getVersion());
        System.out.println("序列号:"+x509Cert.getSerialNumber().toString(16));
        System.out.println("主体名："+x509Cert.getSubjectDN());
        System.out.println("签发者："+x509Cert.getIssuerDN());
        System.out.println("有效期："+x509Cert.getNotBefore());
        System.out.println("签名算法："+x509Cert.getSigAlgName());
        byte [] sig=x509Cert.getSignature();//签名值
        System.out.println("签名值："+ Arrays.toString(sig));
        PublicKey pk=x509Cert.getPublicKey();
        byte [] pkenc=pk.getEncoded();
        System.out.println("公钥");
        for (byte b : pkenc)
            System.out.print(b + ",");
    }

}
