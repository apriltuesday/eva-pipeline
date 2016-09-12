package uk.ac.ebi.eva.test.utils;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CommonUtils {

    public static DBObject constructDbo(String variant) {
        return (DBObject) JSON.parse(variant);
    }

    public static String readFirstLine(File file) throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            return reader.readLine();
        }
    }

}
