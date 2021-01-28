package com.icuxika.control.message;

import com.icuxika.util.FormatUtil;
import com.jfoenix.control.JFXProgressBar;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;

/**
 * 文件消息
 * 三种状态：
 * 一、未下载
 * 二、下载中
 * 三、已下载
 */
public class FileMessageNode extends MessageNode {

    private FontIcon fileTypeIcon;
    private Label fileNameLabel;
    private Label fileSizeLabel;

    private Hyperlink downloadLink;
    private Hyperlink openFileLink;
    private Hyperlink openFolderLink;

    private JFXProgressBar progressBar;

    /**
     * 目前默认设置为，登录用户不显示名称，单聊会话对方不显示名称，群聊显示其他人名称
     */
    public FileMessageNode(boolean showLeft, boolean showName) {
        super(showLeft, showName);
    }

    @Override
    protected void initialize() {
        BorderPane container = new BorderPane();
        container.setBorder(new Border(new BorderStroke(
                Color.rgb(181, 181, 181),
                Color.rgb(181, 181, 181),
                Color.rgb(181, 181, 181),
                Color.rgb(181, 181, 181),
                BorderStrokeStyle.SOLID,
                BorderStrokeStyle.SOLID,
                BorderStrokeStyle.SOLID,
                BorderStrokeStyle.SOLID,
                new CornerRadii(4),
                new BorderWidths(1.0, 1.0, 1.0, 1.0),
                Insets.EMPTY
        )));

        HBox header = new HBox();
        header.setPrefHeight(60.0);
        header.setBorder(new Border(new BorderStroke(
                null,
                null,
                Color.rgb(181, 181, 181),
                null,
                BorderStrokeStyle.SOLID,
                null,
                null,
                null,
                null,
                new BorderWidths(0.0, 0.0, 1.0, 0.0),
                Insets.EMPTY
        )));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);

        fileTypeIcon = new FontIcon(FontAwesomeSolid.FILE);
        fileTypeIcon.setIconSize(32);
        HBox.setMargin(fileTypeIcon, new Insets(0, 0, 0, 10));

        VBox fileInfoContainer = new VBox();
        fileInfoContainer.setAlignment(Pos.CENTER_LEFT);
        fileInfoContainer.setSpacing(4);
        fileNameLabel = new Label();
        fileNameLabel.setFont(Font.font(14));
        fileNameLabel.setMaxWidth(160);
        fileSizeLabel = new Label();
        fileSizeLabel.setTextFill(Color.rgb(181, 181, 181));
        fileSizeLabel.setMaxWidth(160);
        fileInfoContainer.getChildren().addAll(fileNameLabel, fileSizeLabel);
        header.getChildren().addAll(fileTypeIcon, fileInfoContainer);

        HBox body = new HBox();
        body.setAlignment(Pos.CENTER_RIGHT);
        body.setSpacing(4);
        VBox.setVgrow(body, Priority.ALWAYS);

        downloadLink = new Hyperlink("下载文件");
        openFileLink = new Hyperlink("打开文件");
        openFolderLink = new Hyperlink("打开文件夹");
        HBox.setMargin(openFolderLink, new Insets(0, 4, 0, 0));
        body.getChildren().addAll(downloadLink, openFileLink, openFolderLink);

        progressBar = new JFXProgressBar();
        container.setTop(header);
        container.setCenter(body);
        container.setBottom(progressBar);

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
    }

    public void initFile(String url) {
        File file = new File(url);
        fileNameLabel.setText(file.getName());
        fileSizeLabel.setText(FormatUtil.fileSize2String(file.length()));
    }
}
