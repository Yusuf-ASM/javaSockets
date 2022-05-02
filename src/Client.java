public class Client {
    public static void main(String[] args) throws Exception {
        transfer.initializeClient("localhost", 9999);
        transfer.receiveFile();
        transfer.sendFile("B.mkv");
//        new File("received").mkdir();
//
//        Socket socket = new Socket("192.168.1.2", 5000);
//        DataInputStream dis = new DataInputStream(socket.getInputStream());
//        FileOutputStream fos = new FileOutputStream("received/" + dis.readUTF());
//        BufferedOutputStream bos = new BufferedOutputStream(fos);
//        BufferedInputStream is = new BufferedInputStream(dis);
//
//        byte[] contents = new byte[102400];
//
//
//        int bytesRead;
//
//        long startTime = System.nanoTime();
//
//        while ((bytesRead = is.read(contents)) != -1) {
//            bos.write(contents, 0, bytesRead);
//        }
//
//        long stopTime = System.nanoTime();
//
//        //    bos.flush();
//        //    socket.close();
//
//        System.out.println("File saved successfully!");
//        System.out.println((stopTime - startTime) / 100_000_0000.0);
//

    }
}
