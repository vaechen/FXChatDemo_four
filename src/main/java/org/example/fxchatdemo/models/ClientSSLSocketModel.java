package org.example.fxchatdemo.models;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.example.fxchatdemo.ClientMain;
import javax.net.ssl.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;

public class ClientSSLSocketModel {
    private final String StorePass = "123456";
    SSLContext sslCtx;
    SSLSocket sslSocket;
    PrintWriter pWriter;
    BufferedReader bReader;
    InetAddress ip;
    public InetAddress getIp() {
        return ip;
    }
    public ClientSSLSocketModel() throws UnknownHostException {
        ip = InetAddress.getLocalHost();
    }
    public void init() {
        try {
            // "pkcs12"
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] password = StorePass.toCharArray();
            //用KeyStore对象加载keystore文件，需要密码
            keyStore.load(new FileInputStream("src/main/java/org/example/fxchatdemo/certs/Trust.keystore"), password);

            // "SunX509"
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            sslCtx = SSLContext.getInstance("SSL");
            sslCtx.init(null, trustManagers , null);

        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException |
                 KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }
    public void connect(String host, int port) {
        init();
        try {
            // 创建一个套接字
            sslSocket = (SSLSocket) sslCtx.getSocketFactory().createSocket(host, port);
            // 创建一个往套接字中写数据的管道，即输出流，给服务器发送信息
            pWriter = new PrintWriter(sslSocket.getOutputStream());
            // 创建一个从套接字读数据的管道，即输入流，读服务器的返回信息
            bReader = new BufferedReader(new InputStreamReader(
                    sslSocket.getInputStream()));

            sendTheHello();
            sendTheMsg();

            new GetMsgFromServer().start();

        } catch (UnknownHostException e) {
            e.printStackTrace();
            ClientMain.executeDisplay_label_status("当前状态：连接失败");
        } catch (IOException e) {
            e.printStackTrace();
            ClientMain.executeDisplay_label_status("当前状态：传输错误");
        }
    }

    public void sendTheHello() {
        pWriter.println("本地IP为:[" + ip.getHostAddress() + "]的用户加入聊天室");
        pWriter.flush();
        ClientMain.executeDisplay_label_status("当前状态：已连接");
    }

    public void sendTheBye(String msg) {
        pWriter.println(msg);
        pWriter.flush();
    }

    public void sendTheMsg() throws UnknownHostException {
        String strName = ClientMain.executeGet_textField_username();
        String strMsg = ClientMain.executeGet_textArea_message();
        if (!strMsg.isEmpty()) {
            pWriter.println("[" + strName + "]" + "(" + ip + ") 说:" + strMsg);
            pWriter.flush();
            ClientMain.executeSet_textArea_message("");
        }
    }

    class GetMsgFromServer extends Thread {
        private String path = "src/main/java/org/example/fxchatdemo/notification.mp3";
        private Media media = new Media(new File(path).toURI().toString());

        public void run() {
            while (this.isAlive()) {
                try {
                    // 从输入流中读一行信息
                    String strMsg = bReader.readLine();
                    if (strMsg != null) {
                        ClientMain.executeAppend_textArea_reveive(strMsg + "\n");
                        MediaPlayer mediaPlayer = new MediaPlayer(media);
                        mediaPlayer.play();
                    }
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}