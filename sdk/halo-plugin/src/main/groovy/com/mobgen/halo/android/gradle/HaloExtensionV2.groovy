package com.mobgen.halo.android.gradle

import com.mobgen.halo.android.gradle.tasks.HaloNotificationsManifestTask
import com.mobgen.halo.android.gradle.tasks.HaloSMSManifestTask
import com.mobgen.halo.android.gradle.tasks.HaloResourceGenerationTask
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Extension that allows us to configure halo in a very simple way without cluttering
 * with dependencies and scopes. It integrates with firebase and with the different
 * plugins of HALO.
 */
public class HaloExtensionV2 {

    Project project
    String dependenciesVersion
    HaloConfiguration defaultConfiguration
    NamedDomainObjectContainer<HaloConfiguration> configByVariant

    public HaloExtensionV2(Project project, String dependenciesVersion) {
        this.project = project
        this.dependenciesVersion = dependenciesVersion
        this.configByVariant = project.container(HaloConfiguration)
    }

    public void clientId(String clientId) {
        createDefaultIfNotExists()
        defaultConfiguration.clientId(clientId)
    }

    public void clientSecret(String clientSecret) {
        createDefaultIfNotExists()
        defaultConfiguration.clientSecret(clientSecret)
    }

    public void clientIdDebug(String clientIdDebug) {
        createDefaultIfNotExists()
        defaultConfiguration.clientIdDebug(clientIdDebug)
    }

    public void clientSecretDebug(String clientSecretDebug) {
        createDefaultIfNotExists()
        defaultConfiguration.clientSecretDebug(clientSecretDebug)
    }

    public void services(Closure closure) {
        createDefaultIfNotExists()
        defaultConfiguration.services(closure)
    }

    public void androidVariants(Closure closure) {
        configByVariant.configure(closure)
    }

    public HaloConfiguration getConfigurationForName(String variantName) {
        HaloConfiguration configuration = null
        if (defaultConfiguration) {
            configuration = defaultConfiguration
        } else {
            for (int i = 0; i < configByVariant.size(); i++) {
                if (variantName.contains(configByVariant.getAt(i).name) && !configuration) {
                    configuration = configByVariant.getAt(i)
                    break
                }
            }
        }
        if (!configuration) {
            throw GradleException("HALO Plugin Error: The variant requested has no configuration provided: ${variantName}")
        }
        return configuration
    }

    protected void validateConfiguration() {
        if (defaultConfiguration && !configByVariant.isEmpty()) {
            throw new GradleException("HALO Plugin Error: You cannot provide variant config and default config at the same time. They cannot be overridden.")
        }
        if (defaultConfiguration) {
            defaultConfiguration.validate(project)
            defaultConfiguration.plugIn(project)
        } else if (!configByVariant.isEmpty()) {
            configByVariant.all { it ->
                it.validate(project)
                it.plugIn(project)
            }
        } else {
            throw new GradleException("HALO Plugin Error: Halo configuration has not been provided. Please add some info to the halo { } configuration.")
        }
    }

    protected void configureDependencies() {
        if (defaultConfiguration) {
            defaultConfiguration.configureDependencies(project)
        } else {
            configByVariant.all { it ->
                it.configureDependencies(project)
            }
        }
    }

    protected void configureTasks() {
        configureNotificationsManifestTask()
        configureSMSManifestTask()
        configureResourceGenerationTask()
    }

    private void createDefaultIfNotExists() {
        if (!defaultConfiguration) {
            defaultConfiguration = new HaloConfiguration()
        }
    }

    private void configureNotificationsManifestTask() {
        Utils.getVariants(project).all { variant ->
            HaloConfiguration config = getConfigurationForName(variant.getName())
            if (config.haloServices && config.haloServices.notificationsEnabled || config.haloServices.twoFactorAuth && config.haloServices.twoFactorAuth.pushNotificationAuth) {
                //Create push notifications task to add to the manifest the permissions
                Task task = project.tasks.create("${variant.getName()}PushManifest", HaloNotificationsManifestTask, {
                    androidVariant = variant
                    group = HaloPlugin.PLUGIN_NAME
                    description = "Process the manifest to inject the push information for Halo"
                })
                //Inject the task
                project.tasks.getByName("process${variant.getName().capitalize()}Manifest") {
                    doLast {
                        task.execute()
                    }
                }
            }
        }
    }

    private void configureSMSManifestTask() {
        Utils.getVariants(project).all { variant ->
            HaloConfiguration config = getConfigurationForName(variant.getName())
            if (config.haloServices && config.haloServices.twoFactorAuth && config.haloServices.twoFactorAuth.smsNotificationAuth) {
                //Create push notifications task to add to the manifest the permissions
                Task task = project.tasks.create("${variant.getName()}PushManifestSMS", HaloSMSManifestTask, {
                    androidVariant = variant
                    group = HaloPlugin.PLUGIN_NAME
                    description = "Process the manifest to inject the sms information"
                })
                //Inject the task
                project.tasks.getByName("process${variant.getName().capitalize()}Manifest") {
                    doLast {
                        task.execute()
                    }
                }
            }
        }
    }

    private void configureResourceGenerationTask() {
        Utils.getVariants(project).all { variant ->
            File outputDir = project.file("$project.buildDir/generated/res/halo-services/$variant.dirName")
            HaloResourceGenerationTask task = project.tasks.create("process${variant.name.capitalize()}HaloConfig", HaloResourceGenerationTask)
            task.extension = this
            task.intermediateDir = outputDir
            task.androidVariant = variant
            variant.registerResGeneratingTask(task, outputDir)
        }
    }
}
