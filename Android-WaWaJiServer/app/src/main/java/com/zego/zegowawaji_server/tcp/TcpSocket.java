package com.zego.zegowawaji_server.tcp;

import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by admin on 2017/11/11.
 */

public class TcpSocket {
    /**
     * 发送数据的客户端Socket
     */
    private Socket socket;

    private OutputStream out = null;

    public TcpSocket(){
        this("wawa.fomivip.com",8282);
    }

    /**
     * @param ip   接收方的ip地址
     * @param port 接收方的端口号
     */
    public TcpSocket(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            out = socket.getOutputStream();
            Log.i("---", "isBound" + socket.isBound() + " isConnected" + socket.isConnected());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送数据
     */
    public void sendMessage(String msg) {
        if(socket == null){
            return;
        }
        Log.i("---", "isBound" + socket.isBound() + " isConnected：" + socket.isConnected()+",msg");

        try {
            Log.i("---","sendMessage try 1");
            out.write(msg.getBytes());
            Log.i("---","sendMessage try 2");
            out.flush();
            Log.i("---", msg);
            receive(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receive(Socket s){
        try {
            InputStream inputStream = s.getInputStream();
            DataInputStream input = new DataInputStream(inputStream);
            byte[] b = new byte[10000];
            int length = input.read(b);
            String Msg = new String(b, 0, length, "gb2312");
            Log.v("---","receive:"+Msg);
            close();
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket.isInputShutdown()) { //判断输入流是否为打开状态
            try {
                socket.shutdownInput();  //关闭输入流
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket.isOutputShutdown()) {  //判断输出流是否为打开状态
            try {
                socket.shutdownOutput(); //关闭输出流（如果是在给对方发送数据，发送完毕之后需要关闭输出，否则对方的InputStream可能会一直在等待状态）
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket.isConnected()) {  //判断是否为连接状态
            try {
                socket.close();  //关闭socket
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
