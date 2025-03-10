package be.R0B0TB0SS.launcher.ui.panels.pages.content;

import be.R0B0TB0SS.launcher.Launcher;
import be.R0B0TB0SS.launcher.ui.PanelManager;
import be.R0B0TB0SS.launcher.ui.panels.pages.App;
import be.R0B0TB0SS.launcher.utils.desktop.Notification;
import be.R0B0TB0SS.launcher.utils.instancesProfiles.InstanceData;
import be.R0B0TB0SS.launcher.utils.json.JsonUtils;
import be.R0B0TB0SS.launcher.utils.translate.Translate;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.awt.*;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Objects;

public class ManageInstances extends ContentPanel{

    GridPane contentPane = new GridPane();
    final ComboBox<String> instance = new ComboBox<>();
    final ComboBox<String> modLoader = new ComboBox<>();
    private final Saver saver = Launcher.getInstance().getSaver();
    private static final Path launcherDir = Launcher.getInstance().getLauncherDir();
    private static final Path gameDir = Path.of(launcherDir + "/versions/instances");
    private static final String instanceList = String.valueOf(Path.of(gameDir+"/instances.json"));
    TextField name = new TextField();
    TextField modloaderversion = new TextField();
    TextField projectID = new TextField();
    TextField fileID = new TextField();
    Button savebtn = new Button(Translate.getTranslate("btn.save"));
    Button delbtn = new Button(Translate.getTranslate("btn.delete"));
    CheckBox isCursforgemodpack = new CheckBox(Translate.getTranslate("mnginstance.curseforge.checkbox"));

    @Override
    public String getName() {
        return "createInstance";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/addInstance.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        // Background
        this.layout.getStyleClass().add("create-profile-layout");
        this.layout.setPadding(new Insets(40));
        setCanTakeAllSize(this.layout);

        // Content
        contentPane.getStyleClass().add("content-pane");
        setCanTakeAllSize(contentPane);
        this.layout.getChildren().add(contentPane);

        // Titre
        Label title = new Label(Translate.getTranslate("mnginstance.pagename"));
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
        title.getStyleClass().add("settings-title");
        setLeft(title);
        setCanTakeAllSize(title);
        setTop(title);
        title.setTextAlignment(TextAlignment.LEFT);
        title.setTranslateY(-20d);
        title.setTranslateX(50d);
        contentPane.getChildren().add(title);
        form();
        retour();
        InstanceField();
    }
    private void form(){
        name.setVisible(false);
        name.setTranslateY(-90d);
        name.setMaxWidth(200d);
        setCenterH(name);
        name.setPromptText(Translate.getTranslate("mnginstance.textfield.name"));

        modLoader.setVisible(false);
        modLoader.setTranslateY(-60d);
        setCenterH(modLoader);
        modLoader.setValue(Translate.getTranslate("mnginstance.modloader.select"));
        modLoader.getItems().addAll("Forge","Fabric","NeoForge");

        modloaderversion.setVisible(false);
        modloaderversion.setTranslateY(-30d);
        modloaderversion.setMaxWidth(200d);
        setCenterH(modloaderversion);
        modloaderversion.setPromptText(Translate.getTranslate("mnginstance.textfield.modloaderversion"));

        projectID.setVisible(false);
        projectID.setTranslateY(30d);
        projectID.setMaxWidth(200d);
        setCenterH(projectID);
        projectID.setPromptText(Translate.getTranslate("mnginstance.textfield.projectid"));

        fileID.setVisible(false);
        fileID.setTranslateY(60d);
        fileID.setMaxWidth(200d);
        setCenterH(fileID);
        fileID.setPromptText(Translate.getTranslate("mnginstance.textfield.fileid"));

        savebtn.setVisible(false);
        setCenterH(savebtn);
        savebtn.setTranslateY(90d);

        delbtn.setVisible(false);
        setRight(delbtn);
        setTop(delbtn);
        delbtn.setTranslateY(50d);
        delbtn.setTranslateX(-50d);

        isCursforgemodpack.setVisible(false);
        setCenterH(isCursforgemodpack);

        contentPane.getChildren().addAll(modLoader,fileID,projectID,name,savebtn,delbtn,isCursforgemodpack,modloaderversion);
    }

    private void retour(){
        Button back = new Button(Translate.getTranslate("btn.back"));
        back.setVisible(true);
        setCanTakeAllSize(back);
        back.setMaxWidth(60);
        setTop(back);
        back.setTranslateY(-18d);
        back.setTranslateX(-20d);
        back.getStyleClass().add("back-btn");
        this.contentPane.getChildren().add(back);
        back.setOnMouseClicked(e -> this.setPage(new Instances()));
    }
    private void setPage(ContentPanel panel) {
        if (App.currentPage instanceof Instances && ((Instances) App.currentPage).isDownloading()) {
            return;
        }

        App.navContent.getChildren().clear();
        if (panel != null) {
            App.navContent.getChildren().add(panel.getLayout());
            App.currentPage = panel;
            if (panel.getStylesheetPath() != null) {
                if (!panel.equals(new ManageInstances())){panelManager.getStage().getScene().getStylesheets().remove(getStylesheetPath());}

            }
            panel.init(panelManager);
            panel.onShow();
        }
    }
    /*private void refrech(ContentPanel panel){
        App.navContent.getChildren().clear();
        if (panel != null) {
            App.navContent.getChildren().add(panel.getLayout());
            App.currentPage = panel;
        panel.init(panelManager);
        panel.onShow();
        }
    }*/

    private void InstanceField() {
        String latID = null;
        try {
            JsonUtils.createFile();
            JsonReader reader = new JsonReader(new InputStreamReader(
                    new FileInputStream(instanceList)));
            JsonParser jsonParser = new JsonParser();
            JsonObject mainJsonObject = jsonParser.parse(reader).getAsJsonObject();

            instance.getItems().add(Translate.getTranslate("mnginstance.selector.create"));
            JsonArray versionArray = (JsonArray) mainJsonObject.get("instances");
            for (Object o : versionArray) {
                JsonObject versionnumber = (JsonObject) o;
                String id = String.valueOf(versionnumber.get("name")).split("\"")[1];
                instance.getItems().add(id);

            }
        } catch (Exception ex) {}

        instance.setVisible(true);
        setCanTakeAllSize(instance);
        setCenterV(instance);
        instance.setTranslateX(0d);
        setTop(instance);
        instance.setValue(Translate.getTranslate("mnginstance.selector.select"));
        instance.setMaxWidth(180);
        instance.setTranslateY(37d);
        instance.getStyleClass().add("version-combobox");
        this.contentPane.getChildren().add(instance);
        instance.setOnHidden(e -> {
            name.setVisible(false);
            modLoader.setVisible(false);
            modloaderversion.setVisible(false);
            modLoader.setValue(Translate.getTranslate("mnginstance.modloader.select"));
            projectID.setVisible(false);
            fileID.setVisible(false);
            savebtn.setVisible(false);
            delbtn.setVisible(false);
            if (!Objects.equals(instance.getValue(), Translate.getTranslate("mnginstance.selector.select"))) {
                this.logger.info(instance.getValue());
                name.setText("");
                name.setVisible(true);
                modLoader.setVisible(true);
                if (!Objects.equals(instance.getValue(),Translate.getTranslate("mnginstance.selector.create"))){
                    name.setText(instance.getValue());
                    modLoader.setValue(InstanceData.getValue("type",instance.getValue()));
                    modloaderversion.setText(InstanceData.getValue("modloaderVersion",instance.getValue()));
                    modloaderversion.setVisible(true);
                    delbtn.setVisible(true);
                    delbtn.setOnMouseClicked(em->{
                        JsonUtils.removeInstance(instance.getValue());
                        refrech(new ManageInstances());
                    });
                }
                modLoader.setOnHidden(ex ->{
                    if (!Objects.equals(modLoader.getValue(), Translate.getTranslate("mnginstance.modloader.select"))){
                        savebtn.setVisible(true);
                        isCursforgemodpack.setVisible(true);
                        modloaderversion.setText("");
                        modloaderversion.setVisible(true);
                    }
                    isCursforgemodpack.setOnMouseClicked(cl->{
                        if(isCursforgemodpack.isSelected()){
                            projectID.setText("");
                            fileID.setText("");
                            projectID.setVisible(true);
                            fileID.setVisible(true);
                        }else{
                            projectID.setVisible(false);
                            fileID.setVisible(false);
                        }
                    });
                    savebtn.setOnMouseClicked(ev->{
                        if (!JsonUtils.isNameUsed(name.getText()) && !Objects.equals(name.getText(), "" )){
                            if(!isCursforgemodpack.isSelected()){
                                System.out.println("test");
                                JsonUtils.addInstance(name.getText(),modLoader.getValue(),modloaderversion.getText());
                                refrech(new ManageInstances());
                            }else{
                                if(projectID.getText().matches("\\d+") && fileID.getText().matches("\\d+")){
                                    JsonUtils.addInstance(name.getText(),modLoader.getValue(),modloaderversion.getText(),projectID.getText(),fileID.getText());
                                    refrech(new ManageInstances());
                                }else{
                                    Notification.sendSystemNotification(Translate.getTranslate("mnginstance.save.curseforge.intError"), TrayIcon.MessageType.ERROR);
                                }
                            }
                        }else{
                            Notification.sendSystemNotification(Translate.getTranslate("mnginstance.save.general.nameUsed"), TrayIcon.MessageType.ERROR);
                        }

                    });
                });
            }
        });

    }

}
