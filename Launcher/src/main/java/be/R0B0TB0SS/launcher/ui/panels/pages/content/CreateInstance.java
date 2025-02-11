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

public class CreateInstance extends ContentPanel{

    GridPane contentPane = new GridPane();
    final ComboBox<String> instance = new ComboBox<>();
    final ComboBox<String> modLoader = new ComboBox<>();
    private final Saver saver = Launcher.getInstance().getSaver();
    private static final Path launcherDir = Launcher.getInstance().getLauncherDir();
    private static final Path gameDir = Path.of(launcherDir + "/versions/modded");
    private static final String instanceList = String.valueOf(Path.of(gameDir+"/instances.json"));
    TextField name = new TextField();
    TextField projectID = new TextField();
    TextField fileID = new TextField();
    Button savebtn = new Button(Translate.getTranslate("btn.save"));
    Button delbtn = new Button(Translate.getTranslate("btn.delete"));
    CheckBox isCursforgemodpack = new CheckBox(Translate.getTranslate("instances.curseforge.checkbox"));

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
        Label title = new Label(Translate.getTranslate("instance.pagename"));
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
        name.setTranslateY(-50d);
        name.setMaxWidth(200d);
        setCenterH(name);
        name.setPromptText(Translate.getTranslate("instance.textfield.name"));

        modLoader.setVisible(false);
        modLoader.setTranslateY(-30d);
        setCenterH(modLoader);
        modLoader.setValue(Translate.getTranslate("instance.modloader.select"));
        modLoader.getItems().addAll("Forge","Fabric","NeoForge");

        projectID.setVisible(false);
        projectID.setTranslateY(-10d);
        projectID.setMaxWidth(200d);
        setCenterH(projectID);
        projectID.setPromptText(Translate.getTranslate("instance.textfield.projectid"));

        fileID.setVisible(false);
        fileID.setTranslateY(10d);
        fileID.setMaxWidth(200d);
        setCenterH(fileID);
        fileID.setPromptText(Translate.getTranslate("instance.textfield.fileid"));

        savebtn.setVisible(false);
        setCenterH(savebtn);

        delbtn.setVisible(false);
        setRight(delbtn);
        setTop(delbtn);
        delbtn.setTranslateY(50d);
        delbtn.setTranslateX(-50d);

        isCursforgemodpack.setVisible(false);

        contentPane.getChildren().addAll(modLoader,fileID,projectID,name,savebtn,delbtn,isCursforgemodpack);
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
        back.setOnMouseClicked(e -> this.setPage(new Modded()));
    }
    private void setPage(ContentPanel panel) {
        if (App.currentPage instanceof Modded && ((Modded) App.currentPage).isDownloading()) {
            return;
        }

        App.navContent.getChildren().clear();
        if (panel != null) {
            App.navContent.getChildren().add(panel.getLayout());
            App.currentPage = panel;
            if (panel.getStylesheetPath() != null) {
                if (!panel.equals(new CreateInstance())){panelManager.getStage().getScene().getStylesheets().remove(getStylesheetPath());}

            }
            panel.init(panelManager);
            panel.onShow();
        }
    }
    private void refrech(ContentPanel panel){
        App.navContent.getChildren().clear();
        if (panel != null) {
            App.navContent.getChildren().add(panel.getLayout());
            App.currentPage = panel;
        panel.init(panelManager);
        panel.onShow();
        }
    }

    private void InstanceField() {
        String latID = null;
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(
                    new FileInputStream(instanceList)));
            JsonParser jsonParser = new JsonParser();
            JsonObject mainJsonObject = jsonParser.parse(reader).getAsJsonObject();

            instance.getItems().add(Translate.getTranslate("instance.selector.create"));
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
        instance.setValue(Translate.getTranslate("instance.selector.select"));
        instance.setMaxWidth(180);
        instance.setTranslateY(37d);
        instance.getStyleClass().add("version-combobox");
        this.contentPane.getChildren().add(instance);
        instance.setOnHidden(e -> {
            name.setVisible(false);
            modLoader.setVisible(false);
            modLoader.setValue(Translate.getTranslate("instance.modloader.select"));
            projectID.setVisible(false);
            fileID.setVisible(false);
            savebtn.setVisible(false);
            delbtn.setVisible(false);
            if (!Objects.equals(instance.getValue(), Translate.getTranslate("instance.selector.select"))) {
                this.logger.info(instance.getValue());
                name.setText("");
                name.setVisible(true);
                modLoader.setValue(Translate.getTranslate("instance.modloader.select"));
                modLoader.setVisible(true);
                modLoader.setDisable(false);
                if (!Objects.equals(instance.getValue(),Translate.getTranslate("instance.selector.create"))){
                    name.setText(instance.getValue());
                    modLoader.setDisable(true);
                    modLoader.setValue(InstanceData.getValue("type",instance.getValue()));
                    delbtn.setVisible(true);
                    delbtn.setOnMouseClicked(em->{
                        JsonUtils.removeInstance(instance.getValue());
                        refrech(new CreateInstance());
                    });
                }
                modLoader.setOnHidden(ex ->{
                    /*if(Objects.equals(modLoader.getValue(),"CurseForge")){
                        projectID.setText("");
                        fileID.setText("");
                        projectID.setVisible(true);
                        fileID.setVisible(true);
                    }*/
                    savebtn.setVisible(true);
                    isCursforgemodpack.setVisible(true);
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
                        if (!JsonUtils.isNameUsed(name.getText())){
                            if(projectID.getText().matches("\\d+") && fileID.getText().matches("\\d+") ){
                                JsonUtils.addInstance(name.getText(),modLoader.getValue(),"",projectID.getText(),fileID.getText());
                                refrech(new CreateInstance());
                            }else{
                                Notification.sendSystemNotification(Translate.getTranslate("instance.save.curseforge.intError"), TrayIcon.MessageType.ERROR);
                            }
                        }else{
                            Notification.sendSystemNotification(Translate.getTranslate("instance.save.general.nameUsed"), TrayIcon.MessageType.ERROR);
                        }

                    });
                });
            }
        });

    }

}
