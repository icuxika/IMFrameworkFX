package com.icuxika.control;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * From Ensemble8.jar
 */
public class VideoPlayerPane extends StackPane {

    private BorderPane borderPane;

    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private final boolean repeat = false;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Duration duration;
    private Slider timeSlider;
    private Label playTime;
    private Slider volumeSlider;
    private HBox mediaTopBar;
    private HBox mediaBottomBar;
    private ParallelTransition transition = null;

    public VideoPlayerPane(final MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;

        mediaView = new MediaView(mediaPlayer);

        mediaTopBar = new HBox();
        mediaTopBar.setPrefHeight(40);
        mediaTopBar.setPadding(new Insets(5, 10, 5, 10));
        mediaTopBar.setAlignment(Pos.CENTER);
        mediaTopBar.setOpacity(1.0);

        setOnMouseEntered((MouseEvent t) -> {
            if (transition != null) {
                transition.stop();
            }
            FadeTransition fade1 = new FadeTransition(Duration.millis(200), mediaTopBar);
            fade1.setToValue(1.0);
            fade1.setInterpolator(Interpolator.EASE_OUT);

            FadeTransition fade2 = new FadeTransition(Duration.millis(200), mediaBottomBar);
            fade2.setToValue(1.0);
            fade2.setInterpolator(Interpolator.EASE_OUT);

            transition = new ParallelTransition(fade1, fade2);
            transition.play();
        });
        setOnMouseExited((MouseEvent t) -> {
            if (transition != null) {
                transition.stop();
            }
            FadeTransition fadeTop = new FadeTransition(Duration.millis(800), mediaTopBar);
            fadeTop.setToValue(0.0);
            fadeTop.setInterpolator(Interpolator.EASE_OUT);

            FadeTransition fadeBottom = new FadeTransition(Duration.millis(800), mediaBottomBar);
            fadeBottom.setToValue(0.0);
            fadeBottom.setInterpolator(Interpolator.EASE_OUT);
            transition = new ParallelTransition(fadeTop, fadeBottom);
            transition.play();
        });

        ReadOnlyObjectProperty<Duration> time = mediaPlayer.currentTimeProperty();
        time.addListener((ObservableValue<? extends Duration> observable,
                          Duration oldValue, Duration newValue) -> {
            updateValues();
        });
        mediaPlayer.setOnPlaying(() -> {
            if (stopRequested) {
                mediaPlayer.pause();
                stopRequested = false;
            }
        });
        mediaPlayer.setOnReady(() -> {
            duration = mediaPlayer.getMedia().getDuration();
            updateValues();
        });
        mediaPlayer.setOnEndOfMedia(() -> {
            if (!repeat) {
                stopRequested = true;
                atEndOfMedia = true;
            }
        });
        mediaPlayer.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);

        // Time label
        Label timeLabel = new Label("时间");
        timeLabel.setMinWidth(Control.USE_PREF_SIZE);
        timeLabel.setTextFill(Color.WHITE);

        mediaTopBar.getChildren().add(timeLabel);

        // Time slider
        timeSlider = new Slider();
        timeSlider.setMinWidth(200);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        timeSlider.valueProperty().addListener((ObservableValue<? extends Number> observable,
                                                Number old, Number now) -> {
            if (timeSlider.isValueChanging()) {
                if (duration != null) {
                    mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
                }
                updateValues();
            } else if (Math.abs(now.doubleValue() - old.doubleValue()) > 1.5) {
                if (duration != null) {
                    mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
                }
            }
        });
        mediaTopBar.getChildren().add(timeSlider);

        playTime = new Label();
        playTime.setPrefWidth(75);
        playTime.setMinWidth(75);
        playTime.setTextFill(Color.WHITE);
        mediaTopBar.getChildren().add(playTime);

        Label volumeLabel = new Label("音量");
        volumeLabel.setMinWidth(Control.USE_PREF_SIZE);
        volumeLabel.setTextFill(Color.WHITE);

        mediaTopBar.getChildren().add(volumeLabel);

        volumeSlider = new Slider();
        volumeSlider.setPrefWidth(120);
        volumeSlider.setMinWidth(30);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.valueProperty().addListener((Observable ov) -> {
        });
        volumeSlider.valueProperty().addListener((ObservableValue<? extends Number> observable,
                                                  Number old, Number now) -> {
            mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
        });
        HBox.setHgrow(volumeSlider, Priority.ALWAYS);
        mediaTopBar.getChildren().add(volumeSlider);

        final EventHandler<ActionEvent> backAction = (ActionEvent e) -> {
            mediaPlayer.seek(Duration.ZERO);
        };
        final EventHandler<ActionEvent> stopAction = (ActionEvent e) -> {
            mediaPlayer.stop();
        };
        final EventHandler<ActionEvent> playAction = (ActionEvent e) -> {
            mediaPlayer.play();
        };
        final EventHandler<ActionEvent> pauseAction = (ActionEvent e) -> {
            mediaPlayer.pause();
        };
        final EventHandler<ActionEvent> forwardAction = (ActionEvent e) -> {
            Duration currentTime = mediaPlayer.getCurrentTime();
            mediaPlayer.seek(Duration.seconds(currentTime.toSeconds() + 5.0));
        };

        mediaBottomBar = new HBox();
        mediaBottomBar.setPrefHeight(40);
        mediaBottomBar.setSpacing(0);
        mediaBottomBar.setAlignment(Pos.CENTER);

        Button backButton = new Button("快退");
        backButton.setOnAction(backAction);

        Button stopButton = new Button("停止");
        stopButton.setOnAction(stopAction);

        Button playButton = new Button("播放");
        playButton.setOnAction(playAction);

        Button pauseButton = new Button("暂停");
        pauseButton.setOnAction(pauseAction);

        Button forwardButton = new Button("快进");
        forwardButton.setOnAction(forwardAction);

        mediaBottomBar.getChildren().addAll(backButton, stopButton, playButton, pauseButton, forwardButton);

        borderPane = new BorderPane();
        borderPane.setTop(mediaTopBar);
        borderPane.setBottom(mediaBottomBar);

        getChildren().addAll(mediaView, borderPane);
    }

    protected void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null && duration != null) {
            Platform.runLater(() -> {
                Duration currentTime = mediaPlayer.getCurrentTime();
                playTime.setText(formatTime(currentTime, duration));
                timeSlider.setDisable(duration.isUnknown());
                if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging()) {
                    timeSlider.setValue(currentTime.divide(duration).toMillis() * 100.0);
                }
                if (!volumeSlider.isValueChanging()) {
                    volumeSlider.setValue((int) Math.round(mediaPlayer.getVolume() * 100));
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;

            if (durationHours > 0) {
                return String.format("%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d",
                        elapsedMinutes, elapsedSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d",
                        elapsedMinutes, elapsedSeconds);
            }
        }
    }
}
