import facade.Ftp;
import facade.Reader;
import facade.SftpClient;
import facade.Writer;

public class FacadeMain {
    static void main() {
        // Facade 적용 전
//        Ftp ftpClient = new Ftp("localhost", 22, "/home/etc");
//        ftpClient.connect();
//        ftpClient.moveDirectory();
//
//        Writer writer = new Writer("text.tmp");
//        writer.fileConnect();
//        writer.write();
//
//        Reader reader = new Reader("text.tmp");
//        reader.fileConnect();
//        reader.fileRead();
//
//        reader.fileDisconnect();
//        writer.fileDisconnect();
//        ftpClient.disConnect();

        // Facade 적용 후
        SftpClient sftpClient = new SftpClient("localhost", 22, "/home/etc", "text.tmp");
        sftpClient.connect();
        sftpClient.write();
        sftpClient.read();
        sftpClient.disConnect();
    }
}
