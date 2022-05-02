import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class transfer {
    private static DataOutputStream dataOutputStream;
    private static DataInputStream dataInputStream;
    private static DataInputStream textInputStream;
    private static BufferedInputStream bufferedInputStream;
    private static BufferedInputStream bufferedTextInput;
    private static BufferedOutputStream bufferedOutputStream;
    private static FileOutputStream fileOutputStream;
    private static FileInputStream fileInputStream;
    private static Socket socket;
    private static ServerSocket serverSocket;
    private static final int bufferSize = 10 * 1024;


    public static boolean initializeClient(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            return true;

        } catch (IOException e) {
            System.out.println("Error initializing the client side");
            return false;
        }
    }

    public static boolean initializeServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            return true;

        } catch (IOException e) {
            System.out.println("Error initializing the sever side");
            return false;
        }
    }

    public static boolean receiveFile() {
        try {

            File tmp = new File("received");
            if (!tmp.mkdir() && !tmp.exists()) {
                System.out.println("Error creating receveing folder ");
                return false;
            }

            dataInputStream = new DataInputStream(socket.getInputStream());
            fileOutputStream = new FileOutputStream("received/" + dataInputStream.readUTF());
            bufferedInputStream = new BufferedInputStream(dataInputStream);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            byte[] buffer = new byte[bufferSize];

            long startTime = System.nanoTime();
            int bytesRead;
            System.out.println("1");

            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                bufferedOutputStream.write(buffer, 0, bytesRead);
            }


            long stopTime = System.nanoTime();

            bufferedOutputStream.flush();

            System.out.printf("Done on %f ! %n", (stopTime - startTime) / 100_000_0000.0);

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
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedOutputStream = new BufferedOutputStream(dataOutputStream);

            dataOutputStream.writeUTF(file.getName());

            int size = bufferSize;
            long pre = -1, nex, current = 0, fileLength = file.length();
            long startTime = System.nanoTime();

            byte[] buffer = new byte[size];
            System.out.println(fileLength);
            while (current != file.length()) {
                if (fileLength - current >= size)
                    current += size;
                else {
                    size = (int) (fileLength - current);
                    current = fileLength;
                    buffer = new byte[size];
                }

                bufferedInputStream.read(buffer, 0, size);
                bufferedOutputStream.write(buffer);

                nex = (current * 100) / fileLength;
                if (pre != nex)
                    System.out.println("Sending file ... " + nex + "% complete!");
                pre = nex;
            }
            long stopTime = System.nanoTime();
//            bufferedOutputStream.write();

            bufferedOutputStream.flush();
            socket.shutdownOutput();


            System.out.printf("Done on %f ! %n", (stopTime - startTime) / 100_000_0000.0);
            Thread.sleep(20);

            return true;
        } catch (IOException e) {
            System.out.println("Error sending file");
        } catch (InterruptedException e) {
            System.out.println("thread");
        }

        return false;

    }

}
