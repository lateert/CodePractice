package cn.codepractice.oj.translate;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5
 * 
 * @author wangjingtao
 * 
 */
public class MD5 {
    // ，16
    private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f' };

    /**
     * MD5
     * 
     * @param input 
     * @return MD5
     * 
     */
    public static String md5(String input) {
        if (input == null) {
            return null;
        }

        try {
            // MD5（SHA1”SHA1”）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 
            byte[] inputByteArray = input.getBytes("utf-8");
            // inputByteArray
            messageDigest.update(inputByteArray);
            // ，，16
            byte[] resultByteArray = messageDigest.digest();
            // 
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * MD5
     * 
     * @param file
     * @return
     */
    public static String md5(File file) {
        try {
            if (!file.isFile()) {
                System.err.println("File not found or not a regular file: " + file.getAbsolutePath());
                return null;
            }

            FileInputStream in = new FileInputStream(file);

            String result = md5(in);

            in.close();

            return result;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String md5(InputStream in) {

        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                messagedigest.update(buffer, 0, read);
            }

            in.close();

            String result = byteArrayToHex(messagedigest.digest());

            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String byteArrayToHex(byte[] byteArray) {
        // new，（：byte，2（28162））
        char[] resultCharArray = new char[byteArray.length * 2];
        // ，（），
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }

        // 
        return new String(resultCharArray);

    }

}
