package me.MaxBans.SkyWars.Maps;

import java.io.*;

public final class FileUtil {

    public static void copy(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdir();
            }

            String[] files = source.list();
            if (files == null) return;
            for (String file : files) {
                File newSource = new File(source, file);
                File newDestination = new File(destination, file);
                copy(newSource, newDestination);
            }
        } else {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(destination);

            byte[] buffer = new byte[1024];
            int length;

            //copy the file content in bytes

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    static void delete(File file) throws IOException {
        if (file.isDirectory()) {
            File[] entries = file.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    delete(entry);
                }
            }
        }
        if (!file.delete()) {
            throw new IOException("Failed to delete " + file);
        }
    }
}
