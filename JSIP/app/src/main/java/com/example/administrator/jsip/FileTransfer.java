package com.example.administrator.jsip;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileTransfer {
    private String fPth;
    private String basedFile;
    public FileTransfer(String filename,String file){
        base64ToFile(filename,file);
    }
    public FileTransfer(File file){
        Log.i("filetypeeeeeeeeeeeeee", file.getAbsolutePath());
        basedFile = fileToBase64(file);
    }
    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            //readAll(in);
            byte[] bytes = readAll(in);
            int length = bytes.length;
            Log.i("lenthhhhhhhhhhhhhhhhh", Integer.toString(length));
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
            Log.i("2bbbbbbbbbbb", base64);
            //basedFile = base64;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return base64;
    }
    public File base64ToFile(String fileName,String file){
        File BaseTOFile = null;
        FileOutputStream out = null;
        try {
            // 解码，然后将字节转换为文件
            System.out.println("jjjjjjjjjjjjj-------"+fileName);
            BaseTOFile = new File(fileName);
            if (!BaseTOFile.exists())
                BaseTOFile.createNewFile();
            byte[] bytes = Base64.decode(file, Base64.DEFAULT);// 将字符串转换为byte数组
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            byte[] buffer = new byte[1024];
            out = new FileOutputStream(BaseTOFile);
            int bytesum = 0;
            int byteread = 0;
            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread); // 文件写操作
            }
            fPth = BaseTOFile.getCanonicalPath();
            System.out.println("Recived file Path: "+fPth);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (out!= null) {
                    out.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return BaseTOFile;
    }

    public String getfPth() {
        return fPth;
    }
    public static byte[] readAll(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        int capacity = 4096;
        int size = 0;
        byte[] data = new byte[capacity];
        while (true) {
            int ret = bis.read();
            if (ret < 0)
                break;
            if (size == capacity) {
                int newCapacity = capacity << 1;
                byte[] newData = new byte[newCapacity];
                System.arraycopy(data, 0, newData, 0, size);
                data = newData;
                capacity = newCapacity;
            }
            data[size++] = (byte)ret;
        }

        byte[] res = new byte[size];
        System.arraycopy(data, 0, res, 0, size);
        return res;
    }

    public String getBasedFile() {
        return basedFile;
    }
}
