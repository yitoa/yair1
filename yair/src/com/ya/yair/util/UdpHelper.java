
package com.ya.yair.util;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import android.util.Log;

public class UdpHelper {

    private DatagramSocket client;
    public static String result = null;
    // 是否收到设备返回的信息的
    private boolean isSbMessage = false;

    public UdpHelper() {
        try {
            if (client == null) {
                client = new DatagramSocket();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /***
     * 往设备上发送信息
     */
    public void sendSb(String ip, int port, String message) throws Exception {
        // 往设备发送信息
        SocketAddress target = new InetSocketAddress(ip, port);
        byte[] sendbuf = message.getBytes();
        DatagramPacket pack = new DatagramPacket(sendbuf, sendbuf.length, target);
        client.send(pack);
        receive(client);
    }

    // 接收请求内容主机返回的
    private void receive(DatagramSocket client) {
        try {
            for (;;) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                client.receive(packet);
                String receiveMessage = new String(packet.getData(), 0, packet.getLength());
                System.out.println(receiveMessage);
                result = receiveMessage;
                if (result != null) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 验证版本信息的
     */
    public void sendVerSb(String ip, int port, String message) throws Exception {
        // 往设备发送信息
        SocketAddress target = new InetSocketAddress(ip, port);
        byte[] sendbuf = message.getBytes();
        DatagramPacket pack = new DatagramPacket(sendbuf, sendbuf.length, target);
        client.send(pack);
        receiveVer(client);
    }

    // 接收请求内容主机返回的
    private void receiveVer(DatagramSocket client) {
        try {
            for (;;) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                client.receive(packet);
                String receiveMessage = new String(packet.getData(), 0, packet.getLength());
                Log.v("====receive===", receiveMessage);
                if (receiveMessage != null) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 设备返回的信息
     * 
     * @param client
     */
    private void sbReceive(DatagramSocket client) {
        try {
            for (;;) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                client.receive(packet);
                isSbMessage = true;
                String receiveMessage = new String(packet.getData(), 0, packet.getLength());
                System.out.println(receiveMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class TimeThread extends Thread {
        private DatagramSocket client;

        public TimeThread(DatagramSocket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                if (!isSbMessage) {// 没有返回信息 说明是对称性的nat。需要服务器中转命令。同时可以做一个标记

                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
