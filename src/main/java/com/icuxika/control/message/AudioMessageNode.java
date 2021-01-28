package com.icuxika.control.message;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

/**
 * 音频消息组件
 */
public class AudioMessageNode extends MessageNode {

    private Label durationLabel;

    private FontIcon playOrPauseIcon;

    private MediaPlayer mediaPlayer;

    private Duration duration;

    private Canvas canvas;

    private GraphicsContext gc;

    private AudioInputStream audioInputStream;

    private AudioFormat audioFormat;

    private byte[] audioBytes;

    private int[] audioData;

    private boolean playing = false;

    private static final int CANVAS_WIDTH = 200;
    private static final int CANVAS_HEIGHT = 50;

    /**
     * 目前默认设置为，登录用户不显示名称，单聊会话对方不显示名称，群聊显示其他人名称
     */
    public AudioMessageNode(boolean showLeft, boolean showName) {
        super(showLeft, showName);
    }

    @Override
    protected void initialize() {
        BorderPane borderPane = new BorderPane();
        borderPane.setBorder(new Border(new BorderStroke(Paint.valueOf("#eaeaea"), Paint.valueOf("#eaeaea"), Paint.valueOf("#eaeaea"), Paint.valueOf("#eaeaea"), BorderStrokeStyle.SOLID, null, null, null, null, new BorderWidths(2, 2, 2, 2), null)));

        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        gc = canvas.getGraphicsContext2D();

        StackPane stackPane = new StackPane(canvas);
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);

        durationLabel = new Label();
        playOrPauseIcon = new FontIcon(FontAwesomeSolid.PLAY);
        container.getChildren().addAll(durationLabel, playOrPauseIcon);

        stackPane.getChildren().add(container);
        borderPane.setTop(stackPane);

        if (showLeft) {
            AnchorPane.setLeftAnchor(borderPane, 60.0);
            if (showName) {
                AnchorPane.setTopAnchor(borderPane, 30.0);
            } else {
                AnchorPane.setTopAnchor(borderPane, 12.0);
            }
        } else {
            AnchorPane.setRightAnchor(borderPane, 60.0);
            AnchorPane.setTopAnchor(borderPane, 12.0);
        }

        getChildren().add(borderPane);

        playOrPauseIcon.setOnMouseReleased(event -> {
            MediaPlayer.Status currentStatus = mediaPlayer.getStatus();
            if (playing) {
                playing = false;
                mediaPlayer.pause();
                playOrPauseIcon.setIconCode(FontAwesomeSolid.PLAY);
            } else {
                playing = true;
                if (currentStatus == MediaPlayer.Status.READY || currentStatus == MediaPlayer.Status.PAUSED) {
                    mediaPlayer.play();
                } else {
                    mediaPlayer.pause();
                    mediaPlayer.seek(Duration.ZERO);
                }
                playOrPauseIcon.setIconCode(FontAwesomeSolid.PAUSE);
            }
        });
    }

    public void initAudio(URL url) {
        readAudio(url);
        drawWaveform(1, Paint.valueOf("#DBDBDB"));
        initMediaPlayer(url);
    }

    private void initMediaPlayer(URL url) {
        mediaPlayer = new MediaPlayer(new Media(url.toString()));

        mediaPlayer.setOnReady(() -> {
            // 获取音频时长
            duration = mediaPlayer.getMedia().getDuration();
            durationLabel.setText(String.format("%.1f\"", duration.toSeconds()));
        });

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            Duration currentTime = mediaPlayer.getCurrentTime();
            durationLabel.setText(String.format("%.1f\"", currentTime.toSeconds()));
            double end = currentTime.toMillis() / duration.toMillis();
            gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
            drawWaveform(1, Paint.valueOf("#DBDBDB"));
            drawWaveform(end, Paint.valueOf("#BBBBBB"));
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            playing = false;
            playOrPauseIcon.setIconCode(FontAwesomeSolid.PLAY);
        });
    }

    /**
     * 进行波形图绘制
     *
     * @param end   结束点
     * @param paint 颜色
     */
    private void drawWaveform(double end, Paint paint) {
        gc.setStroke(paint);
        int framePerPixel = audioBytes.length / audioFormat.getFrameSize() / CANVAS_WIDTH;
        byte indexByte;
        double oldY = 0;
        int channelNumber = audioFormat.getChannels();
        for (double i = 0; i < CANVAS_WIDTH * end; i++) {
            int index = (int) (framePerPixel * channelNumber * i);
            if (audioFormat.getSampleSizeInBits() == 8) {
                indexByte = (byte) audioData[index];
            } else {
                indexByte = (byte) (128 * audioData[index] / 32768);
            }
            double newY = (double) CANVAS_HEIGHT * (128 - indexByte) / 256;
            gc.strokeLine(i, oldY, i, newY);
            oldY = newY;
        }
    }

    /**
     * 读取音频数据
     *
     * @param url 音频地址
     */
    private void readAudio(URL url) {
        try {
            audioInputStream = AudioSystem.getAudioInputStream(url);
            audioFormat = audioInputStream.getFormat();
            audioBytes = new byte[(int) (audioInputStream.getFrameLength() * audioFormat.getFrameSize())];
            if (audioInputStream.read(audioBytes) == -1) throw new RuntimeException("音频数据异常");

            // 此处开始一堆逻辑未进行验证
            if (audioFormat.getSampleSizeInBits() == 16) {
                int nLengthInSamples = audioBytes.length / 2;
                audioData = new int[nLengthInSamples];
                if (audioFormat.isBigEndian()) {
                    for (int i = 0; i < nLengthInSamples; i++) {
                        int MSB = audioBytes[2 * i];
                        int LSB = audioBytes[2 * i + 1];
                        audioData[i] = MSB << 8 | (255 & LSB);
                    }
                } else {
                    for (int i = 0; i < nLengthInSamples; i++) {
                        int LSB = audioBytes[2 * i];
                        int MSB = audioBytes[2 * i + 1];
                        audioData[i] = MSB << 8 | (255 & LSB);
                    }
                }
            } else if (audioFormat.getSampleSizeInBits() == 8) {
                int nLengthInSamples = audioBytes.length;
                audioData = new int[nLengthInSamples];
                if (audioFormat.getEncoding().toString().startsWith("PCM_SIGN")) {
                    for (int i = 0; i < audioBytes.length; i++) {
                        audioData[i] = audioBytes[i];
                    }
                } else {
                    for (int i = 0; i < audioBytes.length; i++) {
                        audioData[i] = audioBytes[i] - 128;
                    }
                }
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }
}
