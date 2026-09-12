package com.niu.core.listener.sys;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.freewayso.image.combiner.ImageCombiner;
import com.freewayso.image.combiner.element.ImageElement;
import com.freewayso.image.combiner.element.RectangleElement;
import com.freewayso.image.combiner.element.TextElement;
import com.freewayso.image.combiner.enums.LineAlign;
import com.freewayso.image.combiner.enums.ZoomMode;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.common.utils.image.ImageUtils;
import com.niu.core.event.sys.PosterDrawEvent;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

@EventListen("core")
@Component
public class PosterDrawListener extends AbstractListener {

    public void handleEvent(PosterDrawEvent event) {
        JSONObject poster = event.getPoster();
        ImageCombiner combiner = event.getCombiner();
        Map<String, Object> posterData = event.getPosterData();
        String type = poster.getStr("type", "").toLowerCase();

        switch (type) {
            case "image":
                String imgPath = poster.getStr("value", "");
                if (ObjectUtil.isEmpty(imgPath) && ObjectUtil.isNotEmpty(poster.getStr("relate"))) {
                    Object relateValue = posterData.get(poster.getStr("relate"));
                    imgPath = relateValue != null ? String.valueOf(relateValue) : "";
                }
                BufferedImage img = ImageUtils.readImage(imgPath, "static/resource/images/diy/crack_figure.png");

                // 对应PHP: is_file()检查，只有当文件存在时才处理
                if (img != null) {
                    int targetWidth = poster.getInt("width");
                    int targetHeight = poster.getInt("height");
                    String shape = poster.getStr("shape", "");
                    // 对齐 PHP imagecopyresized：先拉伸到目标尺寸，circle 再裁圆；绘制时不再二次缩放
                    BufferedImage posterImage = ImageUtils.preparePosterImage(img, targetWidth, targetHeight, shape);
                    ImageElement imgElement = new ImageElement(
                            posterImage,
                            poster.getInt("x"),
                            poster.getInt("y")
                    );
                    combiner.addElement(imgElement);
                }
                // 如果img为null（文件不存在），则跳过该元素，与PHP逻辑一致
                break;
            case "text":
                String testStr = poster.getStr("value", "");
                if (ObjectUtil.isEmpty(testStr) && ObjectUtil.isNotEmpty(poster.getStr("relate"))) {
                    Object relateValue = posterData.get(poster.getStr("relate"));
                    testStr = relateValue != null ? String.valueOf(relateValue) : "";
                }

                int fontStyle = poster.getBool("weight") ? Font.BOLD : Font.PLAIN;

                Font customFont = null;
                String defaultFont = "static/font/SourceHanSansCN-Regular.ttf";
                File fontFile = new File(WebAppEnvs.get().webRootDownResource, ObjectUtil.defaultIfEmpty(poster.getStr("fontFamily", defaultFont), defaultFont));

                try {
                    customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                    customFont = customFont.deriveFont(fontStyle, poster.getInt("fontSize"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                boolean centerX = "center".equals(poster.getStr("x"));
                int textX = centerX ? 0 : poster.getInt("x", 0);
                TextElement text = new TextElement(testStr, fontStyle, poster.getInt("fontSize"), textX, poster.getInt("y", 0));
                if (centerX) {
                    text.setCenter(true);
                }
                String fontColor = poster.getStr("fontColor", "#303133");
                if (ObjectUtil.isNotEmpty(fontColor)) {
                    text.setColor(Color.decode(fontColor));
                }
                int fontSize = poster.getInt("fontSize", 28);
                int lineCount = fontSize > 0 ? poster.getInt("height", fontSize) / fontSize : 1;
                text.setAutoBreakLine(poster.getInt("width"), lineCount, LineAlign.Left);
                if (customFont != null) text.setFont(customFont);
                combiner.addElement(text);
                break;
            case "qrcode":
                // 对应PHP: qrcode类型在CorePosterService中被转换为image类型处理
                String qrcodePath = (String) posterData.getOrDefault(poster.getStr("relate"), "static/resource/images/diy/qrcode.png");
                BufferedImage qrcode = ImageUtils.readImage(qrcodePath, "static/resource/images/diy/qrcode.png");

                // 对应PHP: is_file()检查，只有当文件存在时才处理
                if (qrcode != null) {
                    ImageElement qrcodeElement = new ImageElement(qrcode, poster.getInt("x"), poster.getInt("y"), poster.getInt("width"), poster.getInt("height"), ZoomMode.Width);
                    combiner.addElement(qrcodeElement);
                }
                // 如果qrcode为null（文件不存在），则跳过该元素，与PHP逻辑一致
                break;
            case "draw":
                RectangleElement rectangle = new RectangleElement(poster.getInt("x"), poster.getInt("y"), poster.getInt("width"), poster.getInt("height"));
                rectangle.setColor(Color.decode(poster.getStr("bgColor")));
                combiner.addElement(rectangle);
                break;
        }
    }
}
