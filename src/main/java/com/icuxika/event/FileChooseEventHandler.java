package com.icuxika.event;

import com.icuxika.model.home.FileChooseType;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.function.Consumer;

/**
 * 文件选择事件
 */
public class FileChooseEventHandler implements EventHandler<MouseEvent> {

    private final Node owner;

    private final FileChooseType type;

    private final Consumer<File> fileConsumer;

    public FileChooseEventHandler(Node owner, FileChooseType type, Consumer<File> fileConsumer) {
        this.owner = owner;
        this.type = type;
        this.fileConsumer = fileConsumer;
    }

    @Override
    public void handle(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择文件");
        if (type.equals(FileChooseType.IMAGE)) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.svg", "*gif"));
        }
        File file = fileChooser.showOpenDialog(owner.getScene().getWindow());
        if (file != null) fileConsumer.accept(file);
    }
}
