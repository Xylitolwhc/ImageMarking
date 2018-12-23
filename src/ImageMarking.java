import CustomUI.TagBlockControl;
import CustomUI.TagState;
import Properties.MouseProperty;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/*
 * @author Xylitolwhc
 * @date 2018.10.26
 */

public class ImageMarking extends Application {
    private final static double SCREEN_WIDTH = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
            SCREEN_HEIGHT = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private final static String buttonStyleGreen = "-jfx-button-type: RAISED;" +
            "-fx-background-color: green;" +
            "-fx-text-fill: white;",
            buttonStyleRed = "-jfx-button-type: RAISED;" +
                    "-fx-background-color: red;" +
                    "-fx-text-fill: white;",
            buttonStyleBlue = "-jfx-button-type: RAISED;" +
                    "-fx-background-color: blue;" +
                    "-fx-text-fill: white;",
            buttonStyleWhite = "-jfx-button-type: RAISED;" +
                    "-fx-background-color: white;" +
                    "-fx-text-fill: black;";

    private MouseProperty mouseMovement, mouseProperty, movePane;
    private TagBlockControl selectedTagBlock;
    private List<TagBlockControl> tagBlocks = new ArrayList<>();
    private Path imagePath, xmlPath;
    private DoubleProperty imageWidth, imageHeight, zoomScale;
    private AnchorPane anchorPane;
    private Boolean isChanged = false;
    private ButtonBar.ButtonData alertResult;
    private JFXColorPicker pointColorPicker, lineColorPicker;
    private JFXSlider tagXSlider, tagYSlider, tagWidthSlider, tagHeightSlider, tagWidthPaddingSlider, tagHeightPaddingSlider;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("通用图片文字标注软件");
        primaryStage.getIcons().add(new Image(new File("resources" + File.separator + "ImageMarker.png").toURI().toString()));

        /*
         * 创建图片显示图层
         * 默认使用javafx图片
         */
        File defaultImage = new File("resources" + File.separator + "javafx-documentation.png");
        imagePath = defaultImage.toPath();
        xmlPath = new File("resources" + File.separator + "javafx-documentation.xml").toPath();
        Image image = new Image(defaultImage.toURI().toString());
        ImageView imageView = new ImageView();
        imageView.preserveRatioProperty().set(true);
        imageView.setImage(image);
        imageWidth = new SimpleDoubleProperty(image.getWidth());
        imageHeight = new SimpleDoubleProperty(image.getHeight());
        zoomScale = new SimpleDoubleProperty(1.0);
        zoomScale.addListener((arg0) -> {
            imageView.setFitWidth(imageWidth.get() * zoomScale.get());
            imageView.setFitHeight(imageHeight.get() * zoomScale.get());
        });

        /*
         * 工具栏，用于修改标记框各项属性
         */
        VBox settings = createSettingPane();

        /*
         * AnchorPane
         * 标记和图片的叠加层
         */
        anchorPane = new AnchorPane();
        anchorPane.getChildren().add(imageView);
        anchorPane.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (e.getButton() == MouseButton.PRIMARY &&
                    e.getX() <= imageWidth.get() * zoomScale.get() &&
                    e.getY() <= imageHeight.get() * zoomScale.get()) {
                createTagBlock(anchorPane, e.getX(), e.getY(), 0, 0, "");
                selectedTagBlock.getTextField().setVisible(false);
                selectedTagBlock.setLineColor(lineColorPicker.getValue());
                selectedTagBlock.setPointColor(pointColorPicker.getValue());
                mouseMovement = new MouseProperty(e);
            }
        });
        anchorPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
            if (e.getButton() == MouseButton.PRIMARY && mouseMovement != null && selectedTagBlock != null) {
                double biasX = e.getScreenX() - mouseMovement.getScreenX(), biasY = e.getScreenY() - mouseMovement.getScreenY();
                selectedTagBlock.updateBlock(Math.abs(biasX), Math.abs(biasY));
                if (biasX >= 0 && biasY >= 0) {
                    anchorPane.setTopAnchor(selectedTagBlock, (selectedTagBlock.getTagY() - selectedTagBlock.getTagHeightPadding()) * zoomScale.get());
                    anchorPane.setLeftAnchor(selectedTagBlock, (selectedTagBlock.getTagX() - selectedTagBlock.getTagWidthPadding()) * zoomScale.get());
                } else if (biasX < 0 && biasY >= 0) {
                    anchorPane.setTopAnchor(selectedTagBlock, (selectedTagBlock.getTagY() - selectedTagBlock.getTagHeightPadding()) * zoomScale.get());
                    anchorPane.setLeftAnchor(selectedTagBlock, (selectedTagBlock.getTagX() + biasX - selectedTagBlock.getTagWidthPadding()) * zoomScale.get());
                } else if (biasX >= 0 && biasY < 0) {
                    anchorPane.setTopAnchor(selectedTagBlock, (selectedTagBlock.getTagY() + biasY - selectedTagBlock.getTagHeightPadding()) * zoomScale.get());
                    anchorPane.setLeftAnchor(selectedTagBlock, (selectedTagBlock.getTagX() - selectedTagBlock.getTagWidthPadding()) * zoomScale.get());
                } else if (biasX < 0 && biasY < 0) {
                    anchorPane.setTopAnchor(selectedTagBlock, (selectedTagBlock.getTagY() + biasY - selectedTagBlock.getTagHeightPadding()) * zoomScale.get());
                    anchorPane.setLeftAnchor(selectedTagBlock, (selectedTagBlock.getTagX() + biasX - selectedTagBlock.getTagWidthPadding()) * zoomScale.get());
                }
            }
        });
        anchorPane.addEventHandler(MouseEvent.MOUSE_RELEASED, (e) -> {
            if (e.getButton() == MouseButton.PRIMARY && mouseMovement != null) {
                double biasX = e.getScreenX() - mouseMovement.getScreenX(), biasY = e.getScreenY() - mouseMovement.getScreenY();
                if (Math.abs(biasX) < 5 || Math.abs(biasY) < 5) {
                    if (selectedTagBlock != null) {
                        delTagBlock(selectedTagBlock, anchorPane);
                    }
                    anchorPane.requestFocus();
                } else {
                    if (selectedTagBlock != null) {
                        moveTagBlock(selectedTagBlock, anchorPane, anchorPane.getLeftAnchor(selectedTagBlock) + selectedTagBlock.getTagWidthPadding() * zoomScale.get(), anchorPane.getTopAnchor(selectedTagBlock) + selectedTagBlock.getTagHeightPadding() * zoomScale.get());
                        selectedTagBlock.getTextField().setVisible(true);
                        selectedTagBlock.getTextField().requestFocus();
                        selectedTagBlock.creationDone();
                        isChanged = true;
                        selectTagBlock(selectedTagBlock);
                    }
                }
                mouseMovement = null;
            }
        });

        /*
         * 用于控制整体移动的movableAnchorPane
         */
        AnchorPane movableAnchorPane = new AnchorPane();
        movableAnchorPane.getChildren().add(anchorPane);
        movableAnchorPane.setLeftAnchor(anchorPane, 0.0);
        movableAnchorPane.setTopAnchor(anchorPane, 0.0);
        movableAnchorPane.setPrefHeight(SCREEN_HEIGHT / 1.2);
        movableAnchorPane.setPrefWidth(SCREEN_WIDTH / 1.2);
        movableAnchorPane.addEventHandler(ScrollEvent.ANY, (ScrollEvent e) -> {
            double newX = movableAnchorPane.getLeftAnchor(anchorPane), newY = movableAnchorPane.getTopAnchor(anchorPane);
            if (e.getDeltaY() > 0) {
                newX = e.getX() - (e.getX() - newX) * 1.2;
                newY = e.getY() - (e.getY() - newY) * 1.2;
                zoomScale.set(zoomScale.get() * 1.2);
            } else if (e.getDeltaY() < 0) {
                newX = e.getX() - (e.getX() - newX) / 1.1;
                newY = e.getY() - (e.getY() - newY) / 1.1;
                zoomScale.set(zoomScale.get() / 1.1);
            }
            movableAnchorPane.setLeftAnchor(anchorPane, newX);
            movableAnchorPane.setTopAnchor(anchorPane, newY);
            for (TagBlockControl tagBlock : tagBlocks) {
                moveTagBlock(tagBlock, anchorPane, tagBlock.getTagX() * zoomScale.get(), tagBlock.getTagY() * zoomScale.get());
            }
            e.consume();
        });
        movableAnchorPane.addEventFilter(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                movePane = new MouseProperty(e);
                e.consume();
            }
        });
        movableAnchorPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, (e) -> {
            if (e.getButton() == MouseButton.SECONDARY && movePane != null) {
                double biasX = e.getScreenX() - movePane.getScreenX(),
                        biasY = e.getScreenY() - movePane.getScreenY();
                movableAnchorPane.setLeftAnchor(anchorPane, movableAnchorPane.getLeftAnchor(anchorPane) + biasX);
                movableAnchorPane.setTopAnchor(anchorPane, movableAnchorPane.getTopAnchor(anchorPane) + biasY);
                movableAnchorPane.getScene().setCursor(Cursor.CLOSED_HAND);
                movePane.set(e);
                e.consume();
            }
        });
        movableAnchorPane.addEventFilter(MouseEvent.MOUSE_RELEASED, (e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                movableAnchorPane.getScene().setCursor(Cursor.DEFAULT);
                movePane = null;
                e.consume();
            }
        });

        /*
         * Open,Save按钮
         * 用于打开和保存图片文件
         */
        JFXButton buttonOpen = new JFXButton("打开");
        buttonOpen.setStyle(buttonStyleGreen);
        buttonOpen.setOnAction((e) -> {
            if (isChanged) {
                ButtonBar.ButtonData result = showJFXAlert(primaryStage);
                if (result == ButtonBar.ButtonData.YES || result == ButtonBar.ButtonData.NO) {
                    if (result == ButtonBar.ButtonData.YES) {
                        save(xmlPath.toFile(), primaryStage);
                    }
                    openNewImage(primaryStage, imageView, movableAnchorPane);
                    isChanged = false;
                } else if (result == ButtonBar.ButtonData.CANCEL_CLOSE) {
                    e.consume();
                }
            } else {
                openNewImage(primaryStage, imageView, movableAnchorPane);
                isChanged = false;
            }
        });
        buttonOpen.setMinSize(150, 30);

        JFXButton buttonSave = new JFXButton("保存");
        buttonSave.setStyle(buttonStyleGreen);
        buttonSave.setMinSize(150, 30);
        buttonSave.setOnAction((e) -> save(xmlPath.toFile(), primaryStage));

        HBox buttons = new HBox();
        buttons.setSpacing(5.0);
        buttons.getChildren().addAll(buttonSave);

        /*
         * 带滑动条的ScrollPane
         */
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(movableAnchorPane);
//        scrollPane.setBackground(
//                new Background(new BackgroundFill(Color.TRANSPARENT, null, null))
//        );
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        /*
         * 网格布局GridPane
         * (0,0)(0,1)为菜单栏
         * (1,0)为工具栏
         * (1,1)为主操作界面
         */
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_LEFT);
        gridPane.setVgap(10.0);
        gridPane.setHgap(5.0);
        gridPane.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
        gridPane.add(buttonOpen, 0, 0);
        gridPane.add(buttons, 1, 0);
        gridPane.add(settings, 0, 1);
        gridPane.add(scrollPane, 1, 1);

        /*
         * 最底层的StackPane
         * 用于放置网格布局GridPane以及添加弹窗等功能
         */
        StackPane root = new StackPane();
        root.getChildren().add(gridPane);

        /*
         * 创建铺满屏幕的窗口
         */
        Scene scene = new Scene(root, SCREEN_WIDTH / 1.2, SCREEN_HEIGHT / 1.2, false, SceneAntialiasing.BALANCED);
        /*
         * 响应全局键盘事件
         */
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            System.out.println(e.getCode());
            switch (e.getCode()) {
                case DELETE: {
                    if (selectedTagBlock != null) {
                        delTagBlock(selectedTagBlock, anchorPane);
                    }
                    break;
                }
                case ENTER:
                case ESCAPE: {
                    anchorPane.requestFocus();
                    break;
                }
                case S: {
                    if (e.isShortcutDown()) {
                        save(xmlPath.toFile(), primaryStage);
                    }
                }
            }
        });
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> {
            Boolean isTyping = false;
            for (TagBlockControl tagBlock : tagBlocks) {
                if (tagBlock.getTextField().isFocused()) isTyping = true;
            }
            if (!isTyping)
                switch (e.getCode()) {
                    case LEFT: {
                        if (selectedTagBlock != null) {
                            moveTagBlock(selectedTagBlock, anchorPane, selectedTagBlock.getTagX() * zoomScale.get() - 1, selectedTagBlock.getTagY() * zoomScale.get());
                        }
                        e.consume();
                        break;
                    }
                    case RIGHT: {
                        if (selectedTagBlock != null) {
                            moveTagBlock(selectedTagBlock, anchorPane, selectedTagBlock.getTagX() * zoomScale.get() + 1, selectedTagBlock.getTagY() * zoomScale.get());
                        }
                        e.consume();
                        break;
                    }
                    case UP: {
                        if (selectedTagBlock != null) {
                            moveTagBlock(selectedTagBlock, anchorPane, selectedTagBlock.getTagX() * zoomScale.get(), selectedTagBlock.getTagY() * zoomScale.get() - 1);
                        }
                        e.consume();
                        break;
                    }
                    case DOWN: {
                        if (selectedTagBlock != null) {
                            moveTagBlock(selectedTagBlock, anchorPane, selectedTagBlock.getTagX() * zoomScale.get(), selectedTagBlock.getTagY() * zoomScale.get() + 1);
                        }
                        e.consume();
                        break;
                    }
                }
        });

        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setOnCloseRequest((e) -> {
            if (isChanged) {
                ButtonBar.ButtonData result = showJFXAlert(primaryStage);
                if (result == ButtonBar.ButtonData.YES) {
                    save(xmlPath.toFile(), primaryStage);
                } else if (result == ButtonBar.ButtonData.CANCEL_CLOSE) {
                    e.consume();
                }
            }
        });
        primaryStage.show();
    }

    /*
     * 创建标记框
     */
    private void createTagBlock(AnchorPane anchorPane, double x, double y, double width, double height, String text) {
        unSelectTagBlock();
        TagBlockControl newTagBlock = new TagBlockControl(x, y, width, height, zoomScale.get());
        newTagBlock.setText(text);
        /*
         *添加拖拽更改标记框大小以及移动功能
         */
        bindEvent(newTagBlock, anchorPane);
        /*
         *将标记框添加到anchorPane中
         */
        anchorPane.getChildren().add(newTagBlock);
        anchorPane.getChildren().add(newTagBlock.getTextField());
        tagBlocks.add(newTagBlock);
        moveTagBlock(newTagBlock, anchorPane, x, y);
        selectedTagBlock = newTagBlock;
    }

    private StackPane stackPaneWithLabel(String title) {
        Label label = new Label(title);
        StackPane stackPane = new StackPane(label);
        stackPane.setAlignment(Pos.TOP_LEFT);
        stackPane.setPadding(new Insets(5, 0, 0, 0));
        return stackPane;
    }

    /*
     * 删除标记框
     */
    private void delTagBlock(TagBlockControl tagBlock, AnchorPane anchorPane) {
        anchorPane.getChildren().remove(tagBlock);
        anchorPane.getChildren().remove(tagBlock.getTextField());
        tagBlocks.remove(tagBlock);
        unSelectTagBlock();
    }

    /*
     * 为标记框绑定事件，增加移动和拖动更改大小功能
     */
    private void bindEvent(TagBlockControl tagBlock, AnchorPane anchorPane) {
        tagBlock.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
            if (tagBlock.isCreationDone() && event.getButton() == MouseButton.PRIMARY) {
                switch (tagBlock.getState()) {
                    case ATTEMPT_TO_MOVE: {
                        tagBlock.setState(TagState.MOVING);
                        break;
                    }
                    case ATTEMPT_TO_RESIZE: {
                        tagBlock.setState(TagState.RESIZING);
                        break;
                    }
                }
                unSelectTagBlock();
                selectTagBlock(tagBlock);
                mouseProperty = new MouseProperty(event);
                event.consume();
            }
        });
        tagBlock.addEventHandler(MouseEvent.MOUSE_DRAGGED, (event) -> {
            if (tagBlock.isCreationDone() && event.getButton() == MouseButton.PRIMARY) {
                if (mouseProperty != null) {
                    double moveBiasX = event.getScreenX() - mouseProperty.getScreenX(), moveBiasY = event.getScreenY() - mouseProperty.getScreenY();
                    switch (tagBlock.getState()) {
                        case MOVING: {
                            moveTagBlock(tagBlock, anchorPane, tagBlock.getTagX() * zoomScale.get() + moveBiasX, tagBlock.getTagY() * zoomScale.get() + moveBiasY);
                            tagBlock.getScene().setCursor(Cursor.MOVE);
                            break;
                        }
                        case RESIZING: {
                            resizeTagBlock(selectedTagBlock, anchorPane, selectedTagBlock.getTagWidth() * zoomScale.get() + moveBiasX, selectedTagBlock.getTagHeight() * zoomScale.get() + moveBiasY);
                            break;
                        }
                    }
                    mouseProperty.set(event);
                    event.consume();
                }
            }
        });
        tagBlock.addEventHandler(MouseEvent.MOUSE_RELEASED, (event) -> {
            if (tagBlock.isCreationDone() && event.getButton() == MouseButton.PRIMARY) {
                event.consume();
                tagBlock.getScene().setCursor(Cursor.DEFAULT);
                tagBlock.setState(TagState.SELECTED);
                mouseProperty = null;
                isChanged = true;
            }
        });
        tagBlock.zoomScaleProperty().bind(zoomScale);
        tagBlock.tagXProperty().addListener(observable -> {
            if (tagBlock.getTagX() > imageWidth.get()) {
                moveTagBlock(tagBlock, anchorPane, imageWidth.get() * zoomScale.get(), tagBlock.getTagY() * zoomScale.get());
            } else if (tagBlock.getTagX() < 0) {
                moveTagBlock(tagBlock, anchorPane, 0, tagBlock.getTagY() * zoomScale.get());
            }
            moveTagBlock(tagBlock, anchorPane, tagBlock.getTagX() * zoomScale.get(), tagBlock.getTagY() * zoomScale.get());
        });
        tagBlock.tagYProperty().addListener(observable -> {
            if (tagBlock.getTagY() > imageHeight.get()) {
                moveTagBlock(tagBlock, anchorPane, tagBlock.getTagX() * zoomScale.get(), imageHeight.get() * zoomScale.get());
            } else if (tagBlock.getTagY() < 0) {
                moveTagBlock(tagBlock, anchorPane, tagBlock.getTagX() * zoomScale.get(), 0);
            }
            moveTagBlock(tagBlock, anchorPane, tagBlock.getTagX() * zoomScale.get(), tagBlock.getTagY() * zoomScale.get());
        });
        tagBlock.tagWidthProperty().addListener(observable -> {
            moveTagBlock(tagBlock, anchorPane, tagBlock.getTagX() * zoomScale.get(), tagBlock.getTagY() * zoomScale.get());
        });
        tagBlock.tagHeightProperty().addListener(observable -> {
            moveTagBlock(tagBlock, anchorPane, tagBlock.getTagX() * zoomScale.get(), tagBlock.getTagY() * zoomScale.get());
        });
        tagBlock.tagWidthPaddingProperty().addListener(observable -> {
            moveTagBlock(tagBlock, anchorPane, tagBlock.getTagX() * zoomScale.get(), tagBlock.getTagY() * zoomScale.get());
        });
        tagBlock.tagHeightPaddingProperty().addListener(observable -> {
            moveTagBlock(tagBlock, anchorPane, tagBlock.getTagX() * zoomScale.get(), tagBlock.getTagY() * zoomScale.get());
        });
    }

    /*
     * 工具栏，用于修改标记框各项属性
     */
    private VBox createSettingPane() {

        Label label = new Label("--标记框属性--");
        label.setStyle("-fx-font-size: 18px;");
        StackPane title = new StackPane(label);
        title.setAlignment(Pos.TOP_CENTER);
        title.setPadding(new Insets(5, 0, 5, 0));

        StackPane tagXLabel = stackPaneWithLabel("X坐标:");
        tagXSlider = new JFXSlider();
        tagXSlider.setMax(imageWidth.get());
        tagXSlider.setMinWidth(150.0);

        StackPane tagYLabel = stackPaneWithLabel("Y坐标:");
        tagYSlider = new JFXSlider();
        tagYSlider.setMax(imageHeight.get());
        tagYSlider.setMinWidth(150.0);

        StackPane tagWidthLabel = stackPaneWithLabel("宽度:");
        tagWidthSlider = new JFXSlider();
        tagWidthSlider.setMax(imageWidth.get());
        tagWidthSlider.setMinWidth(150.0);

        StackPane tagHeightLabel = stackPaneWithLabel("高度:");
        tagHeightSlider = new JFXSlider();
        tagHeightSlider.setMax(imageHeight.get());
        tagHeightSlider.setMinWidth(150.0);

        StackPane tagWidthPaddingLabel = stackPaneWithLabel("左右边距:");
        tagWidthPaddingSlider = new JFXSlider();
        tagWidthPaddingSlider.setMax(20.0);
        tagWidthPaddingSlider.setMinWidth(150.0);

        StackPane tagHeightPaddingLabel = stackPaneWithLabel("上下边距:");
        tagHeightPaddingSlider = new JFXSlider();
        tagHeightPaddingSlider.setMax(20.0);
        tagHeightPaddingSlider.setMinWidth(150.0);

        StackPane pointColorLabel = stackPaneWithLabel("点色彩:");
        pointColorPicker = new JFXColorPicker();
        pointColorPicker.setValue(Color.BLACK);
        pointColorPicker.setOnAction((e -> {
            if (selectedTagBlock != null) {
                selectedTagBlock.setPointColor(pointColorPicker.getValue());
            }

        }));
        StackPane pointColorPickerPane = new StackPane(pointColorPicker);
        pointColorPickerPane.setAlignment(Pos.TOP_RIGHT);

        StackPane lineColorLabel = stackPaneWithLabel("线条色彩:");
        lineColorPicker = new JFXColorPicker();
        lineColorPicker.setValue(Color.BLACK);
        lineColorPicker.setOnAction((e -> {
            if (selectedTagBlock != null) {
                selectedTagBlock.setLineColor(lineColorPicker.getValue());
            }

        }));
        StackPane lineColorPickerPane = new StackPane(lineColorPicker);
        lineColorPickerPane.setAlignment(Pos.TOP_RIGHT);

        VBox vBox = new VBox();
        vBox.setMinWidth(150);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setSpacing(5.0);
        vBox.getChildren().addAll(title,
                tagXLabel, tagXSlider,
                tagYLabel, tagYSlider,
                tagWidthLabel, tagWidthSlider,
                tagHeightLabel, tagHeightSlider,
                tagWidthPaddingLabel, tagWidthPaddingSlider,
                tagHeightPaddingLabel, tagHeightPaddingSlider,
                pointColorLabel, pointColorPickerPane,
                lineColorLabel, lineColorPickerPane);
        return vBox;
    }

    /*
     * 移动标记框
     */
    private void moveTagBlock(TagBlockControl tagBlock, AnchorPane anchorPane, double x, double y) {
        tagBlock.updateBlockXY(x, y);
        anchorPane.setTopAnchor(tagBlock, (tagBlock.getTagY() - tagBlock.getTagHeightPadding()) * zoomScale.get());
        anchorPane.setLeftAnchor(tagBlock, (tagBlock.getTagX() - tagBlock.getTagWidthPadding()) * zoomScale.get());
        if (anchorPane.getChildren().contains(tagBlock.getTextField())) {
            anchorPane.setTopAnchor(tagBlock.getTextField(), (tagBlock.getTagY() + tagBlock.getTagHeight() + tagBlock.getTagRadius()) * zoomScale.get());
            anchorPane.setLeftAnchor(tagBlock.getTextField(), (tagBlock.getTagX() + tagBlock.getTagWidth() + tagBlock.getTagRadius()) * zoomScale.get());
        }
    }

    /*
     * 更改标记框大小
     */
    private void resizeTagBlock(TagBlockControl tagBlock, AnchorPane anchorPane, double width, double height) {
        tagBlock.updateBlock(width, height);
        anchorPane.setTopAnchor(tagBlock.getTextField(), (tagBlock.getTagY() + tagBlock.getTagHeight() + tagBlock.getTagRadius()) * zoomScale.get());
        anchorPane.setLeftAnchor(tagBlock.getTextField(), (tagBlock.getTagX() + tagBlock.getTagWidth() + tagBlock.getTagRadius()) * zoomScale.get());
    }

    /*
     * 打开图片文件以及配置文件
     */
    private void openNewImage(Stage primaryStage, ImageView imageView, AnchorPane movableAnchorPane) {
        File newFile = getImage(primaryStage);
        if (newFile != null) {
            if (!newFile.isDirectory() && newFile.exists()) {
                anchorPane.getChildren().clear();
                tagBlocks.clear();
                unSelectTagBlock();
                try {
                    Image newImage = new Image(newFile.toURI().toString());
                    imageView.setImage(newImage);
                    if (!anchorPane.getChildren().contains(imageView)) {
                        anchorPane.getChildren().add(imageView);
                    }
                    imageWidth.set(newImage.getWidth());
                    imageHeight.set(newImage.getHeight());
                    if (tagXSlider != null) {
                        tagXSlider.setMax(imageWidth.get());
                    }
                    if (tagYSlider != null) {
                        tagYSlider.setMax(imageHeight.get());
                    }
                    if (tagWidthSlider != null) {
                        tagWidthSlider.setMax(imageWidth.get());
                    }
                    if (tagHeightSlider != null) {
                        tagHeightSlider.setMax(imageHeight.get());
                    }
                    movableAnchorPane.setTopAnchor(anchorPane, 0.0);
                    movableAnchorPane.setLeftAnchor(anchorPane, 0.0);
                    imagePath = newFile.toPath();
                    //判断是否有同名xml文件
                    int position = imagePath.toString().lastIndexOf(".");
                    File xmlFile = new File(imagePath.toString().substring(0, position + 1) + "xml");
                    xmlPath = xmlFile.toPath();
                    if (xmlFile.exists()) {
                        readXml(xmlFile);
                    }
                    zoomScale.set(1.0);
                    isChanged = false;
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    /*
     * 选择图片文件
     */
    private File getImage(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("IMAGE", "*.bmp", "*.gif", "*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("All", "*.*")
        );
        File file = fileChooser.showOpenDialog(stage);
        return file;
    }

    /*
     * 选择标记框并与属性面板各项内容绑定
     */
    private void selectTagBlock(TagBlockControl tagBlock) {
        selectedTagBlock = tagBlock;
        if (lineColorPicker != null)
            lineColorPicker.setValue(selectedTagBlock.getLineColor());
        if (pointColorPicker != null)
            pointColorPicker.setValue(selectedTagBlock.getPointColor());
        if (tagXSlider != null) {
            tagXSlider.valueProperty().bindBidirectional(selectedTagBlock.tagXProperty());
        }
        if (tagYSlider != null) {
            tagYSlider.valueProperty().bindBidirectional(selectedTagBlock.tagYProperty());
        }
        if (tagWidthSlider != null) {
            tagWidthSlider.valueProperty().bindBidirectional(selectedTagBlock.tagWidthProperty());
        }
        if (tagHeightSlider != null) {
            tagHeightSlider.valueProperty().bindBidirectional(selectedTagBlock.tagHeightProperty());
        }
        if (tagWidthPaddingSlider != null) {
            tagWidthPaddingSlider.valueProperty().bindBidirectional(selectedTagBlock.tagWidthPaddingProperty());
        }
        if (tagHeightPaddingSlider != null) {
            tagHeightPaddingSlider.valueProperty().bindBidirectional(selectedTagBlock.tagHeightPaddingProperty());
        }
    }

    /*
     * 取消选择标记框
     */
    private void unSelectTagBlock() {
        if (selectedTagBlock != null) {
            if (tagXSlider != null) {
                tagXSlider.valueProperty().unbindBidirectional(selectedTagBlock.tagXProperty());
            }
            if (tagYSlider != null) {
                tagYSlider.valueProperty().unbindBidirectional(selectedTagBlock.tagYProperty());
            }
            if (tagWidthSlider != null) {
                tagWidthSlider.valueProperty().unbindBidirectional(selectedTagBlock.tagWidthProperty());
            }
            if (tagHeightSlider != null) {
                tagHeightSlider.valueProperty().unbindBidirectional(selectedTagBlock.tagHeightProperty());
            }
            if (tagWidthPaddingSlider != null) {
                tagWidthPaddingSlider.valueProperty().unbindBidirectional(selectedTagBlock.tagWidthPaddingProperty());
            }
            if (tagHeightPaddingSlider != null) {
                tagHeightPaddingSlider.valueProperty().unbindBidirectional(selectedTagBlock.tagHeightPaddingProperty());
            }
        }
        selectedTagBlock = null;
    }

    /*
     * 保存为xml文件
     */
    private void save(File xmlFile, Stage stage) {
        //dom
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(false);
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.newDocument();
            document.setXmlStandalone(true);
            Element tagBlocksElement = document.createElement("tagblocks");
            for (TagBlockControl tagBlock : tagBlocks) {
                Element tagBlockElement = document.createElement("tagblock");
                Element x = document.createElement("x");
                x.setTextContent(tagBlock.getTagX() + "");
                Element y = document.createElement("y");
                y.setTextContent(tagBlock.getTagY() + "");
                Element width = document.createElement("width");
                width.setTextContent(tagBlock.getTagWidth() + "");
                Element height = document.createElement("height");
                height.setTextContent(tagBlock.getTagHeight() + "");
                Element widthPadding = document.createElement("widthPadding");
                widthPadding.setTextContent(tagBlock.getTagWidthPadding() + "");
                Element heightPadding = document.createElement("heightPadding");
                heightPadding.setTextContent(tagBlock.getTagHeightPadding() + "");
                Element lineColor = document.createElement("lineColor");
                lineColor.setTextContent(tagBlock.getLineColor().toString());
                Element pointColor = document.createElement("pointColor");
                pointColor.setTextContent(tagBlock.getPointColor().toString());
                Element text = document.createElement("text");
                text.setTextContent(tagBlock.getText());
                tagBlockElement.appendChild(x);
                tagBlockElement.appendChild(y);
                tagBlockElement.appendChild(width);
                tagBlockElement.appendChild(height);
                tagBlockElement.appendChild(widthPadding);
                tagBlockElement.appendChild(heightPadding);
                tagBlockElement.appendChild(lineColor);
                tagBlockElement.appendChild(pointColor);
                tagBlockElement.appendChild(text);
                tagBlocksElement.appendChild(tagBlockElement);
            }
            document.appendChild(tagBlocksElement);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(document), new StreamResult(xmlFile));
            isChanged = false;
            showSavedDialog(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 读取xml文件
     */
    private void readXml(File xmlFile) {
        //dom
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(false);
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            NodeList nodeList = ((Element) document.getElementsByTagName("tagblocks").item(0)).getElementsByTagName("tagblock");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                double x = Double.parseDouble(element.getElementsByTagName("x").item(0).getTextContent());
                double y = Double.parseDouble(element.getElementsByTagName("y").item(0).getTextContent());
                double width = Double.parseDouble(element.getElementsByTagName("width").item(0).getTextContent());
                double height = Double.parseDouble(element.getElementsByTagName("height").item(0).getTextContent());
                double widthPadding = Double.parseDouble(element.getElementsByTagName("widthPadding").item(0).getTextContent());
                double heightPadding = Double.parseDouble(element.getElementsByTagName("heightPadding").item(0).getTextContent());
                Color lineColor = Color.valueOf(element.getElementsByTagName("lineColor").item(0).getTextContent());
                Color pointColor = Color.valueOf(element.getElementsByTagName("pointColor").item(0).getTextContent());
                String text = element.getElementsByTagName("text").item(0).getTextContent();
                createTagBlock(anchorPane, x, y, width, height, text);
                selectedTagBlock.setLineColor(lineColor);
                selectedTagBlock.setPointColor(pointColor);
                selectedTagBlock.setTagWidthPadding(widthPadding);
                selectedTagBlock.setTagHeightPadding(heightPadding);
                tagBlocks.add(selectedTagBlock);
            }
            unSelectTagBlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 弹出已保存提示框
     */
    private void showSavedDialog(Stage stage) {
        JFXAlert<Void> jfxAlert = new JFXAlert<>(stage);

        Label label = new Label("保存成功！");
        label.setStyle("-fx-font-size: 20px;");
        label.setPadding(new Insets(10, 10, 10, 10));

        JFXButton buttonOK = new JFXButton("好的");
        buttonOK.setStyle(buttonStyleGreen);
        buttonOK.setMinSize(60, 30);
        buttonOK.setOnAction((event -> jfxAlert.close()));
        HBox hBox = new HBox(buttonOK);
        hBox.setAlignment(Pos.BOTTOM_RIGHT);

        JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
        jfxDialogLayout.setHeading(label);
        jfxDialogLayout.setBody(hBox);

        jfxAlert.setOverlayClose(true);
        jfxAlert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        jfxAlert.setContent(jfxDialogLayout);
        jfxAlert.initModality(Modality.WINDOW_MODAL);
        jfxAlert.showAndWait();
    }

    /*
     * 弹出未保存提示框
     */
    private ButtonBar.ButtonData showJFXAlert(Stage stage) {
        alertResult = ButtonBar.ButtonData.CANCEL_CLOSE;
        JFXAlert<Void> jfxAlert = new JFXAlert<>(stage);

        JFXButton confirmSave = new JFXButton("保存");
        confirmSave.setStyle(buttonStyleGreen);
        confirmSave.setMinSize(60, 30);
        confirmSave.setOnAction(e -> {
            alertResult = ButtonBar.ButtonData.YES;
            jfxAlert.close();
        });
        JFXButton notSave = new JFXButton("不保存");
        notSave.setStyle(buttonStyleRed);
        notSave.setMinSize(60, 30);
        notSave.setOnAction(e -> {
            alertResult = ButtonBar.ButtonData.NO;
            jfxAlert.close();
        });
        JFXButton cancel = new JFXButton("取消");
        cancel.setStyle(buttonStyleWhite);
        cancel.setMinSize(60, 30);
        cancel.setOnAction(e -> {
            alertResult = ButtonBar.ButtonData.CANCEL_CLOSE;
            jfxAlert.close();
        });

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(confirmSave, notSave, cancel);
        hBox.setAlignment(Pos.BOTTOM_RIGHT);

        Label label = new Label("您所作的修改还未保存，是否保存？");
        label.setStyle("-fx-font-size: 20px;");

        JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
        jfxDialogLayout.setHeading(label);
        jfxDialogLayout.setBody(hBox);

        jfxAlert.setOverlayClose(false);
        jfxAlert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        jfxAlert.setContent(jfxDialogLayout);
        jfxAlert.initModality(Modality.WINDOW_MODAL);
        jfxAlert.showAndWait();
        return alertResult;
    }

    public static void main(String... args) {
        launch(args);
    }

}
