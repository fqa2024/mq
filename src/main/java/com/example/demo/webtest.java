package com.example.demo;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
public class webtest {

    @PostMapping(value = "/test")
    public void test() throws MalformedURLException {
        // 启用Headless模式（可选，用于无界面截图）
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        // 创建WebDriver实例
        WebDriver driver = new RemoteWebDriver(new URL("http://172.17.0.3:4444/wd/hub"), capabilities);
        try {
            List<String> urls = new ArrayList<>();
            urls.add("https://www.baidu.com/");
            urls.add("https://www.jianshu.com/");
            urls.forEach(url -> {
                driver.get(url);

                try {
                    Thread.sleep(10000); // 延迟5秒，仅供参考，请根据实际情况调整
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // 保存截图到文件
                BufferedImage fullImage;
                try {
                    fullImage = ImageIO.read(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // 保存全屏截图到本地，根据URL生成不同的文件名
                String fileName = url.replaceAll("[^a-zA-Z0-9]", "_") + ".png";
                File screenshotFile = new File(fileName);
                try {
                    ImageIO.write(fullImage, "PNG", screenshotFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                driver.navigate().refresh();
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭浏览器
            driver.quit();
        }
    }

//    @GetMapping(value = "/test-video")
    public void getVideo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://175.178.29.193:9000/")
                .credentials("admin", "an486918")
                .build();
        StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder().bucket("test").object("video.mp4").build());
        long size = statObjectResponse.size();
        Range range = Range.parseRangeHeader(request.getHeader("range"),size);
        //处理range 计算请求的哪部分数据
        long len = (range.getEnd() - range.getStart()) + 1;
        response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
        response.addHeader("Accept-Ranges", "bytes");
        response.setContentType("video/mp4");
//        response.addHeader("Content-Disposition", "inline; filename=" + "飞书20240320-171616.mp4");
        // 设置内容范围
        String contentRange = "bytes " + range.getStart() + "-" + range.getEnd() + "/" + len;
        response.addHeader("Content-Range", contentRange);
        response.addHeader("Cache-control", "private");
        response.setContentLength((int) len);
        // 返回部分内容，状态码为206
        try (InputStream object = minioClient.getObject(GetObjectArgs.builder().bucket("file-core-10010").object("2024-03-18/video.mp4").build())) {
            // 读取InputStream内容并转换为字节数组
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[8096];
            int length;
            while ((length = object.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } catch (Exception e) {
//            throw new Exception(e);
        }
    }

    @GetMapping(value = "/test-video")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://192.168.16.108:9000/")
                .credentials("admin", "1236547890")
                .build();
        try (InputStream videoStream = minioClient.getObject(GetObjectArgs.builder().bucket("file-core-10010").object("2024-03-18/video.mp4").build())) {

            StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder().bucket("file-core-10010").object("2024-03-18/video.mp4").build());
            long contentLength = statObjectResponse.size();

            response.setContentType("video/mp4");
            response.setHeader("Content-Length", String.valueOf(contentLength));
            // Handle range requests
            handleRangeRequest(request, response, videoStream,contentLength);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleRangeRequest(HttpServletRequest request, HttpServletResponse response, InputStream videoStream, long contentLength) throws IOException {
        String rangeHeader = request.getHeader("Range");
        if (rangeHeader != null) {
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            Range range = Range.parseRangeHeader(rangeHeader, contentLength);
            response.setHeader("Content-Range", "bytes " + range.getStart() + "-" +
                    range.getEnd() + "/" + contentLength);
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Length", String.valueOf(range.getEnd() - range.getStart() + 1));
            byte[] buffer = new byte[8192];
            videoStream.skip(range.getStart());
            int bytesRead;
            while ((bytesRead = videoStream.read(buffer)) != -1) {
                System.out.println("111");
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = videoStream.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
        }
        videoStream.close();
    }

    public static int reverseBits(String n) {
        char[] charArray = String.valueOf(n).toCharArray();
        for (int i = 0; i < charArray.length / 2; i++) {
            char temp = charArray[i];
            charArray[i] = charArray[charArray.length - i - 1];
            charArray[charArray.length - i - 1] = temp;
        }
        return Integer.valueOf(new String(charArray));
    }

//    public static void main(String[] args) {
//        System.out.println(9|1);
//    }
//    public static void main(String[] args) {
//        WebDriverManager.chromedriver().setup();
//
//        // 启用Headless模式（可选，用于无界面截图）
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless", "--disable-gpu");
//        options.addArguments("--window-size=1920,1080");
//
//        // 创建WebDriver实例
//        WebDriver driver = new ChromeDriver(options);
//        try {
//            List<String> urls = new ArrayList<>();
//            urls.add("https://www.baidu.com/");
//            urls.add("https://www.jianshu.com/");
//            urls.forEach(url -> {
//                driver.get(url);
//
//                try {
//                    Thread.sleep(3000); // 延迟5秒，仅供参考，请根据实际情况调整
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//
//                // 保存截图到文件
//                BufferedImage fullImage;
//                try {
//                    fullImage = ImageIO.read(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE));
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//
//                // 保存全屏截图到本地，根据URL生成不同的文件名
//                String fileName = url.replaceAll("[^a-zA-Z0-9]", "_") + ".png";
//                File screenshotFile = new File(fileName);
//                try {
//                    ImageIO.write(fullImage, "PNG", screenshotFile);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//
//                driver.navigate().refresh();
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            // 关闭浏览器
//            driver.quit();
//        }
//    }
}
