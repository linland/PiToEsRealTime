package com.link;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;

/**
 * 监听PI数据库，将PI数据库中数据抽取到Kafka中。
 * 启动项目时需将JDK版本切换至32bit，否则无法正常调用PIAPI。
 * @author liugh
 *
 */
@SpringBootApplication
public class PiToEsApplication {

	public static void main(String[] args) {
		//加载动态链接库，注意和SpringBoot的启动
        String systemType = System.getProperty("os.name");
        if (systemType.toLowerCase().indexOf("win") != -1)
            loadNative("piapi32");
        else
            loadNative("libpiapi");
		SpringApplication.run(PiToEsApplication.class, args);
	}
	
	private synchronized static void loadNative(String nativeName) {

        String systemType = System.getProperty("os.name");
        String fileExt = (systemType.toLowerCase().indexOf("win") != -1) ? ".dll" : ".so";
        File path = new File(".");
        //将所有动态链接库dll/so文件都放在一个临时文件夹下，然后进行加载
        //这是应为项目为可执行jar文件的时候不能很方便的扫描里面文件
        //此目录放置在与项目同目录下的natives文件夹下
        String sysUserTempDir = path.getAbsoluteFile().getParent() + File.separator + "natives";
        String fileName = nativeName + fileExt;
        InputStream in = null;
        BufferedInputStream reader = null;
        FileOutputStream writer = null;

        File tempFile = new File(sysUserTempDir + File.separator + fileName);
        if(!tempFile.getParentFile().exists())
            tempFile.getParentFile().mkdirs();
        if (tempFile.exists()) {
            tempFile.delete();
        }
        try {
            //读取文件形成输入流
            in = PiToEsApplication.class.getResourceAsStream("/native/" + fileName);
            if (in == null)
                in = PiToEsApplication.class.getResourceAsStream("native/" + fileName);
          PiToEsApplication.class.getResource(fileName);
            reader = new BufferedInputStream(in);
            writer = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];

            while (reader.read(buffer) > 0) {
                writer.write(buffer);
                buffer = new byte[1024];
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.load(tempFile.getPath());
        System.out.println("------>> 加载native文件 :" + tempFile + "成功!!");
    }

}
