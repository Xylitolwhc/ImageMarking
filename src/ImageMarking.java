import CustomUI.TagBlockControl;
import CustomUI.TagState;
import Properties.MouseProperty;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ImageMarking extends Application {
    public final static double SCREEN_WIDTH = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
            SCREEN_HEIGHT = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    private MouseProperty mouseMovement, mouseProperty, movePane;
    private Scene scene;
    private TagBlockControl selectedTagBlock;
    private List<TagBlockControl> tagBlocks = new ArrayList<>();
    private Path imagePath, xmlPath;
    private DoubleProperty imageWidth, imageHeight, zoomScale;
    private AnchorPane anchorPane;

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
        imageWidth = new SimpleDoubleProperty(image.getWidth());
        imageHeight = new SimpleDoubleProperty(image.getHeight());
        zoomScale = new SimpleDoubleProperty(1.0);
        zoomScale.addListener((arg0) -> {
            imageView.setFitWidth(imageWidth.get() * zoomScale.get());
            imageView.setFitHeight(imageHeight.get() * zoomScale.get());
        });
        /*
         *AnchorPane
         *标记和图片的叠加层
         */
        anchorPane = new AnchorPane();
        anchorPane.getChildren().add(imageView);
        anchorPane.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                createTagBlock(anchorPane, e.getX(), e.getY(), 0, 0, "");
                selectedTagBlock.getTextField().setVisible(false);
                mouseMovement = new MouseProperty(e);
            }
        });
        anchorPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
            if (e.getButton() == MouseButton.PRIMARY && mouseMovement != null) {
                double biasX = e.getScreenX() - mouseMovement.getScreenX(), biasY = e.getScreenY() - mouseMovement.getScreenY();
                selectedTagBlock.updateBlock(Math.abs(biasX), Math.abs(biasY));
                if (biasX >= 0 && biasY >= 0) {
                    anchorPane.setTopAnchor(selectedTagBlock, (selectedTagBlock.getY() - selectedTagBlock.getTagHeightPadding()) * zoomScale.get());
                    anchorPane.setLeftAnchor(selectedTagBlock, (selectedTagBlock.getX() - selectedTagBlock.getTagWidthPadding()) * zoomScale.get());
                } else if (biasX < 0 && biasY >= 0) {
                    anchorPane.setTopAnchor(selectedTagBlock, (selectedTagBlock.getY() - selectedTagBlock.getTagHeightPadding()) * zoomScale.get());
                    anchorPane.setLeftAnchor(selectedTagBlock, (selectedTagBlock.getX() + biasX - selectedTagBlock.getTagWidthPadding()) * zoomScale.get());
                } else if (biasX >= 0 && biasY < 0) {
                    anchorPane.setTopAnchor(selectedTagBlock, (selectedTagBlock.getY() + biasY - selectedTagBlock.getTagHeightPadding()) * zoomScale.get());
                    anchorPane.setLeftAnchor(selectedTagBlock, (selectedTagBlock.getX() - selectedTagBlock.getTagWidthPadding()) * zoomScale.get());
                } else if (biasX < 0 && biasY < 0) {
                    anchorPane.setTopAnchor(selectedTagBlock, (selectedTagBlock.getY() + biasY - selectedTagBlock.getTagHeightPadding()) * zoomScale.get());
                    anchorPane.setLeftAnchor(selectedTagBlock, (selectedTagBlock.getX() + biasX - selectedTagBlock.getTagWidthPadding()) * zoomScale.get());
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
                    moveTagBlock(selectedTagBlock, anchorPane, anchorPane.getLeftAnchor(selectedTagBlock) + selectedTagBlock.getTagWidthPadding() * zoomScale.get(), anchorPane.getTopAnchor(selectedTagBlock) + selectedTagBlock.getTagHeightPadding() * zoomScale.get());
                    selectedTagBlock.getTextField().setVisible(true);
                    selectedTagBlock.getTextField().requestFocus();
                    selectedTagBlock.creationDone();
                }
                mouseMovement = null;
            }
        });

        /*
         *用于控制整体移动的movableAnchorPane
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
                moveTagBlock(tagBlock, anchorPane, tagBlock.getX() * zoomScale.get(), tagBlock.getY() * zoomScale.get());
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
         *Open按钮
         *用于打开图片文件
         */
        Button buttonOpen = new Button("Open");
        buttonOpen.setOnAction((e) -> {
            File newFile = getImage(primaryStage);
            if (newFile != null) {
                anchorPane.getChildren().clear();
                tagBlocks.clear();
                try {
                    Image newImage = new Image(newFile.toURI().toString());
                    imageView.setImage(newImage);
                    if (!anchorPane.getChildren().contains(imageView)) {
                        anchorPane.getChildren().add(imageView);
                    }
                    imageWidth.set(newImage.getWidth());
                    imageHeight.set(newImage.getHeight());
                    movableAnchorPane.setTopAnchor(anchorPane, 0.0);
                    movableAnchorPane.setLeftAnchor(anchorPane, 0.0);
                    imagePath = newFile.toPath();
                    //判断是否有同名xml文件
                    int position = imagePath.toString().lastIndexOf(".");
                    File xmlFile = new File(imagePath.toString().substring(0, position + 1) + "xml");
                    xmlPath = xmlFile.toPath();
                    System.out.println(xmlFile.exists());
                    if (xmlFile.exists()) {
                        readXml(xmlFile);
                    }
                    zoomScale.set(1.0);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        buttonOpen.setPrefSize(200, 50);

        Button buttonSave = new Button("Save");
        buttonSave.setPrefSize(200, 50);
        buttonSave.setOnAction((e) -> {
            save(xmlPath);
        });

        /*
         *带滑动条的ScrollPane
         */
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(movableAnchorPane);
//        scrollPane.setBackground(
//                new Background(new BackgroundFill(Color.TRANSPARENT, null, null))
//        );
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

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
        gridPane.add(buttonSave, 1, 0);
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
                        showAlert(primaryStage);
                        save(xmlPath);
                    }
                }
            }
        });
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private void createTagBlock(AnchorPane anchorPane, double x, double y, double width, double height, String text) {
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

    private void delTagBlock(TagBlockControl tagBlock, AnchorPane anchorPane) {
        anchorPane.getChildren().remove(tagBlock);
        anchorPane.getChildren().remove(tagBlock.getTextField());
        tagBlocks.remove(tagBlock);
        selectedTagBlock = null;
    }

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
                selectedTagBlock = tagBlock;
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
                            moveTagBlock(tagBlock, anchorPane, tagBlock.getX() * zoomScale.get() + moveBiasX, tagBlock.getY() * zoomScale.get() + moveBiasY);
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
            }
        });
        tagBlock.zoomScaleProperty().bind(zoomScale);
    }

    private void moveTagBlock(TagBlockControl tagBlock, AnchorPane anchorPane, double x, double y) {
        tagBlock.updateBlockXY(x, y);
        anchorPane.setTopAnchor(tagBlock, (tagBlock.getY() - tagBlock.getTagHeightPadding()) * zoomScale.get());
        anchorPane.setLeftAnchor(tagBlock, (tagBlock.getX() - tagBlock.getTagWidthPadding()) * zoomScale.get());
        if (anchorPane.getChildren().contains(tagBlock.getTextField())) {
            anchorPane.setTopAnchor(tagBlock.getTextField(), (tagBlock.getY() + tagBlock.getTagHeight() + tagBlock.getTagHeightPadding()) * zoomScale.get());
            anchorPane.setLeftAnchor(tagBlock.getTextField(), (tagBlock.getX() + tagBlock.getTagWidth() + tagBlock.getTagWidthPadding()) * zoomScale.get());
        }
    }

    private void resizeTagBlock(TagBlockControl tagBlock, AnchorPane anchorPane, double width, double height) {
        tagBlock.updateBlock(width, height);
        anchorPane.setTopAnchor(tagBlock.getTextField(), (tagBlock.getY() + tagBlock.getTagHeight() + tagBlock.getTagHeightPadding()) * zoomScale.get());
        anchorPane.setLeftAnchor(tagBlock.getTextField(), (tagBlock.getX() + tagBlock.getTagWidth() + tagBlock.getTagWidthPadding()) * zoomScale.get());
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

    private void save(Path path) {
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
                x.setTextContent(tagBlock.getX().toString());
                Element y = document.createElement("y");
                y.setTextContent(tagBlock.getY().toString());
                Element width = document.createElement("width");
                width.setTextContent(tagBlock.getTagWidth().toString());
                Element height = document.createElement("height");
                height.setTextContent(tagBlock.getTagHeight().toString());
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
                tagBlockElement.appendChild(lineColor);
                tagBlockElement.appendChild(pointColor);
                tagBlockElement.appendChild(text);
                tagBlocksElement.appendChild(tagBlockElement);
            }
            document.appendChild(tagBlocksElement);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(document), new StreamResult(path.toFile()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                Color lineColor = Color.valueOf(element.getElementsByTagName("lineColor").item(0).getTextContent());
                Color pointColor = Color.valueOf(element.getElementsByTagName("pointColor").item(0).getTextContent());
                String text = element.getElementsByTagName("text").item(0).getTextContent();
                createTagBlock(anchorPane, x, y, width, height, text);
                selectedTagBlock.setLineColor(lineColor);
                selectedTagBlock.setPointColor(pointColor);
                tagBlocks.add(selectedTagBlock);
                System.out.println(element);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Optional<ButtonType> showAlert(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "  ",
                new ButtonType("不保存", ButtonBar.ButtonData.NO),
                new ButtonType("保存", ButtonBar.ButtonData.YES),
                new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE));
        alert.setTitle("");
        alert.setHeaderText("当前修改内容还未保存,是否保存？");
        alert.initOwner(stage);
        return alert.showAndWait();
    }

    public static void main(String... args) {
        launch(args);
    }

}