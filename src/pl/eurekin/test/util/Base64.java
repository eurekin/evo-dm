package pl.eurekin.test.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static org.apache.commons.codec.binary.Base64.*;
/**
 *
 * @author eurekin
 */
public class Base64 {

    public static String serializeObjectToString(Object o) {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.flush();
            oos.close();
            baos.close();
            // String res = enc.encode(baos.toByteArray());
            String res = encodeBase64String(baos.toByteArray());
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
            byte[] decodeBuffer = decodeBase64(str);
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
