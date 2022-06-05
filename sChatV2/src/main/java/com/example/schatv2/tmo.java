package com.example.schatv2;

import javafx.scene.Scene;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class tmo {
    public static void main(String[] args) throws IOException {
        ServerSocket ser = new ServerSocket(9999);

        Socket soc = new Socket("192.168.1.8",9999);
ser.accept();
//        System.out.println(soc.());



    }
}
