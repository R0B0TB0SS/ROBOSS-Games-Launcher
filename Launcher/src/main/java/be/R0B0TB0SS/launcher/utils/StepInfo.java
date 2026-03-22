package be.R0B0TB0SS.launcher.utils;

import be.R0B0TB0SS.launcher.utils.translate.Translate;

public enum StepInfo {
    READ(Translate.getTranslate("step.read")),
    DL_LIBS(Translate.getTranslate("step.dl_libs")),
    DL_ASSETS(Translate.getTranslate("step.dl_assets")),
    EXTRACT_NATIVES(Translate.getTranslate("step.extract_natives")),
    FORGE(Translate.getTranslate("step.forge")),
    FABRIC(Translate.getTranslate("step.fabric")),
    MODS(Translate.getTranslate("step.mods")),
    EXTERNAL_FILES(Translate.getTranslate("step.external_files")),
    POST_EXECUTIONS(Translate.getTranslate("step.post_executions")),
    MOD_LOADER(Translate.getTranslate("step.mod_loader")),
    INTEGRATION(Translate.getTranslate("step.mods.integration")),
    MOD_PACK(Translate.getTranslate("step.mod_pack")),
    END(Translate.getTranslate("step.end"));

    final String details;

    StepInfo(String details) {
        this.details = details;
    }

    public String getDetails() {
        return this.details;
    }
}
