package com.ping.student.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadPic {
    // 上传文件的缓冲区大小
    private static final int BUFFER_SIZE = 16 * 1024;
    //  BufferedInputStream继承于FilterInputStream，提供缓冲输入流功能。缓冲输入流相对于普通输入流的优势是，它提供了一个缓冲数组，每次调用read方法的时候，它首先尝试从缓冲区里读取数据，若读取失败（缓冲区无可读数据），则选择从物理数据源（譬如文件）读取新数据（这里会尝试尽可能读取多的字节）放入到缓冲区中，最后再将缓冲区中的内容部分或全部返回给用户.
    //  由于从缓冲区里读取数据远比直接从物理数据源（譬如文件）读取速度快。
    public static void copy(InputStream ins, File dstFile) {
        InputStream in = null;
        OutputStream out = null;
        try {
            // 建立缓存输入流 BufferedInputStream, 缓冲区大小为16kb。
            // 缓存流必须建立在一个存在的流的基础上
            in = new BufferedInputStream(ins, BUFFER_SIZE);
            // 建立缓存输入流 BufferedInputStream, 缓冲区大小为16kb。
            out = new BufferedOutputStream(new FileOutputStream(dstFile), BUFFER_SIZE);
            // 建立一个缓冲数组。
            byte[] buffer = new byte[BUFFER_SIZE];
            //每次读取的光标。
            int len = 0;
            //若读取到文件结尾，光标值为-1。
            while ((len = in.read(buffer)) > 0) {
                //写入到指定路径下。
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获得文件在服务器上的唯一路径。
     * @param fileName
     * @param basePath
     * @return
     */
    public static String getRealFilePath(String fileName, String basePath) {
        long uuid = System.currentTimeMillis();
        String fileType = getExtension(fileName);   //eg.jsp
        String fileName_UUID = uuid + "." + fileType;// 得到以唯一标识符重命名的文件名

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + File.separator
                + "MM" + File.separator + "dd" + File.separator);

        String rq = sdf.format(date);
        String a = File.separator;
        int j = basePath.lastIndexOf(File.separator);

        // 如果基路径不以"\\"结尾,则添加上
        if ((j > -1) && (j != basePath.length() - 1)) {
            basePath = basePath + File.separator;
        }

        mkDirectory(basePath + rq); // 根据路径创建一系列的目录

        String realFilePath = rq + fileName_UUID; // 得到在服务器上存储的唯一路径

        return realFilePath;
    }

    /**
     * 根据文件名得到文件的后缀名
     * @param filename
     * @return
     */
    public static String getExtension(String filename) {
        int i = filename.lastIndexOf(".");
        if ((i > -1) && (i < (filename.length() - 1))) {
            return filename.substring(i + 1);
        } else {
            return "data";
        }
    }

    /**
     * 根据路径创建一系列的目录
     *
     * @param path
     */
    public static boolean mkDirectory(String path) {
        File file = null;
        try {
            file = new File(path);
            if (!file.exists()) {
                return file.mkdirs();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            file = null;
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param filePathAndName
     *            String 文件路径及名称 如c:/fqf.txt
     *            String
     * @return boolean
     */
    public static boolean delFile(String filePathAndName) {
        File myDelFile = new java.io.File(filePathAndName);
        if (!myDelFile.exists()) {
            return true;
        }
        return myDelFile.delete();
    }

    /**
     * 删除指定文件路径下面的所有文件和文件夹
     *
     * @param file
     */
    public static boolean delFiles(File file) {
        boolean flag = false;
        try {
            if (file.exists()) {
                if (file.isDirectory()) {
                    String[] contents = file.list();
                    for (int i = 0; i < contents.length; i++) {
                        File file2X = new File(file.getAbsolutePath() + "/"
                                + contents[i]);
                        if (file2X.exists()) {
                            if (file2X.isFile()) {
                                flag = file2X.delete();
                            } else if (file2X.isDirectory()) {
                                delFiles(file2X);
                            }
                        } else {
                            throw new RuntimeException("File not exist!");
                        }
                    }
                }
                flag = file.delete();
            } else {
                throw new RuntimeException("File not exist!");
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

}
