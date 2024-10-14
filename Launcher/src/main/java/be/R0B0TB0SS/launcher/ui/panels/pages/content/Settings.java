package be.R0B0TB0SS.launcher.ui.panels.pages.content;

import be.R0B0TB0SS.launcher.Launcher;
import be.R0B0TB0SS.launcher.ui.PanelManager;
import be.R0B0TB0SS.launcher.utils.translate.Translate;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.flowarg.flowupdater.utils.IOUtils;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

import java.io.InputStream;
import java.util.Objects;


public class Settings extends ContentPanel {
    private final Saver saver = Launcher.getInstance().getSaver();
    GridPane contentPane = new GridPane();


    @Override
    public String getName() {
        return "settings";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/settings.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        // Background
        this.layout.getStyleClass().add("settings-layout");
        this.layout.setPadding(new Insets(40));
        setCanTakeAllSize(this.layout);

        // Content
        contentPane.getStyleClass().add("content-pane");
        setCanTakeAllSize(contentPane);
        this.layout.getChildren().add(contentPane);

        // Titre
        Label title = new Label(Translate.getTranslate("setting.pagename"));
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
        title.getStyleClass().add("settings-title");
        setLeft(title);
        setCanTakeAllSize(title);
        setTop(title);
        title.setTextAlignment(TextAlignment.LEFT);
        title.setTranslateY(40d);
        title.setTranslateX(25d);
        contentPane.getChildren().add(title);

        // RAM
        Label ramLabel = new Label(Translate.getTranslate("setting.ram"));
        ramLabel.getStyleClass().add("settings-labels");
        setLeft(ramLabel);
        setCanTakeAllSize(ramLabel);
        setTop(ramLabel);
        ramLabel.setTextAlignment(TextAlignment.LEFT);
        ramLabel.setTranslateX(25d);
        ramLabel.setTranslateY(100d);
        contentPane.getChildren().add(ramLabel);
        // RAM Slider
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        int ram = (int) Math.ceil(memory.getTotal() / Math.pow(1024, 2));

        int maxramgo = ram / 1024 ;

        double tickun = maxramgo+1;
        double tickun2 = Math.ceil(tickun)/32 ;

        Slider slider = new Slider(1 , maxramgo,1);
        slider.getStyleClass().add("ram-selector");
        slider.setMajorTickUnit(tickun2);
        if(tickun2 < 1) {
            slider.setMinorTickCount(0);
        }else{slider.setMinorTickCount(1);}
        slider.setShowTickLabels(true);
        slider.setSnapToTicks(true);
        slider.setShowTickMarks(true);
        setCenterH(slider);

        int val = 2048;
        try {
            if (saver.get("maxRam") != null) {
                val = Integer.parseInt(saver.get("maxRam"));
                if (slider.getMax() >= val/1024.0) {
                    slider.setValue(val / 1024.0);
                } else {
                    slider.setValue(1);
                }
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException error) {
            saver.set("maxRam", String.valueOf(val));
            saver.save();
            if (slider.getMax() >= val/1024.0) {
                slider.setValue(val / 1024.0);
            } else {
                slider.setValue(1);
            }
        }
        setTop(slider);
        setCenterH(slider);
        slider.setTranslateY(150d);
        contentPane.getChildren().add(slider);


        //launcher pref
        Label launchpred = new Label(Translate.getTranslate("setting.pref"));
        launchpred.getStyleClass().add("settings-labels");
        setLeft(launchpred);
        setCanTakeAllSize(launchpred);
        setTop(launchpred);
        launchpred.setTextAlignment(TextAlignment.LEFT);
        launchpred.setTranslateX(25d);
        launchpred.setTranslateY(225d);
        contentPane.getChildren().add(launchpred);

        final ComboBox<String> priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll(
                Translate.getTranslate("setting.pref.hide"),
                     Translate.getTranslate("setting.pref.close")
        );
        priorityComboBox.getStyleClass().add("acc-selector");
        setLeft(priorityComboBox);
        setCanTakeAllSize(priorityComboBox);
        setTop(priorityComboBox);
        priorityComboBox.setTranslateX(35d);
        priorityComboBox.setTranslateY(250d);
        try {
            if(Objects.equals(saver.get("closeAfterLaunch"), "true")){
                priorityComboBox.setValue(Translate.getTranslate("setting.pref.close"));
            }else{
                priorityComboBox.setValue(Translate.getTranslate("setting.pref.hide"));
            }


        }catch (Exception e){
            priorityComboBox.setValue(Translate.getTranslate("setting.pref.hide"));
        }
        contentPane.getChildren().add(priorityComboBox);


        //langue
        Label laungetext = new Label(Translate.getTranslate("setting.lang"));
        laungetext.getStyleClass().add("settings-labels");
        setLeft(laungetext);
        setCanTakeAllSize(laungetext);
        setTop(laungetext);
        laungetext.setTextAlignment(TextAlignment.LEFT);
        laungetext.setTranslateX(25d);
        laungetext.setTranslateY(300d);
        contentPane.getChildren().add(laungetext);

        final ComboBox<String> langcombobox = new ComboBox<>();

        try {
            InputStream inputStream = Translate.class.getClassLoader().getResourceAsStream("lang/lang.json");
            JsonObject mainJsonObject = IOUtils.readJson(inputStream).getAsJsonObject();
            JsonArray langArray = (JsonArray) mainJsonObject.get("translation");

            for (Object o : langArray) {
                JsonObject versionNumber = (JsonObject) o;
                String lang = String.valueOf(versionNumber.get("language")).split("\"")[1];
                langcombobox.getItems().add(lang);

            }
        } catch (Exception e) {
            System.out.println(e);
        }
        langcombobox.getItems().addAll();
        langcombobox.getStyleClass().add("acc-selector");
        setLeft(langcombobox);
        setCanTakeAllSize(langcombobox);
        setTop(langcombobox);
        langcombobox.setTranslateX(35d);
        langcombobox.setTranslateY(325d);
        try {
            String language = saver.get("language");

            langcombobox.setValue(language);
        }catch (Exception e){
            langcombobox.setValue("English");

        }
        contentPane.getChildren().add(langcombobox);
        //alpha
        CheckBox alphaCheck = new CheckBox(Translate.getTranslate("setting.alpha.checkbox"));
        setCanTakeAllSize(alphaCheck);
        setCenterV(alphaCheck);
        alphaCheck.getStyleClass().add("login-mode-chk");
        alphaCheck.setMaxWidth(325d);
        alphaCheck.setTranslateY(90d);
        alphaCheck.setSelected(Objects.equals(saver.get("alphaCheck"), "true"));
        alphaCheck.selectedProperty().addListener((e, old, newValue) -> {
            if(Objects.equals(saver.get("alphaCheck"), "false")) {
                saver.set("alphaCheck", "true");
            }else{
                saver.set("alphaCheck", "false");
            }
        });
        contentPane.getChildren().add(alphaCheck);

        //version
        Label version = new Label(Translate.getTranslate("setting.version")+ Launcher.VERSION);
        version.getStyleClass().add("ver-labels");
        setRight(version);
        setCanTakeAllSize(version);
        setBottom(version);
        version.setTextAlignment(TextAlignment.RIGHT);
        version.setTranslateX(25d);
        version.setTranslateY(25d);
        contentPane.getChildren().add(version);

        /*
         * Save Button
         */
        Button saveBtn = new Button(Translate.getTranslate("btn.save"));
        saveBtn.getStyleClass().add("save-btn");
        final var iconView = new MaterialDesignIconView<>(MaterialDesignIcon.F.FLOPPY);
        iconView.getStyleClass().add("save-icon");
        saveBtn.setGraphic(iconView);
        setCanTakeAllSize(saveBtn);
        setBottom(saveBtn);
        setCenterH(saveBtn);
        saveBtn.setOnMouseClicked(e -> {
            double _val = Double.parseDouble(String.valueOf(slider.getValue()));
            _val *= 1024;
            saver.set("maxRam", String.valueOf((int) _val));

            String _val__ = String.valueOf(_val / 1024);
            this.logger.info("Now RAM is "+_val__+"Go");

            String pref = priorityComboBox.getValue();
            if(Objects.equals(priorityComboBox.getValue(), Translate.getTranslate("setting.pref.hide"))){
            saver.set("closeAfterLaunch", "false");}else{
                saver.set("closeAfterLaunch", "true");
            }
            this.logger.info("The Launcher pref is \"" + pref+ "\"");

            String lang = langcombobox.getValue();
            saver.set("language", lang);
            this.logger.info("The Launcher language is \"" + lang+ "\"");



            var iconViewd = new MaterialDesignIconView<>(MaterialDesignIcon.C.CHECK);
            iconViewd.getStyleClass().add("save-icon");
            saveBtn.setGraphic(iconViewd);

        });
        contentPane.getChildren().add(saveBtn);

    }


}