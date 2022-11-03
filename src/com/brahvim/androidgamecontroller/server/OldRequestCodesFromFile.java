package com.brahvim.androidgamecontroller.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public final class OldRequestCodesFromFile {
    static HashMap<String, Integer> values;
    static HashMap<Integer, String> keys; // ...not in the mood to uyse `BiDiMap`/`BiMap`!
    // ...apparently it's from a library, "Guava". No. Please no.
    // No more dependencies!

    static int get(String p_key) {
        return values.get(p_key);
    }

    public static byte[] toBytes(String p_req) {
        byte[] ret = ByteBuffer.allocate(Integer.BYTES)
                .putInt(OldRequestCodesFromFile.values.get(p_req)).array();
        System.out.printf("Converted request code to `byte[]` of length: `%d`.\n", ret.length);
        return ret;
    }

    public static String fromBytes(byte[] p_bytes) {
        byte[] firstInt = new byte[Integer.BYTES];
        System.arraycopy(p_bytes, 0, firstInt, 0, firstInt.length);

        return keys.get(ByteBuffer.wrap(firstInt).getInt());
    }

    static { // Parse `AGC_RequestCodes.properties`.
        values = new HashMap<String, Integer>();

        File file = new File("data", "AgcRequestCodes.properties");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            int eqPos, numEnd;
            for (String line; (line = reader.readLine()) != null;) {
                if (line.isBlank()) // Need to handle this separately...
                    continue;
                if (line.charAt(0) == '#')
                    continue;

                eqPos = line.indexOf('=');
                numEnd = line.indexOf('#');

                if (numEnd == -1)
                    numEnd = line.length();

                values.put(line.substring(0, eqPos),
                        Integer.parseInt(line.substring(eqPos + 1, numEnd)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Revert the `values` `HashMap` and store the reverted values into `keys`.
        // Too lazy to use `BiDiMap`/`BiMap`! :joy:
        // ...apparently it's from a library, "Guava". No. Please no.
        // No more dependencies!
        keys = new HashMap<Integer, String>();

        for (Map.Entry<String, Integer> entry : values.entrySet())
            keys.put(entry.getValue(), entry.getKey());
    }
}
