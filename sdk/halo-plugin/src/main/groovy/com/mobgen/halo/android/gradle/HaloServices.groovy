package com.mobgen.halo.android.gradle

import com.google.gms.googleservices.GoogleServicesPlugin
import com.neenbedankt.gradle.androidapt.AndroidAptPlugin
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil

/**
 * Services that can be enabled for halo.
 */
public class HaloServices {

    boolean analyticsEnabled
    boolean notificationsEnabled
    boolean translationsEnabled
    boolean contentEnabled
    boolean presenterEnabled
    HaloSocial social

    /**
     * Enables the analytics services.
     * @param enabled True if enabled. False otherwise.
     */
    public void analytics(boolean enabled) {
        this.analyticsEnabled = enabled
    }

    /**
     * Enables the push notifications service.
     * @param enabled True if enabled. False otherwise.
     */
    public void notifications(boolean enabled) {
        this.notificationsEnabled = enabled
    }

    /**
     * Enables the translations library.
     * @param enabled Translations library.
     */
    public void translations(boolean enabled) {
        this.translationsEnabled = true
    }

    /**
     * Enables the presenter library.
     * @param enabled Presenter library.
     */
    public void presenter(boolean enabled) {
        this.presenterEnabled = true
    }

    /**
     * The social closure.
     * @param closure The closure.
     */
    public void social(Closure closure) {
        if (social == null) {
            social = new HaloSocial(configuration)
        }
        ConfigureUtil.configure(closure, social)
    }

    /**
     * Enanles the content api for halo.
     * @param enabled True to enable the content api, false otherwise.
     */
    public void content(boolean enabled) {
        this.contentEnabled = true
    }

    /**
     * Configures the dependencies based on the services.
     * @param project The project to configure the dependencies on.
     * @param dependencyMode The dependency mode based on the name of the android variant.
     * @param dependencyVersion The version of the dependency.
     */
    public void configureDependencies(Project project, String dependencyMode, String dependencyVersion) {
        if (presenterEnabled) {
            project.dependencies.add(dependencyMode, "${HaloPlugin.GROUP_NAME}:halo-presenter:${dependencyVersion}")
        }

        if (translationsEnabled) {
            contentEnabled = true
            project.dependencies.add(dependencyMode, "${HaloPlugin.GROUP_NAME}:halo-translations:${dependencyVersion}")
        }

        if (analyticsEnabled) {
            project.dependencies.add(dependencyMode, "${HaloPlugin.GROUP_NAME}:halo-analytics:${dependencyVersion}")
        }

        if (notificationsEnabled) {
            project.dependencies.add(dependencyMode, "${HaloPlugin.GROUP_NAME}:halo-notifications:${dependencyVersion}")
        }

        if (social) {
            social.configureDependencies(project, dependencyMode, dependencyVersion)
        }

        if (contentEnabled) {
            String loganVersion = "1.3.6";
            project.dependencies.add("apt", "com.bluelinelabs:logansquare-compiler:${loganVersion}")
            project.dependencies.add(dependencyMode, "${HaloPlugin.GROUP_NAME}:halo-content:${dependencyVersion}")
        }
    }
}
