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
//    private static FileWriter fileWriter;

    private static BufferedInputStream bufferedInputStream;
    private static BufferedOutputStream bufferedOutputStream;
    private static final int bufferSize = 10 * 1024;

    public static String Sender;
    public static String Receiver;

    public static boolean initializeClient(String ip, int port, String name) {
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
            return true;

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Error initializing the client side");
        }
        return false;
    }


    public static boolean initializeServer(int port, String name) {
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
            return true;
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Error initializing the sever side");
        }
        return false;
    }

    public static int receiveMode() {
        if (!initialized) return -1;

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            return dataInputStream.readInt();

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Error receiving mode");
            System.exit(0);
        }

        return -1;
    }

    public static String receiveText() {
        if (!initialized) return null;

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String hash = crypto.decryptAES(dataInputStream.readUTF());
            String message = crypto.decryptAES(dataInputStream.readUTF());

            if (crypto.checkerText(message, "SHA-256", hash)) {
                System.out.println("received");
                return message;
            }

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Error receiving message");
        }
        return null;

    }

    public static boolean sendText(String text) {
        if (!initialized) return false;
        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream.writeInt(0);

            String hash = crypto.hashingText(text, "SHA-256");

            dataOutputStream.writeUTF(crypto.byte2string(crypto.encryptAES(hash)));
            dataOutputStream.writeUTF(crypto.byte2string(crypto.encryptAES(text)));

            return true;
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Error sending message");
        }
        return false;
    }

    public static void receiveThread(TextArea textArea) {
        Thread thread = new Thread(() -> {
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

        });
        thread.setDaemon(true);
        thread.start();

    }

    public static boolean receiveFile() {
        try {

            File tmp = new File("received");
            if (!tmp.mkdir() && !tmp.exists()) {
                System.out.println("Error creating receveing folder ");
                return false;
            }
            dataInputStream = new DataInputStream(socket.getInputStream());
            String hash = crypto.decryptAES(dataInputStream.readUTF());
            File file = new File("received/" + dataInputStream.readUTF());
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bufferedInputStream = new BufferedInputStream(dataInputStream);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            long fileLength = dataInputStream.readLong();
            int lastblock = (int) (fileLength - (fileLength / 10240) * 10240);

            byte[] buffer = new byte[bufferSize + 16];
            byte[] decrypted;

            long startTime = System.nanoTime();
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
//                System.out.println(bytesRead);

                if (bytesRead == 10256) {
                    bytesRead = 10240;
                    decrypted = crypto.decryptAES(buffer, 1);
                } else {
                    bytesRead = lastblock;
                    decrypted = crypto.decryptAES(buffer, 0);
                    bufferedOutputStream.write(decrypted, 0, bytesRead);
                    break;
                }
                bufferedOutputStream.write(decrypted, 0, bytesRead);
            }

            long stopTime = System.nanoTime();

            bufferedOutputStream.flush();
            fileOutputStream.close();


            System.out.printf("Done on %f! %n", (stopTime - startTime) / 100_000_0000.0);
            if (crypto.checkerFile(file, "SHA256", hash)) {
                System.out.println("file is ok");
                return true;

            }
            file.delete();
            System.out.println("corrupted");
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Error receiving file");
        }
        return false;
    }

    public static boolean sendFile(String path) {
        try {
            File file = new File(path);

            FileInputStream fileInputStream = new FileInputStream(file);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedOutputStream = new BufferedOutputStream(dataOutputStream);

            int size = bufferSize;
            long pre = -1, nex, current = 0, fileLength = file.length();
            byte[] encrypted;
            long startTime = System.nanoTime();
            byte[] buffer = new byte[size];

            dataOutputStream.writeInt(1);
            String hash = crypto.hashingFile(file, "SHA256");
            dataOutputStream.writeUTF(crypto.byte2string(crypto.encryptAES(hash)));
            dataOutputStream.writeUTF(file.getName());
            dataOutputStream.writeLong(fileLength);


            while (current != file.length()) {
                if (fileLength - current >= size)
                    current += size;
                else {
                    size = (int) (fileLength - current);
                    current = fileLength;
                    buffer = new byte[size];
                }

                bufferedInputStream.read(buffer, 0, size);
                encrypted = crypto.encryptAES(buffer, 0);
                bufferedOutputStream.write(encrypted, 0, encrypted.length);


                nex = (current * 100) / fileLength;
                if (pre != nex) {
                    System.out.println("Sending file ... " + nex + "% complete!");
                    pre = nex;
                }
            }
            long stopTime = System.nanoTime();

            bufferedOutputStream.flush();
            fileInputStream.close();


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
