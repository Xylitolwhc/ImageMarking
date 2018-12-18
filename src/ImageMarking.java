import CustomUI.TagBlockControl;
import CustomUI.TagState;
import Properties.MouseProperty;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
    MouseProperty imageProperty = new MouseProperty();
    MouseProperty mouseMovement, mouseProperty;
    Scene scene;
    TagBlockControl selectedTagBlock;
    List<TagBlockControl> tagBlocks = new ArrayList<>();

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

        imageView.addEventFilter(MouseEvent.MOUSE_DRAGGED, (event) -> {
            double newX = event.getScreenX(), newY = event.getScreenY();
            double oldX = imageProperty.getScreenX(), oldY = imageProperty.getScreenY();
            imageView.setTranslateX(newX - oldX + imageProperty.getX());
            imageView.setTranslateY(newY - oldY + imageProperty.getY());
            imageView.setX(newX - oldX + imageProperty.getX());
            imageView.setY(newY - oldY + imageProperty.getY());
            event.consume();
        });
*/


        /*
         *AnchorPane
         *标记叠加层
         */
        AnchorPane anchorPane = new AnchorPane();
//        anchorPane.setOnMouseClicked((e) -> {
//            System.out.println(e.getX() + " " + e.getY());
//            TagBlockControl tagBlockControl = new TagBlockControl(e.getX(), e.getY(), e.getX(), e.getY());
//            tagBlockControl.setOnAction((event) -> {
//                tagBlockControl.updateBlock(tagBlockControl.getTagWidth() + 10, tagBlockControl.getTagHeight() + 10);
//            });
//            anchorPane.getChildren().add(tagBlockControl);
//            anchorPane.setTopAnchor(tagBlockControl, e.getY() - 5);
//            anchorPane.setLeftAnchor(tagBlockControl, e.getX() - 5);
//        });
        anchorPane.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                TagBlockControl newTagBlock = new TagBlockControl(e.getX(), e.getY(), 0, 0);
                /*
                 *添加拖拽更改标记框大小以及移动功能
                 */
                newTagBlock.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
                    if (newTagBlock.isCreationDone()) {

                        switch(newTagBlock.getState()){
                            case ATTEMPT_TO_MOVE:{
                                newTagBlock.setState(TagState.MOVING);
                                break;
                            }
                            case ATTEMPT_TO_RESIZE:{
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
                    if (newTagBlock.isCreationDone()) {
                        if (mouseProperty != null) {
                            double moveBiasX = event.getScreenX() - mouseProperty.getScreenX(), moveBiasY = event.getScreenY() - mouseProperty.getScreenY();
                            switch (newTagBlock.getState()) {
                                case MOVING: {
                                    anchorPane.setLeftAnchor(newTagBlock, newTagBlock.getX() + moveBiasX - newTagBlock.getTagWidthPadding());
                                    anchorPane.setTopAnchor(newTagBlock, newTagBlock.getY() + moveBiasY - newTagBlock.getTagHeightPadding());
                                    newTagBlock.updateBlockXY(newTagBlock.getX() + moveBiasX, newTagBlock.getY() + moveBiasY);
                                    newTagBlock.getScene().setCursor(Cursor.MOVE);
                                    break;
                                }
                                case RESIZING: {
                                    selectedTagBlock.updateBlock(selectedTagBlock.getTagWidth() + moveBiasX, selectedTagBlock.getTagHeight() + moveBiasY);
                                    break;
                                }
                            }
                            mouseProperty.set(event);
                            event.consume();
                        }
                    }
                });
                newTagBlock.addEventHandler(MouseEvent.MOUSE_RELEASED, (event) -> {
                    if (newTagBlock.isCreationDone()) {
                        event.consume();
                        newTagBlock.getScene().setCursor(Cursor.DEFAULT);
                        newTagBlock.setState(TagState.SELECTED);
                        mouseProperty = null;
                    }
                });

                anchorPane.setLeftAnchor(newTagBlock, newTagBlock.getX() - newTagBlock.getTagWidthPadding());
                anchorPane.setTopAnchor(newTagBlock, newTagBlock.getY() - newTagBlock.getTagHeightPadding());
                anchorPane.getChildren().add(newTagBlock);
                selectedTagBlock = newTagBlock;
                mouseMovement = new MouseProperty(e);
                e.consume();
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
                    e.consume();
                }
            }
        });
        anchorPane.addEventHandler(MouseEvent.MOUSE_RELEASED, (e) -> {
            if (e.getButton() == MouseButton.PRIMARY && mouseMovement != null) {
                double biasX = e.getScreenX() - mouseMovement.getScreenX(), biasY = e.getScreenY() - mouseMovement.getScreenY();
                if (Math.abs(biasX) < 5 || Math.abs(biasY) < 5) {
                    anchorPane.getChildren().remove(selectedTagBlock);
                } else {
                    selectedTagBlock.updateBlock(anchorPane.getLeftAnchor(selectedTagBlock) + selectedTagBlock.getTagWidthPadding(), anchorPane.getTopAnchor(selectedTagBlock) + selectedTagBlock.getTagHeightPadding(), selectedTagBlock.getTagWidth(), selectedTagBlock.getTagHeight());
                    tagBlocks.add(selectedTagBlock);
                }
                selectedTagBlock.creationDone();
                selectedTagBlock = null;
                mouseMovement = null;
                e.consume();
            }
        });

        /*
         *按钮
         *用于打开图片文件
         */
        Button button = new Button();
        button.setText("Open");
        button.setOnAction((e) -> {
            String newUrl = getImage(primaryStage);
            if (newUrl != null) {
                Image newImage = new Image(newUrl);
                imageView.setImage(newImage);
                anchorPane.getChildren().removeAll(tagBlocks);
                tagBlocks.clear();
            }
        });

        /*
         *StackPane
         *由图片和标记叠加而成的StackPane
         */
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.TOP_LEFT);
        stackPane.getChildren().add(imageView);
        stackPane.getChildren().add(anchorPane);

        /*
         *网格布局GridPane
         *(0,0)(0,1)为菜单栏
         *(1,0)为工具栏
         *(1,1)为主操作界面
         */
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_LEFT);
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.add(button, 0, 0);
        gridPane.add(stackPane, 1, 1);

        /*
         *最底层的StackPane
         *用于放置网格布局GridPane以及添加后续弹窗等功能
         */
        StackPane root = new StackPane();
        root.getChildren().add(gridPane);

        /*
         *获取当前屏幕长宽
         *创建铺满屏幕的窗口
         */
        java.awt.Dimension scrSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        scene = new Scene(root, scrSize.getWidth() / 1.2, scrSize.getHeight() / 1.2);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private String getImage(Stage stage) {
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
        return file == null ? null : file.toURI().toString();
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