package org.example.fxchatdemo.models;

import javafx.application.Platform;
import org.example.fxchatdemo.ServerMain;
import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class ServerSSLSocketModel {
    private final String StorePass = "123456";
    private SSLContext sslCtx;

    // 声明服务器端套接字ServerSocket
    private SSLServerSocket sslServerSocket;
    // 输入流列表集合
    private ArrayList<BufferedReader> bReaders = new ArrayList<>();
    // 输出流列表集合
    private ArrayList<PrintWriter> pWriters = new ArrayList<>();
    // 聊天信息链表集合
    private LinkedList<String> msgList = new LinkedList<>();

//    public static void main(String[] args) {
//        System.out.println(KeyStore.getDefaultType());
//        System.out.println(KeyManagerFactory.getDefaultAlgorithm());
//    }
    public void init(){
        try {
            // "pkcs12"
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] password = StorePass.toCharArray();
            //用KeyStore对象加载keystore文件，需要密码
            keyStore.load(new FileInputStream("src/main/java/org/example/fxchatdemo/certs/server.keystore"), password);

            // "SunX509"
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password);
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

            sslCtx = SSLContext.getInstance("SSL");
            sslCtx.init(keyManagers, null , null);

        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException |
                 UnrecoverableKeyException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    public void openThePort(int port) {
        init();
        try {
            if (port > 0 && port < 65536) {
                sslServerSocket = (SSLServerSocket) sslCtx.getServerSocketFactory().createServerSocket(port);

                ServerMain.executeDisplay_label_status("当前状态: 服务已开启");
                // 创建接收客户端Socket的线程实例，并启动
                new AcceptSocketThread().start();
                // 创建给客户端发送信息的线程实例，并启动
                new SendMsgToClient().start();
                
                ServerMain.executeDisplay_label_count();
            } else {
                ServerMain.executeDisplay_label_status("当前状态: 端口错误");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendTheMsg(String msg) {
        for (PrintWriter printWriter : pWriters) {
            printWriter.print(msg);
            printWriter.flush();
        }
    }

    // 接收客户端Socket套接字线程
    class AcceptSocketThread extends Thread {
        public void run() {
            while (this.isAlive()) {
                try {
                    // 接收一个客户端Socket对象
                    SSLSocket sslsocket = (SSLSocket) sslServerSocket.accept();
                    // 建立该客户端的通信管道
                    if (sslsocket != null) {
                        // 获取Socket对象的输入流
                        BufferedReader bReader = new BufferedReader(
                                new InputStreamReader(sslsocket.getInputStream()));
                        BufferedReader bufferedReader_userId = new BufferedReader(
                                new InputStreamReader(sslsocket.getInputStream())
                        );
                        // 将输入流添加到输入流列表集合中
                        bReaders.add(bReader);
                        // 开启一个线程接收该客户端的聊天信息
                        new GetMsgFromClient(bReader).start();

                        // 获取Socket对象的输出流，并添加到输入出流列表集合中
                        pWriters.add(new PrintWriter(sslsocket.getOutputStream()));
                        Platform.runLater(() -> {
                            //更新JavaFX的主线程的代码放在此处
//                            NUM++;
//                            label_count.setText("当前客户端连接数: " + NUM);
                            ServerMain.executeAddlabel_count(1);
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // 接收客户端的聊天信息的线程，但该线程只是将消息添加到msgList中，并不将msg追加到textArea_reveive
    // 将msg追加到textArea_reveive中是在SendMsgToClient时进行的。
    class GetMsgFromClient extends Thread {
        BufferedReader bReader;
        public GetMsgFromClient(BufferedReader bReader) {
            this.bReader = bReader;
        }

        public void run() {
            while (this.isAlive()) {
                try {
                    // 从输入流中读一行信息
                    String strMsg = bReader.readLine();
                    if (strMsg != null) {
                        // SimpleDateFormat日期格式化类，指定日期格式为"年-月-日  时:分:秒",例如"2015-11-06 13:50:26"
                        SimpleDateFormat dateFormat = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss");
                        // 获取当前系统时间，并使用日期格式化类格式化为指定格式的字符串
                        String strTime = dateFormat.format(new Date());
                        // 将时间和信息添加到信息链表集合中
                        msgList.addFirst("<== " + strTime + " ==>\n" + strMsg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        //更新JavaFX的主线程的代码放在此处
//                        NUM--;
//                        label_count.setText("当前客户端连接数: " + NUM);
                        ServerMain.executeAddlabel_count(-1);
                    });
                    this.stop();    //如果把stop改为interrupt，线程并不会中止，运行会出错。
                }
            }
        }
    }

    // 给所有客户发送聊天信息的线程
    class SendMsgToClient extends Thread {
        public void run() {
            while (this.isAlive()) {
                try {
                    // 如果信息链表集合不空（还有聊天信息未发送）
                    if (!msgList.isEmpty()) {
                        // 取信息链表集合中的最后一条,并移除
                        String msg = msgList.removeLast();
//                        textArea_reveive.appendText(msg + "\n");
                        ServerMain.executeAppendtextArea_reveive(msg + "\n");
                        System.out.println(msg);
                        // 对输出流列表集合进行遍历，循环发送信息给所有客户端
                        for (PrintWriter pWriter : pWriters) {
                            pWriter.println(msg);
                            pWriter.flush();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}