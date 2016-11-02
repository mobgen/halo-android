package com.mobgen.halo.android.gradle.tasks

import com.android.build.gradle.api.BaseVariant
import com.google.common.base.Charsets
import com.google.common.io.Files
import com.mobgen.halo.android.gradle.HaloConfiguration
import com.mobgen.halo.android.gradle.HaloExtensionV2
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Resource generation task to generate some resources with the ids of halo.
 */
public class HaloResourceGenerationTask extends DefaultTask {

    @Input
    public HaloExtensionV2 extension

    @Input
    public BaseVariant androidVariant

    @OutputDirectory
    public File intermediateDir

    @TaskAction
    public void action() {
        deleteFolder(intermediateDir)
        if (!intermediateDir.mkdirs()) {
            throw new GradleException("Failed to create folder: " + intermediateDir);
        }

        Map<String, String> valuesInjected = new TreeMap<String, String>();
        Map<String, Map<String, String>> resAttributes = new TreeMap<String, Map<String, String>>();
        insertVariables(valuesInjected)
        File values = new File(intermediateDir, "values");
        if (!values.exists() && !values.mkdirs()) {
            throw new GradleException("Failed to create folder: " + values);
        }
        Files.write(getValuesContent(valuesInjected, resAttributes), new File(values, "values.xml"), Charsets.UTF_8);
    }

    void insertVariables(TreeMap<String, String> data) {
        HaloConfiguration config = extension.getConfigurationForName(androidVariant.getName())
        data.put("halo_client_id", config.clientId)
        data.put("halo_secret_id", config.clientSecret)
        data.put("halo_client_id_debug", config.clientIdDebug)
        data.put("halo_secret_id_debug", config.clientSecretDebug)
        if(config.haloServices){
            data.put("halo_notifications_enabled", String.valueOf(config.haloServices.notificationsEnabled))
            data.put("halo_analytics_enabled", String.valueOf(config.haloServices.analyticsEnabled))
            data.put("halo_presenter_enabled", String.valueOf(config.haloServices.presenterEnabled))
            data.put("halo_translations_enabled", String.valueOf(config.haloServices.translationsEnabled))
            data.put("halo_content_enabled", String.valueOf(config.haloServices.contentEnabled))
            if(config.haloServices.social){
                data.put("halo_social_google_client", config.haloServices.social.google)
                data.put("halo_social_facebook_client", config.haloServices.social.facebook)
            }
        }
    }

    private static void deleteFolder(final File folder) {
        if (!folder.exists()) {
            return;
        }
        File[] files = folder.listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    if (!file.delete()) {
                        throw new GradleException("Failed to delete: " + file);
                    }
                }
            }
        }
        if (!folder.delete()) {
            throw new GradleException("Failed to delete: " + folder);
        }
    }

    private static String getValuesContent(Map<String, String> values,
                                           Map<String, Map<String, String>> attributes) {
        StringBuilder sb = new StringBuilder(256);

        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources>\n");

        for (Map.Entry<String, String> entry : values.entrySet()) {
            String name = entry.getKey();
            sb.append("    <string name=\"").append(name).append("\" translatable=\"false\"");
            if (attributes.containsKey(name)) {
                for (Map.Entry<String, String> attr : attributes.get(name).entrySet()) {
                    sb.append(" ").append(attr.getKey()).append("=\"")
                            .append(attr.getValue()).append("\"");
                }
            }
            sb.append(">").append(entry.getValue()).append("</string>\n");
        }

        sb.append("</resources>\n");

        return sb.toString();
    }
}
