package com.icuxika.control.message;

import com.icuxika.control.VideoPlayerPane;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;

/**
 * 视频消息组件
 */
public class VideoMessageNode extends MessageNode {

    StackPane container = new StackPane();
    private MediaPlayer videoPlayer;
    private MediaView mediaView;

    private boolean playing = false;

    /**
     * 目前默认设置为，登录用户不显示名称，单聊会话对方不显示名称，群聊显示其他人名称
     */
    public VideoMessageNode(boolean showLeft, boolean showName) {
        super(showLeft, showName);
    }

    @Override
    public void initialize() {
        container = new StackPane();
        container.setBorder(new Border(new BorderStroke(Paint.valueOf("#eaeaea"), Paint.valueOf("#eaeaea"), Paint.valueOf("#eaeaea"), Paint.valueOf("#eaeaea"), BorderStrokeStyle.SOLID, null, null, null, null, new BorderWidths(2, 2, 2, 2), null)));

        mediaView = new MediaView();
        mediaView.setFitWidth(240);
        mediaView.setFitHeight(135);
        container.getChildren().add(mediaView);

        HBox operateBox = new HBox();
        operateBox.setAlignment(Pos.CENTER);
        operateBox.setSpacing(8.0);

        FontIcon playOrPauseIcon = new FontIcon(FontAwesomeSolid.PLAY);
        playOrPauseIcon.setIconSize(18);

        FontIcon stopIcon = new FontIcon(FontAwesomeSolid.STOP);
        stopIcon.setIconSize(18);

        FontIcon externalIcon = new FontIcon(FontAwesomeSolid.EXTERNAL_LINK_ALT);
        externalIcon.setIconSize(18);
        operateBox.getChildren().addAll(playOrPauseIcon, stopIcon, externalIcon);

        container.getChildren().add(operateBox);

        if (showLeft) {
            AnchorPane.setLeftAnchor(container, 60.0);
            if (showName) {
                AnchorPane.setTopAnchor(container, 30.0);
            } else {
                AnchorPane.setTopAnchor(container, 12.0);
            }
        } else {
            AnchorPane.setRightAnchor(container, 60.0);
            AnchorPane.setTopAnchor(container, 12.0);
        }

        getChildren().add(container);

        container.setOnMouseEntered(event -> operateBox.setVisible(true));
        container.setOnMouseExited(event -> operateBox.setVisible(!playing));
        playOrPauseIcon.setOnMouseReleased(event -> {
            if (playing) {
                playing = false;
                videoPlayer.pause();
                playOrPauseIcon.setIconCode(FontAwesomeSolid.PLAY);
            } else {
                playing = true;
                videoPlayer.play();
                playOrPauseIcon.setIconCode(FontAwesomeSolid.PAUSE);
            }
        });

        stopIcon.setOnMouseReleased(event -> {
            if (playing) {
                playing = false;
                videoPlayer.seek(Duration.ZERO);
                videoPlayer.pause();
                playOrPauseIcon.setIconCode(FontAwesomeSolid.PLAY);
            } else {
                videoPlayer.seek(Duration.ZERO);
            }
        });

        externalIcon.setOnMouseReleased(event -> {
            // 暂停当前播放
            if (playing) {
                playing = false;
                videoPlayer.pause();
                playOrPauseIcon.setIconCode(FontAwesomeSolid.PLAY);
            }

            // 打开 Ensemble8.jar 中的 Overlay Media Player来单独打开一个窗口播放视频
            MediaPlayer externalVideoPlayer = new MediaPlayer(new Media(videoPlayer.getMedia().getSource()));
            final double mediaWidth = 450.0;
            final double mediaHeight = 300.0;
            VideoPlayerPane videoPlayerPane = new VideoPlayerPane(externalVideoPlayer);
            Stage stage = new Stage();
            stage.setScene(new Scene(videoPlayerPane, mediaWidth, mediaHeight));
            externalVideoPlayer.play();
            stage.show();
            stage.setOnCloseRequest(event1 -> externalVideoPlayer.stop());
        });
    }

    public void initVideo(URL url) {
        initMediaPlayer(url);
    }

    private void initMediaPlayer(URL url) {
        videoPlayer = new MediaPlayer(new Media(url.toString()));
        if (videoPlayer.getError() == null) {
            mediaView.setMediaPlayer(videoPlayer);
            // FIXME 此处对 container 添加子元素，界面不更新
        }
    }
}
