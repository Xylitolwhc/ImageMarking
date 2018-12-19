import CustomUI.TagBlockControl;
import CustomUI.TagState;
import Properties.MouseProperty;
import com.sun.istack.internal.Nullable;
import com.sun.javafx.css.Style;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.lang.StrictMath.random;

public class ImageMarking extends Application {
    public final static double SCREEN_WIDTH = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
            SCREEN_HEIGHT = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    private MouseProperty mouseMovement, mouseProperty;
    private Scene scene;
    private TagBlockControl selectedTagBlock;
    private List<TagBlockControl> tagBlocks = new ArrayList<>();
    private double imageWidth, imageHeight;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Test");

        /*
         *创建图片显示图层
         *默认使用javafx图片
         */
        Image image = new Image("https://docs.oracle.com/javafx/javafx/images/javafx-documentation.png");
        ImageView imageView = new ImageView();
        imageView.preserveRatioProperty().set(true);
        imageView.setImage(image);

        DoubleProperty zoomProperty = new SimpleDoubleProperty(200);
        zoomProperty.addListener((Observable arg0) -> {
            imageView.setFitWidth(zoomProperty.get());
            imageView.setFitHeight(zoomProperty.get());
        });
/*
        scrollPane.addEventFilter(ScrollEvent.ANY, (ScrollEvent event) -> {
            if (event.getDeltaY() > 0) {
                zoomProperty.set(zoomProperty.get() * 1.2);
            } else if (event.getDeltaY() < 0) {
                zoomProperty.set(zoomProperty.get() / 1.1);
            }
        });

        imageView.addEventFilter(MouseEvent.MOUSE_PRESSED, (event -> {
            imageProperty.setScreenX(event.getScreenX());
            imageProperty.setScreenY(event.getScreenY());
            imageProperty.setX(imageView.getX());
            imageProperty.setY(imageView.getY());
        }));
*/


        /*
         *AnchorPane
         *标记和图片的叠加层
         */
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(imageView);
        anchorPane.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                TagBlockControl newTagBlock = new TagBlockControl(e.getX(), e.getY(), 0, 0);
                /*
                 *添加拖拽更改标记框大小以及移动功能
                 */
                newTagBlock.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
                    if (newTagBlock.isCreationDone() && event.getButton() == MouseButton.PRIMARY) {

                        switch (newTagBlock.getState()) {
                            case ATTEMPT_TO_MOVE: {
                                newTagBlock.setState(TagState.MOVING);
                                break;
                            }
                            case ATTEMPT_TO_RESIZE: {
                                newTagBlock.setState(TagState.RESIZING);
                                break;
                            }
                        }
                        selectedTagBlock = newTagBlock;
                        mouseProperty = new MouseProperty(event);
                        event.consume();
                    }
                });
                newTagBlock.addEventHandler(MouseEvent.MOUSE_DRAGGED, (event) -> {
                    if (newTagBlock.isCreationDone() && event.getButton() == MouseButton.PRIMARY) {
                        if (mouseProperty != null) {
                            double moveBiasX = event.getScreenX() - mouseProperty.getScreenX(), moveBiasY = event.getScreenY() - mouseProperty.getScreenY();
                            switch (newTagBlock.getState()) {
                                case MOVING: {
                                    moveTagBlock(newTagBlock, anchorPane, newTagBlock.getX() + moveBiasX, newTagBlock.getY() + moveBiasY);
                                    newTagBlock.getScene().setCursor(Cursor.MOVE);
                                    break;
                                }
                                case RESIZING: {
                                    resizeTagBlock(selectedTagBlock, anchorPane, selectedTagBlock.getTagWidth() + moveBiasX, selectedTagBlock.getTagHeight() + moveBiasY);
                                    break;
                                }
                            }
                            mouseProperty.set(event);
                            event.consume();
                        }
                    }
                });
                newTagBlock.addEventHandler(MouseEvent.MOUSE_RELEASED, (event) -> {
                    if (newTagBlock.isCreationDone() && event.getButton() == MouseButton.PRIMARY) {
                        event.consume();
                        newTagBlock.getScene().setCursor(Cursor.DEFAULT);
                        newTagBlock.setState(TagState.SELECTED);
                        mouseProperty = null;
                    }
                });
                /*
                 *将标记框添加到anchorPane中
                 */
                anchorPane.getChildren().add(newTagBlock);
                moveTagBlock(newTagBlock, anchorPane, e.getX(), e.getY());
                selectedTagBlock = newTagBlock;
                mouseMovement = new MouseProperty(e);
            }
        });
        anchorPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
            if (e.getButton() == MouseButton.PRIMARY && mouseMovement != null) {
                double biasX = e.getScreenX() - mouseMovement.getScreenX(), biasY = e.getScreenY() - mouseMovement.getScreenY();
                selectedTagBlock.updateBlock(Math.abs(biasX), Math.abs(biasY));
                if (biasX >= 0 && biasY >= 0) {
                    anchorPane.setTopAnchor(selectedTagBlock, selectedTagBlock.getY() - selectedTagBlock.getTagHeightPadding());
                    anchorPane.setLeftAnchor(selectedTagBlock, selectedTagBlock.getX() - selectedTagBlock.getTagWidthPadding());
                } else if (biasX < 0 && biasY >= 0) {
                    anchorPane.setTopAnchor(selectedTagBlock, selectedTagBlock.getY() - selectedTagBlock.getTagHeightPadding());
                    anchorPane.setLeftAnchor(selectedTagBlock, selectedTagBlock.getX() + biasX - selectedTagBlock.getTagWidthPadding());
                } else if (biasX >= 0 && biasY < 0) {
                    anchorPane.setTopAnchor(selectedTagBlock, selectedTagBlock.getY() + biasY - selectedTagBlock.getTagHeightPadding());
                    anchorPane.setLeftAnchor(selectedTagBlock, selectedTagBlock.getX() - selectedTagBlock.getTagWidthPadding());
                } else if (biasX < 0 && biasY < 0) {
                    anchorPane.setTopAnchor(selectedTagBlock, selectedTagBlock.getY() + biasY - selectedTagBlock.getTagHeightPadding());
                    anchorPane.setLeftAnchor(selectedTagBlock, selectedTagBlock.getX() + biasX - selectedTagBlock.getTagWidthPadding());
                }
            }
        });
        anchorPane.addEventHandler(MouseEvent.MOUSE_RELEASED, (e) -> {
            if (e.getButton() == MouseButton.PRIMARY && mouseMovement != null) {
                double biasX = e.getScreenX() - mouseMovement.getScreenX(), biasY = e.getScreenY() - mouseMovement.getScreenY();
                if (Math.abs(biasX) < 5 || Math.abs(biasY) < 5) {
                    if (selectedTagBlock != null) {
                        anchorPane.getChildren().remove(selectedTagBlock);
                        tagBlocks.remove(selectedTagBlock);
                    }
                    anchorPane.requestFocus();
                } else {
                    tagBlocks.add(selectedTagBlock);
                    anchorPane.getChildren().add(selectedTagBlock.getTextField());
                    selectedTagBlock.getTextField().requestFocus();
                    moveTagBlock(selectedTagBlock, anchorPane, anchorPane.getLeftAnchor(selectedTagBlock) + selectedTagBlock.getTagWidthPadding(), anchorPane.getTopAnchor(selectedTagBlock) + selectedTagBlock.getTagHeightPadding());
                }
                selectedTagBlock.creationDone();
                mouseMovement = null;
            }
        });

        /*
         *Open按钮
         *用于打开图片文件
         */
        Button buttonOpen = new Button();
        buttonOpen.setText("Open");
        buttonOpen.setOnAction((e) -> {
            File newFile = getImage(primaryStage);
            if (newFile != null) {
                anchorPane.getChildren().clear();
                tagBlocks.clear();
                Image newImage = new Image(newFile.toURI().toString());
                imageWidth = image.getWidth();
                imageHeight = image.getHeight();
                imageView.setImage(newImage);
                anchorPane.getChildren().add(imageView);
            }
        });
        buttonOpen.setPrefSize(200, 50);

        /*
         *由图片和标记层叠加而成的StackPane
         */
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.CENTER);
        stackPane.getChildren().add(anchorPane);
        stackPane.setPrefHeight(SCREEN_HEIGHT / 1.2);
        stackPane.setPrefWidth(SCREEN_WIDTH / 1.2);

        /*
         *带滑动条的ScrollPane
         */
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(stackPane);
//        scrollPane.setBackground(
//                new Background(new BackgroundFill(Color.TRANSPARENT, null, null))
//        );
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.addEventFilter(ScrollEvent.ANY, (ScrollEvent event) -> {
            if (event.getDeltaY() > 0) {
                zoomProperty.set(zoomProperty.get() * 1.2);
            } else if (event.getDeltaY() < 0) {
                zoomProperty.set(zoomProperty.get() / 1.1);
            }
        });

        /*
         *网格布局GridPane
         *(0,0)(0,1)为菜单栏
         *(1,0)为工具栏
         *(1,1)为主操作界面
         */
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_LEFT);
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.add(buttonOpen, 0, 0);
        gridPane.add(scrollPane, 1, 1);

        /*
         *最底层的StackPane
         *用于放置网格布局GridPane以及添加后续弹窗等功能
         */
        StackPane root = new StackPane();
        root.getChildren().add(gridPane);

        /*
         *创建铺满屏幕的窗口
         */
        scene = new Scene(root, SCREEN_WIDTH / 1.2, SCREEN_HEIGHT / 1.2);
        /*
         *响应全局键盘事件
         */
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            System.out.println(e.getCode());
            switch (e.getCode()) {
                case DELETE: {
                    if (selectedTagBlock != null) {
                        anchorPane.getChildren().remove(selectedTagBlock);
                        anchorPane.getChildren().remove(selectedTagBlock.getTextField());
                        selectedTagBlock = null;
                    }
                    break;
                }
                case ENTER:
                case ESCAPE: {
                    anchorPane.requestFocus();
                    break;
                }
            }
        });
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private void moveTagBlock(TagBlockControl tagBlock, AnchorPane anchorPane, double x, double y) {
        tagBlock.updateBlockXY(x, y);
        anchorPane.setTopAnchor(tagBlock, tagBlock.getY() - tagBlock.getTagHeightPadding());
        anchorPane.setLeftAnchor(tagBlock, tagBlock.getX() - tagBlock.getTagWidthPadding());
        anchorPane.setTopAnchor(tagBlock.getTextField(), tagBlock.getY() + tagBlock.getTagHeight() + tagBlock.getTagHeightPadding());
        anchorPane.setLeftAnchor(tagBlock.getTextField(), tagBlock.getX() + tagBlock.getTagWidth() + tagBlock.getTagWidthPadding());
    }

    private void resizeTagBlock(TagBlockControl tagBlock, AnchorPane anchorPane, double width, double height) {
        tagBlock.updateBlock(width, height);
        anchorPane.setTopAnchor(tagBlock.getTextField(), tagBlock.getY() + tagBlock.getTagHeight() + tagBlock.getTagHeightPadding());
        anchorPane.setLeftAnchor(tagBlock.getTextField(), tagBlock.getX() + tagBlock.getTagWidth() + tagBlock.getTagWidthPadding());
    }

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
//        JFileChooser fileChooser=new JFileChooser();
//        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//        fileChooser.setFileFilter(new FileFilter() {
//            @Override
//            public boolean accept(File f) {
//                return f.isDirectory() ||
//                        f.getName().toLowerCase().endsWith(".jpg") ||
//                        f.getName().toLowerCase().endsWith(".jpeg") ||
//                        f.getName().toLowerCase().endsWith(".png") ||
//                        f.getName().toLowerCase().endsWith(".gif") ||
//                        f.getName().toLowerCase().endsWith(".bmp");
//            }
//
//            @Override
//            public String getDescription() {
//                return "*.jpg;*.jpeg;*.png;*.gif;*.bmp";
//            }
//        });
//        fileChooser.showOpenDialog(new JLabel());
//        File file = fileChooser.getSelectedFile();
//        if (file != null) {
//            System.out.println("文件:" + file.getAbsolutePath());
//            System.out.println(fileChooser.getSelectedFile().getName());
//            return file.toURI().toString();
//        } else return null;
    }

    private ImageView drawImage(String uri) {
        Image image = new Image(uri);
        ImageView imageView = new ImageView();
        imageView.setImage(image);

        // 获取PixelReader
        PixelReader pixelReader = image.getPixelReader();
        System.out.println("Image Width: " + image.getWidth());
        System.out.println("Image Height: " + image.getHeight());
        System.out.println("Pixel Format: " + pixelReader.getPixelFormat());

        // 确定图片中每一个像素的颜色
//        for (int readY = 0; readY < image.getHeight(); readY++) {
//            for (int readX = 0; readX < image.getWidth(); readX++) {
//                Color color = pixelReader.getColor(readX, readY);
//                System.out.println("\nPixel color at coordinates ("
//                        + readX + "," + readY + ") "
//                        + color.toString());
//                System.out.println("R = " + color.getRed());
//                System.out.println("G = " + color.getGreen());
//                System.out.println("B = " + color.getBlue());
//                System.out.println("Opacity = " + color.getOpacity());
//                System.out.println("Saturation = " + color.getSaturation());
//            }
//        }
        return imageView;
    }

    public static void main(String... args) {
        launch(args);
    }

    private void helloworld(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
        //gridPane.setGridLinesVisible(true);

        Text scenetitle = new Text("Welcome");
        scenetitle.setId("welcome-text");
        gridPane.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        userName.setFont(Font.font("Microsoft Yahei"));
        gridPane.add(userName, 0, 1);

        TextField userTextField = new TextField();
        gridPane.add(userTextField, 1, 1);

        Label password = new Label("Password:");
        gridPane.add(password, 0, 2);

        PasswordField passwordField = new PasswordField();
        gridPane.add(passwordField, 1, 2);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);//将按钮控件作为子节点
        gridPane.add(hbBtn, 1, 4);//将HBox pane放到grid中的第1列，第4行

        final Text actionTarget = new Text();
        actionTarget.setId("actiontarget");
        gridPane.add(actionTarget, 1, 6);

        btn.setOnAction((e) -> {
            actionTarget.setText("Pressed");
        });

        Scene scene = new Scene(gridPane, 300, 275);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(ImageMarking.class.getResource("Login.css").toExternalForm());
        primaryStage.show();
    }

    private void animation(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 1600, 900, Color.BLACK);
        primaryStage.setScene(scene);
        Group circles = new Group();

        for (int i = 0; i < 30; i++) {
            Circle circle = new Circle(150, Color.web("white", 0.05));
            circle.setStrokeType(StrokeType.OUTSIDE);
            circle.setStroke(Color.web("white", 0.16));
            circle.setStrokeWidth(4);
            circles.getChildren().add(circle);
        }
        circles.setEffect(new BoxBlur(10, 10, 3));

        Rectangle colors = new Rectangle(scene.getWidth(), scene.getHeight(),
                new LinearGradient(0f, 1f, 1f, 0f, true, CycleMethod.NO_CYCLE, new
                        Stop[]{
                        new Stop(0, Color.web("#f8bd55")),
                        new Stop(0.14, Color.web("#c0fe56")),
                        new Stop(0.28, Color.web("#5dfbc1")),
                        new Stop(0.43, Color.web("#64c2f8")),
                        new Stop(0.57, Color.web("#be4af7")),
                        new Stop(0.71, Color.web("#ed5fc2")),
                        new Stop(0.85, Color.web("#ef504c")),
                        new Stop(1, Color.web("#f2660f")),}));
        colors.widthProperty().bind(scene.widthProperty());
        colors.heightProperty().bind(scene.heightProperty());

        Group blendModeGroup = new Group(new Group(new Rectangle(scene.getWidth(), scene.getHeight(),
                Color.BLACK), circles), colors);
        colors.setBlendMode(BlendMode.OVERLAY);
        root.getChildren().add(blendModeGroup);

        Timeline timeline = new Timeline();

        for (Node circle : circles.getChildren()) {
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, // set start position at 0
                            new KeyValue(circle.translateXProperty(), random() * 1600),
                            new KeyValue(circle.translateYProperty(), random() * 900)
                    ),

                    new KeyFrame(new Duration(40000), // set end position at 40s
                            new KeyValue(circle.translateXProperty(), random() * 1600),
                            new KeyValue(circle.translateYProperty(), random() * 900)
                    )
            );
        }

// play 40s of animation
        timeline.play();

        primaryStage.show();
    }

}