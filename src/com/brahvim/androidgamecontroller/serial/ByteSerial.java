package com.brahvim.androidgamecontroller.serial;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// Bite Cereal! ":D!
// *Just add milk!*
public class ByteSerial {
    @Nullable
    public static byte[] encode(Serializable p_object) {
        try {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                bos.flush();
                oos.flush();

                oos.writeObject(p_object);

                oos.flush();
                oos.close();

                bos.flush();
                bos.close();
                return bos.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static Object decode(byte[] p_data) {
        try {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(p_data);
                    ObjectInputStream ois = new ObjectInputStream(bis)) {
                return ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
