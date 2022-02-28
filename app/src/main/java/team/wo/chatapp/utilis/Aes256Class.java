package team.wo.chatapp.utilis;

import com.google.common.base.Charsets;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Aes256Class {

    private SecretKey secretKey;

    public Aes256Class() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);

            secretKey = keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    public String makeAes(String rawMessage, int cipherMode) {
        byte[] bytes = rawMessage.getBytes(Charsets.UTF_8);
        //    String text = new String(bytes, Charsets.UTF_8);
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(cipherMode, this.secretKey);
            byte[] output = cipher.doFinal(bytes);
             String output1 = new String(output, Charsets.UTF_8);

            return output1;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public  byte[] makeAes(byte[] rawMessage, int cipherMode) {
//        try {
//            Cipher cipher = Cipher.getInstance("AES");
//            cipher.init(cipherMode, this.secretKey);
//            byte[] output = cipher.doFinal(rawMessage);
//            return output;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
