package com.icuxika.framework;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成
 */
public class QRCodeGenerator {

    /**
     * 默认二维码宽度
     */
    private static final int WIDTH = 200;

    /**
     * 默认二维码高度
     */
    private static final int HEIGHT = 200;

    /**
     * 默认二维码文件格式
     */
    private static final String format = "png";

    /**
     * 二维码生成参数
     */
    private static final Map<EncodeHintType, Object> hints = new HashMap<>();

    static {
        // 字符编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // 容错等级，从高到低为 H、Q、M、L
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 二维码边界空白大小 1、2、3、4 默认为4
        hints.put(EncodeHintType.MARGIN, 1);
    }

    public static BufferedImage toBufferedImage(String content, int width, int height) throws WriterException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    public static BufferedImage toBufferedImage(String content) throws WriterException {
        return toBufferedImage(content, WIDTH, HEIGHT);
    }

    public static Image toImage(String content, int width, int height) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            writeToStream(content, stream, width, height);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        return new Image(new ByteArrayInputStream(stream.toByteArray()));
    }

    public static Image toImage(String content) {
        return toImage(content, WIDTH, HEIGHT);
    }

    /**
     * 二维码绘制logo
     *
     * @param image     二维码图片
     * @param logoImage logo图片
     * @param logoSize  logo大小
     * @return 图片
     */
    public static Image encodeQRCodeLogo(Image image, Image logoImage, int logoSize) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        // 读取二维码图片，获取画笔
        Graphics2D graphics2D = bufferedImage.createGraphics();
        // 读取logo图片
        BufferedImage logo = SwingFXUtils.fromFXImage(logoImage, null);
        // 设置logo尺寸
        int logoWidth = Math.min(logo.getWidth(null), bufferedImage.getWidth() * logoSize / 100);
        int logoHeight = Math.min(logo.getHeight(null), bufferedImage.getHeight() * logoSize / 100);
        // 设置logo坐标
        int x = (bufferedImage.getWidth() - logoWidth) / 2;
        int y = (bufferedImage.getHeight() - logoHeight) / 2;
        // 开始合并绘制图片
        graphics2D.drawImage(logo, x, y, logoWidth, logoHeight, null);
        graphics2D.drawRoundRect(x, y, logoWidth, logoHeight, 15, 15);
        // logo 边框大小
        graphics2D.setStroke(new BasicStroke(2));
        // logo边框颜色
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawRect(x, y, logoWidth, logoHeight);
        graphics2D.dispose();
        logo.flush();
        bufferedImage.flush();
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    /**
     * 将二维码图片输出到一个流中
     *
     * @param content 二维码内容
     * @param stream  输出流
     * @param width   宽
     * @param height  高
     */
    public static void writeToStream(String content, OutputStream stream, int width, int height) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        MatrixToImageWriter.writeToStream(bitMatrix, format, stream);
    }
}
