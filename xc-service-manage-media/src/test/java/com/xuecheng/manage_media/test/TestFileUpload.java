package com.xuecheng.manage_media.test;

import org.aspectj.apache.bcel.classfile.SourceFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.naming.ldap.SortKey;
import java.io.*;
import java.util.*;

/**
 * @program: XcEduCode
 * @description: 文件上传测试类
 * @author: WhyWhatHow
 * @create: 2020-04-14 20:54
 **/
//@SpringBootTest
//@RunWith(SpringRunner.class)
public class TestFileUpload {
    private static String fileAbsPath = "D:\\FFmpeg\\test\\lucene.mp4";
    long chunkSize = 1 << 20; // 分块文件大小 1M
    String chunkFileDir = "D:\\FFmpeg\\test\\chunk\\";

    @Test
    /**
     *测试文件分块
     * 1、获取源文件长度
     * 2、根据设定的分块文件的大小计算出块数
     * 3、从源文件读数据依次向每一个块文件写数据。
     */
    public void testChunk() throws IOException {
        File srcFile = new File(fileAbsPath);
        //1 . 获取文件大
        long srcFileLength = srcFile.length();
//        System.err.println(srcFileLength);
        // 2 获取分块数量
        long chunkNum = (long) Math.ceil(srcFileLength * 1.0 / chunkSize);
        chunkNum = chunkNum > 0 ? chunkNum : 1;
        // 3 .判断分块目录是否存在
        File chunkFolder = new File(chunkFileDir);
        if (!chunkFolder.exists()) {
            chunkFolder.mkdirs();// mkdir 创建一个目录, mkdirs 创建所有到这这个路径的目录
        }
        // 4 项文件中写入数据 IO
        byte[] buf = new byte[1024]; // 1kb
        //
        RandomAccessFile readRandomFile = new RandomAccessFile(srcFile, "r");

        for (int i = 0; i < chunkNum; i++) {
            File file = new File(chunkFileDir + i);
            try {
                boolean newFile = file.createNewFile();
                if (newFile) {
                    // 创建文件成功
                    RandomAccessFile writeFile = new RandomAccessFile(file, "rw");
                    int len = -1;
                    while ((len = readRandomFile.read(buf)) != -1) {
                        writeFile.write(buf, 0, len);
                    }
                    writeFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        readRandomFile.close();
    }

    /**
     * 合并文件
     * 1、找到要合并的文件并按文件合并的先后进行排序。
     * 2、创建合并文件
     * 3、依次从合并的文件中读取数据向合并文件写入数
     */
    @Test
    public void mergeFile() {
        // 1. 创建合并文件
        String mergeFileName = chunkFileDir + "a.mp4";
        File mergeFile = new File(mergeFileName);
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        //2 分块文件拍讯
        File chunkFileFolder = new File(chunkFileDir);
        if (chunkFileFolder.isDirectory()) {
            File[] files = chunkFileFolder.listFiles();
//            travelArray(files);
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    int first = Integer.parseInt(o1.getName());
                    int sen = Integer.parseInt(o2.getName());
                    if (first < sen) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
            byte[] b = new byte[1024];
            try {
                RandomAccessFile writeRandomFile = new RandomAccessFile(mergeFile, "rw");
                for (File file : files) {
                    RandomAccessFile readFile = new RandomAccessFile(file, "r");
                    int len = -1;
                    while ((len = readFile.read(b)) != -1) {
                        writeRandomFile.write(b, 0, len);
                    }readFile.close();
                }
                writeRandomFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

//             System.out.println("=====================");
//             travelArray(files);
        }
    }

    private void travelArray(File[] files) {
        for (File file : files) {
            System.out.print(file.getName() + " ");
        }
    }

    public static void main(String[] args) {
        Integer[] a = new Integer[]{5, 6, 7, 82, 1, 2, 3, 63, 0, 10};
//        Arrays.sort(a); // default asc
        Arrays.sort(a, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
//                return o1-o2;
                return o1 - o2;
//               return  o1-o2<0 ? 1 :-1 ;
            }
        });
//        Collections
        for (int i : a) {
            System.out.println(i + " ");
        }
//        Arrays.sort(a, Comparator.comparingInt(SortKey.esc));

    }
    @Test
    public void testProcessBuilder() throws IOException {

        String[] command = new String[]{"ipconfig"};
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        Process start = builder.start();
        OutputStream outputStream = start.getOutputStream();
//        String s1 = outputStream.toString(); // 查看输出流
        InputStream inputStream = start.getInputStream();
//        String s = inputStream.toString();
        StringBuffer stringBuffer =new StringBuffer();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"gbk");
        char[] buf = new char[1024];
        int len =-1;
        while((len = inputStreamReader.read(buf))!=-1){
//            String ss = new String();
            stringBuffer.append(buf);
        }
        System.out.println(stringBuffer.toString());
//        while(inputStreamReader.read(, , ))
        System.out.println("===================================");
        OutputStreamWriter writer =new OutputStreamWriter(outputStream,"gbk");
        len =-1;
        StringBuffer stringBuffer1 = new StringBuffer();

//        while ((len=writer.write(buf)!=-1))
    }

    @Test
    public void testFFMpeg(){
        ProcessBuilder processBuilder = new ProcessBuilder();
    //定义命令内容
        List<String> command = new ArrayList<>();
        command.add("D:\\FFmpeg\\ffmpeg-20180227-fa0c9d6-win64-static\\bin\\ffmpeg.exe");
        command.add("-i");
        command.add("D:\\FFmpeg\\test\\1.avi");
        command.add("-y");//覆盖输出文件
        command.add("-c:v");
        command.add("libx264");
        command.add("-s");
        command.add("1280x720");
        command.add("-pix_fmt");
        command.add("yuv420p");
        command.add("-b:a");
        command.add("63k");
        command.add("-b:v");
        command.add("753k");
        command.add("-r");
        command.add("20");
        command.add("D:\\FFmpeg\\test\\1.mp4");
        processBuilder.command(command);
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream,"gbk");
            StringBuffer stringBuffer =new StringBuffer();
            int len = -1 ;
            char[] buf = new char[1024];
            while((len = reader.read(buf))!=-1){
//            String ss = new String();
                stringBuffer.append(buf);
            }
            System.out.println(stringBuffer.toString());
//        while(inputStreamReader.read(, , ))
            System.out.println("===================================");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
