package com.paloit.training.sp01.utils;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class FileReader {
    public String getFile(String path) {
        try (
                var inputStream = getClass().getClassLoader()
                        .getResourceAsStream(path);
                BufferedReader buffReader = new BufferedReader(new InputStreamReader(inputStream));
        ) {

            String line;
            StringBuilder stringBuffer = new StringBuilder();
            while ((line = buffReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            return stringBuffer.toString();
        } catch (IOException | NullPointerException ex) {
            throw new IllegalArgumentException("Could not read file " + path);
        }
    }
}
