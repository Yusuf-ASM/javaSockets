package com.example.schatv2;

import javafx.scene.control.TextArea;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class transfer {
    private static DataOutputStream dataOutputStream;
    private static DataInputStream dataInputStream;

    private static Socket socket;
    private static boolean initialized = false;
    private static FileWriter fileWriter;

    private static BufferedInputStream bufferedInputStream;
    private static BufferedOutputStream bufferedOutputStream;
    private static int bufferSize = 10 * 1024;

    public static String Sender;
    public static String Receiver;

    public static void initializeClient(String ip, int port, String name) {
        try {
            socket = new Socket(ip, port);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            crypto.initializeAES();
            crypto.initializeRSA();
            dataOutputStream.writeUTF(crypto.byte2string(crypto.getPublicKey()));
            dataOutputStream.writeUTF(crypto.dumpAES());

            initialized = true;
            System.out.println("done client");

            Sender = name;
            dataOutputStream.writeUTF(Sender);

            Receiver = dataInputStream.readUTF();


        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Error initializing the client side");
        }
    }


    public static void initializeServer(int port, String name) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            crypto.bytes2publicKey(crypto.string2byte(dataInputStream.readUTF()));
            crypto.dedumpAES(dataInputStream.readUTF());

            System.out.println("done sever");
            initialized = true;

            Sender = name;
            Receiver = dataInputStream.readUTF();

            dataOutputStream.writeUTF(Sender);

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Error initializing the sever side");
        }
    }

    public static int receiveMode() {
        if (!initialized) return -1;

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            return dataInputStream.readInt();

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Error receiving mode");
        }

        return -1;
    }

    public static String receiveText() {
        if (!initialized) return null;

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            String hash = (dataInputStream.readUTF());
            String message = crypto.decryptAES(dataInputStream.readUTF());
            if (crypto.checkerText(message, "SHA-256", hash)) {
                System.out.println("received");

            }
            return message;

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Error receiving message");
        }
        return null;

    }

    public static void receiveThread(TextArea textArea) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()) {
                    int mode = receiveMode();
                    if (mode == 0) {
                        String tmp = receiveText();
                        if (tmp != null) {
                            textArea.appendText(Receiver + ": " + tmp + "\n");
                        }
                    } else if (mode == 1) {
                        receiveFile();

                    }
                }

            }
        });
        thread.setDaemon(true);
        thread.start();

    }

    public static boolean sendText(String text) {
        if (!initialized) return false;
        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream.writeInt(0);
            String hash = crypto.hashingText(text, "SHA-256");
//            dataOutputStream.writeUTF(crypto.byte2string(crypto.encryptAES(hash)));
            dataOutputStream.writeUTF(hash);
            dataOutputStream.writeUTF(crypto.byte2string(crypto.encryptAES(text)));
            System.out.println("sent");

//            dataOutputStream.writeUTF(text);
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Error sending message");
        }
        return false;
    }

    public static boolean receiveFile() {
        try {

            File tmp = new File("received");
            if (!tmp.mkdir() && !tmp.exists()) {
                System.out.println("Error creating receveing folder ");
                return false;
            }

            dataInputStream = new DataInputStream(socket.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream("received/" + dataInputStream.readUTF());
            bufferedInputStream = new BufferedInputStream(dataInputStream);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            long size = dataInputStream.readLong();
            System.out.println(size);
            byte[] buffer = new byte[10256];

            long startTime = System.nanoTime();
            int bytesRead = 0;
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                byte[] yp;


//                System.out.println(buffer.length);
//                crypto.decryptAES(buffer);
                if (bytesRead == 10256) {
                    bytesRead = 10240;
                    yp = crypto.decryptAES(buffer);
                } else {
                    bytesRead = 3479;
                    yp = crypto.decryptAES2(buffer);
                }
//                System.out.println("y = " + yp.length + " b = " + buffer.length + " s = " + bytesRead);
                bufferedOutputStream.write(yp, 0, bytesRead);
            }

            long stopTime = System.nanoTime();

            bufferedOutputStream.flush();

            System.out.printf("Done on %f! %n", (stopTime - startTime) / 100_000_0000.0);

            return true;
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Error receiving file");
            return false;
        }
    }

    public static boolean sendFile(String path) throws IOException {
        try {
            File file = new File(path);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            FileInputStream fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedOutputStream = new BufferedOutputStream(dataOutputStream);

            dataOutputStream.writeUTF(file.getName());
            int size = bufferSize;
            long pre = -1, nex, current = 0, fileLength = file.length();
            long startTime = System.nanoTime();
            System.out.println(fileLength);

            dataOutputStream.writeLong(fileLength);


            byte[] buffer = new byte[size];

            while (current != file.length()) {
                if (fileLength - current >= size)
                    current += size;
                else {
                    size = (int) (fileLength - current);
                    current = fileLength;
                    buffer = new byte[size];
                }

                bufferedInputStream.read(buffer, 0, size);
//                System.out.println(size);
                byte[] yp = crypto.encryptAES(buffer);
//                System.out.println("y = " + yp.length + " b = " + buffer.length + " s = " + size);
                bufferedOutputStream.write(yp, 0, yp.length);

                nex = (current * 100) / fileLength;
                if (pre != nex)
//                    System.out.println("Sending file ... " + nex + "% complete!");
                    pre = nex;
            }
            long stopTime = System.nanoTime();

            bufferedOutputStream.flush();
            bufferedOutputStream.close();


            System.out.printf("Done on %f! %n", (stopTime - startTime) / 100_000_0000.0);
            Thread.sleep(20);

            return true;
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Error sending file");
        } catch (InterruptedException e) {
            System.out.println(e);
            System.out.println("thread");
        }

        return false;

    }

}
