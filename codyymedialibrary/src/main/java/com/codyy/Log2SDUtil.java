package com.codyy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by codyy on 2016/2/27.
 */
public class Log2SDUtil {
    public static void writeLog2SD(String log) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(log.getBytes());
            File file = MediaFileUtils.getOutputMediaFile(MediaFileUtils.DIRECTORY_DOCUMENTS,"");
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
