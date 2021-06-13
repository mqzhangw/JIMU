package jimu;

public interface Consts {

    String GROUP_ID = "io.github.leobert-lan";

    String jimu_plugin_name = "com.dd.comgradle";

    interface Artifacts {
        String router_anno_compiler = "jimu-router-anno-compiler";
    }

    interface Versions {
        String jimu_plugin = "1.3.6";
        String router_anno_compiler = "1.0.2";

        //todo
//        def ARTIFACT_ID = 'jimu-router-anno-compiler'
//        def VERSION_NAME = '1.0.1'
//        def GROUP_ID = 'io.github.leobert-lan'
    }

    interface Deps {
        String JIMU_PLUGIN = GROUP_ID + ":jimu-build-gradle:" + Versions.jimu_plugin;
        String JIMU_ROUTER_COMPILER = GROUP_ID + ":" + Artifacts.router_anno_compiler + ":" + Versions.router_anno_compiler;
    }

}