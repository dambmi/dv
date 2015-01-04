package de.mida.vidorg;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * Created by HP on 03.01.15.
 */
public class FileIdentifier {

    Logger LOG = Logger.getLogger(FileIdentifier.class.getName());

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        new FileIdentifier().identify("p:\\tatort\\ARD Mediathek Tatort - Borowski und der freie Fall (FSK  tgl- ab 20 Uhr) - Sonntag, 14-10-2012  Das Erste.flv");
        new FileIdentifier().identify("c:\\Dokumente und Einstellungen/HP/Eigene Dateien/Downloads/ARD Mediathek Tatort - Borowski.flv");
        new FileIdentifier().identify("p:/tatort//ARD Mediathek Tatort - Nachtkrapp (FSK  tgl- ab 20 Uhr) - Sonntag, 07-10-2012  Das Erste - 1.flv");
    }

    private static String hashFile(File file, String algorithm) throws IOException, NoSuchAlgorithmException {

        System.out.println("FileIdentifier.hashFile " + file + "," + algorithm);
        FileInputStream inputStream = new FileInputStream(file);
        MessageDigest digest = MessageDigest.getInstance(algorithm);

        byte[] bytesBuffer = new byte[1024 * 1024];
        //to contain 10 bytes from beginning, 10 in the middle, 10 at the end:
        byte[] fileBytes = new byte[30];
        int bytesRead = -1;
        int totalBytesRead = -1;
        int i = 0;
        while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
            totalBytesRead += bytesRead;
            System.out.println("FileIdentifier.hashFile bytesReaD=" + totalBytesRead);
            digest.update(bytesBuffer, 0, bytesRead);
            if (i == 0) {
                for (; i < 10; i++) {
                    fileBytes[i] = bytesBuffer[i];
                }
            } else if (i == 10) {
                for (; i < 20; i++) {
                    fileBytes[i] = bytesBuffer[i];
                }
            }


        }
        //last 10 bytes
        for (i = 20; i < 30; i++) {
            fileBytes[i] = bytesBuffer[i];
        }
        byte[] hashedBytes = digest.digest();

        return convertByteArrayToHexString(hashedBytes) + "_" + convertByteArrayToHexString(fileBytes);

    }

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

    private void identify(String path) throws IOException, NoSuchAlgorithmException {
        File f = new File(path);
        long start = System.currentTimeMillis();

        String hash = hashFile2(f, "md5");

        long end = System.currentTimeMillis();

        System.out.println("hash for " + f.getAbsolutePath() + "=" + hash + " time=" + (end - start) + "ms");

    }

    private String hashFile2(File f, String algorithm) throws IOException, NoSuchAlgorithmException {

        DataInputStream dis = new DataInputStream(new FileInputStream(f));

        int length = dis.available();

        //hash first 1024 bytes
        byte[] buf = new byte[1024];
        int num = dis.read(buf);

        if (num == -1) {
            throw new IOException("empty file");
        }
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(buf, 0, num);

        //hash 1024 bytes in the middle
        dis.skipBytes(length / 2);
        byte[] buf2 = new byte[1024];
        int num2 = dis.read(buf2);

        if (num2 == -1) {
            throw new IOException("file shorter than length said " + length);
        }
        digest.update(buf2, 0, num2);

        //add length to hash
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(length);
        byte[] lengthArray = b.array();
        digest.update(lengthArray);

        dis.close();
        byte[] hashedBytes = digest.digest();

        return convertByteArrayToHexString(hashedBytes);
    }
}
