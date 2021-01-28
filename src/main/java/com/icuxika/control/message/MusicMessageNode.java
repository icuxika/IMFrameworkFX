package com.icuxika.control.message;

import com.icuxika.control.SelectableLabel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.*;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;

/**
 * 分享-音乐消息组件
 */
public class MusicMessageNode extends MessageNode {

    private MediaPlayer musicPlayer;
    private AudioSpectrumListener audioSpectrumListener;
    private XYChart.Data<String, Number>[] serialsData;
    private SelectableLabel musicTitleText;

    private boolean playing = false;

    /**
     * 目前默认设置为，登录用户不显示名称，单聊会话对方不显示名称，群聊显示其他人名称
     */
    public MusicMessageNode(boolean showLeft, boolean showName) {
        super(showLeft, showName);
    }

    @Override
    protected void initialize() {
        StackPane container = new StackPane();
        container.setBorder(new Border(new BorderStroke(Paint.valueOf("#eaeaea"), Paint.valueOf("#eaeaea"), Paint.valueOf("#eaeaea"), Paint.valueOf("#eaeaea"), BorderStrokeStyle.SOLID, null, null, null, null, new BorderWidths(2, 2, 2, 2), null)));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 50, 10);
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setPrefSize(240, 140);
        barChart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        serialsData = new XYChart.Data[128];
        String[] categories = new String[128];
        for (int i = 0; i < serialsData.length; i++) {
            categories[i] = Integer.toString(i + 1);
            serialsData[i] = new XYChart.Data<>(categories[i], 0);
            series.getData().add(serialsData[i]);
        }
        barChart.getData().add(series);

        barChart.setTitle(null);
        barChart.setLegendVisible(false);
        barChart.getXAxis().setLabel(null);
        barChart.getXAxis().setTickLabelGap(0);
        barChart.getXAxis().setTickLabelsVisible(false);
        barChart.getXAxis().setTickMarkVisible(false);
        barChart.getYAxis().setLabel(null);
        barChart.getYAxis().setTickLabelGap(0);
        barChart.getYAxis().setTickLabelsVisible(false);
        barChart.getYAxis().setTickMarkVisible(false);
        barChart.setVerticalGridLinesVisible(false);
        barChart.setVerticalZeroLineVisible(false);
        barChart.setHorizontalGridLinesVisible(false);
        barChart.setHorizontalZeroLineVisible(false);
        barChart.setAlternativeColumnFillVisible(false);
        barChart.setAlternativeRowFillVisible(false);

        HBox shadePane = new HBox();
        shadePane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        shadePane.setOpacity(0.5);

        BorderPane borderPane = new BorderPane();
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        musicTitleText = new SelectableLabel();
        musicTitleText.setAlignment(Pos.CENTER);
        musicTitleText.setMaxWidth(200);
        musicTitleText.setWrapText(true);
        header.getChildren().add(musicTitleText);

        borderPane.setTop(header);

        HBox body = new HBox();
        body.setAlignment(Pos.CENTER);
        body.setSpacing(8.0);

        FontIcon playOrPauseIcon = new FontIcon(FontAwesomeSolid.PLAY);
        playOrPauseIcon.setIconSize(18);

        FontIcon stopIcon = new FontIcon(FontAwesomeSolid.STOP);
        stopIcon.setIconSize(18);
        body.getChildren().addAll(playOrPauseIcon, stopIcon);

        borderPane.setCenter(body);

        container.getChildren().addAll(barChart, shadePane, borderPane);

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

        playOrPauseIcon.setOnMouseReleased(event -> {
            if (playing) {
                playing = false;
                musicPlayer.pause();
                playOrPauseIcon.setIconCode(FontAwesomeSolid.PLAY);
            } else {
                playing = true;
                musicPlayer.play();
                playOrPauseIcon.setIconCode(FontAwesomeSolid.PAUSE);
            }
        });

        stopIcon.setOnMouseReleased(event -> {
            if (playing) {
                playing = false;
                musicPlayer.seek(Duration.ZERO);
                musicPlayer.pause();
                playOrPauseIcon.setIconCode(FontAwesomeSolid.PLAY);
            } else {
                musicPlayer.seek(Duration.ZERO);
            }
        });
    }

    public void initMusic(URL url) {
        initMediaPlayer(url);
    }

    private void initMediaPlayer(URL url) {
        musicPlayer = new MediaPlayer(new Media(url.toString()));
        audioSpectrumListener = (timestamp, duration, magnitudes, phases) -> {
            for (int i = 0; i < serialsData.length; i++) {
                serialsData[i].setYValue(magnitudes[i] + 60);
            }
        };
        musicPlayer.setAudioSpectrumListener(audioSpectrumListener);
    }
}
