package com.niu.core.common.utils.image;

import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.StringUtils;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 */
@Log4j2
public class ImageUtils {

    /**
     * 图片转base64
     * @throws IOException
     */
    public static String imageToBase64(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) return "";

            byte[] imageBytes = Files.readAllBytes(Paths.get(path));
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            return "data:image/"+ getImageExt(path) +";base64," + base64Image;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取图片后缀
     * @param fileName
     * @return
     */
    public static String getImageExt(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf('.');

        if (lastIndexOfDot > 0 && lastIndexOfDot < fileName.length() - 1) {
            return fileName.substring(lastIndexOfDot + 1);
        } else {
            return "";
        }
    }

    /**
     * 读取图片
     * @param imgPath 图片路径或URL
     * @return BufferedImage 读取到的图片，如果失败返回null
     * @throws Exception 读取失败时抛出异常
     */
    public static BufferedImage readImage(String imgPath) throws Exception {
        if (StringUtils.isEmpty(imgPath)) {
            log.warn("图片路径为空，无法读取");
            return null;
        }
        
        try {
            if (CommonUtils.isUrl(imgPath)) {
                // 处理URL图片
                URL url = new URL(imgPath);
                BufferedImage image = ImageIO.read(url);
                if (image == null) {
                    log.warn("无法读取URL图片: {}", imgPath);
                    throw new IOException("无法从URL读取图片: " + imgPath);
                }
                return image;
            } else {
                // 处理本地图片
                File imageFile = new File(WebAppEnvs.get().webRootDownResource, imgPath);
                if (!imageFile.exists()) {
                    log.warn("图片文件不存在: {}", imageFile.getAbsolutePath());
                    throw new IOException("图片文件不存在: " + imageFile.getAbsolutePath());
                }
                if (!imageFile.isFile()) {
                    log.warn("路径不是文件: {}", imageFile.getAbsolutePath());
                    throw new IOException("路径不是文件: " + imageFile.getAbsolutePath());
                }
                BufferedImage image = ImageIO.read(imageFile);
                if (image == null) {
                    log.warn("无法读取图片文件: {}", imageFile.getAbsolutePath());
                    throw new IOException("无法读取图片文件: " + imageFile.getAbsolutePath());
                }
                return image;
            }
        } catch (IOException e) {
            log.error("读取图片失败: {}, 错误信息: {}", imgPath, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("读取图片时发生未知错误: {}, 错误信息: {}", imgPath, e.getMessage(), e);
            throw new Exception("读取图片失败: " + imgPath + ", " + e.getMessage(), e);
        }
    }

    /**
     * 读取图片，如果失败则尝试读取默认图片
     * @param imgPath 图片路径或URL
     * @param defaultImg 默认图片路径
     * @return BufferedImage 读取到的图片，如果都失败返回null
     */
    public static BufferedImage readImage(String imgPath, String defaultImg) {
        try {
            // 首先尝试读取指定图片
            if (!StringUtils.isEmpty(imgPath)) {
                BufferedImage image = readImage(imgPath);
                if (image != null) {
                    return image;
                }
            }
        } catch (Exception e) {
            log.warn("读取图片失败，尝试使用默认图片。原图片路径: {}, 错误: {}", imgPath, e.getMessage());
        }
        
        // 如果指定图片读取失败，尝试读取默认图片
        try {
            if (!StringUtils.isEmpty(defaultImg)) {
                BufferedImage defaultImage = readImage(defaultImg);
                if (defaultImage != null) {
                    log.info("使用默认图片: {}", defaultImg);
                    return defaultImage;
                }
            }
        } catch (Exception e) {
            log.error("读取默认图片也失败。默认图片路径: {}, 错误: {}", defaultImg, e.getMessage());
        }
        
        log.error("无法读取图片，指定图片路径: {}, 默认图片路径: {}", imgPath, defaultImg);
        return null;
    }

    /**
     * 获取图片尺寸
     * @param imagePath 图片路径或URL
     * @return 包含宽度和高度的Map
     */
    public static ImageSize getImageSize(String imagePath) {
        ImageSize result = new ImageSize();
        Map<String, Integer> size = new HashMap<>();

        if (StringUtils.isEmpty(imagePath)) {
            return result;
        }

        try {
            BufferedImage bufferedImage = null;

            if (CommonUtils.isUrl(imagePath)) {
                // 处理网络图片
                URL url = new URL(imagePath);
                bufferedImage = ImageIO.read(url);
            } else {
                // 处理本地图片
                File file = new File(WebAppEnvs.get().webRootDownResource, imagePath);
                if (file.exists() && file.isFile()) {
                    bufferedImage = ImageIO.read(file);
                }
            }

            if (bufferedImage != null) {
                result.setWidth(bufferedImage.getWidth());
                result.setHeight(bufferedImage.getHeight());
            }
        } catch (IOException e) {
            log.error("获取图片尺寸失败:{}", e.getMessage(), e);
        }

        return result;
    }


    /**
     * 获取第一张图片的尺寸（处理逗号分隔的图片字符串）
     * @param images 逗号分隔的图片字符串
     * @return 包含宽度和高度的Map
     */
    public static ImageSize getFirstImageSize(String images) {
        ImageSize imageSize = new ImageSize();
        if (StringUtils.isEmpty(images)) {
            return imageSize;
        }

        String[] imageArray = images.split(",");
        if (imageArray.length == 0) {
            return imageSize;
        }

        String firstImage = imageArray[0].trim();
        return getImageSize(firstImage);
    }

    @Data
    public static class ImageSize {
        private Integer width = 0;
        private Integer height = 0;
    }
}
