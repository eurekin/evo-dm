package testrunner.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author eurekin
 */
public class Base64 {

    public static String serializeObjectToString(Object o) {
        ObjectOutputStream oos = null;
        try {
            BASE64Encoder enc = new BASE64Encoder();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.flush();
            oos.close();
            baos.close();
            String res = enc.encode(baos.toByteArray());
            return res;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                oos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static Object deserializeObjectFrom(String str) {
        try {
            BASE64Decoder dec = new BASE64Decoder();
            byte[] decodeBuffer = dec.decodeBuffer(str);
            ByteArrayInputStream bins = new ByteArrayInputStream(decodeBuffer);
            ObjectInputStream ois = new ObjectInputStream(bins);
            Object res = ois.readObject();
            return res;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
