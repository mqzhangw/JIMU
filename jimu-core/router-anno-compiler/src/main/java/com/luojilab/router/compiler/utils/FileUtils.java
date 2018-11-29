package com.luojilab.router.compiler.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by mrzhang on 2017/12/20.
 */

public class FileUtils {


    /**
     * @param fileName
     */
    public static boolean createFile(String fileName) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * @param fileName
     * @param content
     */
    public static void writeStringToFile(String fileName, String content, boolean append) {
        BufferedWriter out = null;
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter osw = null;
        try {
            fileOutputStream = new FileOutputStream(fileName, append);
            osw = new OutputStreamWriter(fileOutputStream, "UTF-8");
            out = new BufferedWriter(osw);
            out.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
                if (osw != null)
                    osw.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
