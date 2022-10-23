package com.brahvim.androidgamecontroller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class StringTable {
    static HashMap<String, String> table = StringTable.parse("AGC_StringTable.ini");

    static HashMap<String, String> parse /* parseStringTableFromFile */(String p_fileName) {
        HashMap<String, String> parsedMap = new HashMap<>();
        File tableFile = new File("data", p_fileName);

        if (tableFile.exists()) {
            System.out.println("Found string table file!");
            try (BufferedReader reader = new BufferedReader(new FileReader(tableFile))) {
                String section = null, content = null;
                StringBuilder parsedContent;
                int eqPos = 0, lineLen = 0, newLineCharPos = 0;

                // Remember that this loop goes through EACH LINE!
                // Not each *character!* :joy::
                for (String line; (line = reader.readLine()) != null;) {
                    lineLen = line.length();

                    // Leave empty lines alone!:
                    if (line.isBlank())
                        continue;

                    // Skipping comments and registering sections,
                    // and skip this iteration if they exist:
                    switch (line.charAt(0)) {
                        case '#':
                            continue;
                        case '[':
                            section = line.substring(1, line.indexOf(']'));
                            continue;
                    }

                    // Find where the `=` sign is!:
                    eqPos = line.indexOf('=');
                    content = line.substring(eqPos + 1, lineLen);

                    // Parse out `\n`s!:
                    parsedContent = new StringBuilder(content);

                    while ((newLineCharPos = parsedContent.indexOf("\\n")) != -1) {
                        // Causes an infinite loop, and I won't be writing `\n` anywhere, anyway:
                        // if (parsedContent.charAt(newLineCharPos - 1) == '\\')
                        // continue;

                        for (int i = 0; i < 2; i++)
                            parsedContent.deleteCharAt(newLineCharPos);
                        parsedContent.insert(newLineCharPos, '\n');
                    }

                    // if (content.contains("<br>"))
                    // content = content.replace("\\\\n", App.NEWLINE);

                    parsedMap.put(
                            // Format: `SectionName.propertyName`:
                            section.concat(".")
                                    .concat(line.substring(0, eqPos)),
                            parsedContent.toString());
                }
            } catch (IOException e) {
                System.out.println("Failed to read string table file!");
                e.printStackTrace();
            }
        }
        return parsedMap;
    }

    public static String get(String p_key) {
        return StringTable.table.get(p_key);
    }
}
