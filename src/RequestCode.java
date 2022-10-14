import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public final class RequestCode {
    static HashMap<String, Integer> values;

    static int get(String p_key) {
        return values.get(p_key);
    }

    static { // Parse `AGC_RequestCodes.properties`.
        values = new HashMap<>();

        File file = new File("data", "AGC_RequestCodes.properties");
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
    }

}
