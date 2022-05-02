public class Server {
    public static void main(String[] args) throws Exception {
        transfer.initializeServer(9999);
        transfer.sendFile("A.mkv");
        transfer.receiveFile();

//        File file = new File("[MSRT]Yu-Gi-Oh!GX-163[HQ][480p][8bit][701D23CE].mkv");
//        ServerSocket serverSocket = new ServerSocket(5000);
//        Socket socket = serverSocket.accept();
//
//        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
//        FileInputStream fis = new FileInputStream(file);
//        BufferedInputStream bis = new BufferedInputStream(fis);
//        BufferedOutputStream os = new BufferedOutputStream(dos);
//
//        dos.writeUTF(file.getName());
//
////Get socket's output stream
//        int size = 102400;
//        long pre = -1;
//        long nex;
//        byte[] contents = new byte[size];
//        long fileLength = file.length();
//        long current = 0;
//        long start = System.nanoTime();
//        while (current != fileLength) {
//
//            if (fileLength - current >= size)
//                current += size;
//            else {
//                size = (int) (fileLength - current);
//                current = fileLength;
//                contents = new byte[size];
//            }
//
//            bis.read(contents, 0, size);
//            os.write(contents);
//            nex = (current * 100) / fileLength;
//            if (pre != nex)
//                System.out.println("Sending file ... " + nex + "% complete!");
//            pre = nex;
//
//        }
//        long end = System.nanoTime();
//
//        os.flush();
////File transfer done. Close the socket connection!
//        socket.close();
//        serverSocket.close();
//        System.out.println("File sent succesfully!");
//        System.out.println((end - start) / 100_000_0000.0);
//
    }

}
