package com.mobgen.halo.android.gradle

import com.google.gms.googleservices.GoogleServicesPlugin
import com.neenbedankt.gradle.androidapt.AndroidAptPlugin
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil

/**
 * Contains the configuration for halo.
 */
public class HaloConfiguration {

    /**
     * Default name of the dependencies configuration container. This adds
     * the dependencies into compile.
     */
    public static final String DEPENDENCY_CONFIGURATION = "compile"

    /**
     * Client id.
     */
    String clientId
    /**
     * Client secret.
     */
    String clientSecret
    /**
     * Client id debug.
     */
    String clientIdDebug
    /**
     * Client secret debug.
     */
    String clientSecretDebug
    /**
     * The service configuration and dependencies.
     */
    HaloServices haloServices
    /**
     * The name for the configuration.
     */
    String name

    /**
     * Default configuration.
     */
    HaloConfiguration() {
        this("")
    }

    /**
     * Constructor for the NamedDomainObjectContainer.
     * @param name The name of the container.
     */
    HaloConfiguration(String name) {
        this.name = name
    }

    /**
     * The client id DSL.
     * @param clientId The client id.
     */
    public void clientId(String clientId) {
        this.clientId = clientId
    }

    /**
     * Client secret configuration DSL.
     * @param clientSecret The client secret.
     */
    public void clientSecret(String clientSecret) {
        this.clientSecret = clientSecret
    }

    /**
     * Client id debug configuration DSL.
     * @param clientIdDebug client id debug.
     */
    public void clientIdDebug(String clientIdDebug) {
        this.clientIdDebug = clientIdDebug
    }

    /**
     * Client secret debug configuration DSL.
     * @param clientSecretDebug The client secret.
     */
    public void clientSecretDebug(String clientSecretDebug) {
        this.clientSecretDebug = clientSecretDebug
    }

    /**
     * The services closure DSL.
     * @param closure The closure.
     */
    public void services(Closure closure) {
        if (haloServices == null) {
            haloServices = new HaloServices()
        }
        ConfigureUtil.configure(closure, haloServices)
    }

    /**
     * Validates the current configuration by ensuring minimal data is provided.
     */
    public void validate(Project project) {
        if (!((clientId && clientSecret) || (clientIdDebug && clientSecretDebug))) {
            throw new GradleException("HALO Plugin Error: Provide a clientId and a clientSecret or a clientIdDebug and a clientSecretDebug")
        }
        validateConfigurationName(project)
    }

    /**
     * Applies the plugins needed.
     * @param project The project.
     */
    public void plugIn(Project project) {
        if(haloServices && (haloServices.analyticsEnabled || haloServices.notificationsEnabled || haloServices.auth)){
            if (!project.plugins.hasPlugin(GoogleServicesPlugin)) {
                project.apply(plugin: GoogleServicesPlugin)
            }
        }
    }

    /**
     * Validates the config name for different variants.
     * @param project The project to check.
     */
    public void validateConfigurationName(Project project) {
        String configName = getConfigName()
        if (!project.configurations.findByName(configName)) {
            throw new GradleException("HALO Plugin Error: Error while configuring halo. The build variant name provided ${name} does not exist.")
        }
    }

    /**
     * Configures the current dependencies.
     * @param project The project to be configured.
     */
    public void configureDependencies(Project project) {
        validateConfigurationName(project)
        if (haloServices) {
            haloServices.configureDependencies(project, getConfigName(), Version.VERSION)
        }
    }

    /**
     * Provides the configuration name for the dependency management system.
     * @return The name of the configuration.
     */
    private String getConfigName() {
        String config = DEPENDENCY_CONFIGURATION;
        if (name) {
            config = "${name}${DEPENDENCY_CONFIGURATION.capitalize()}"
        }
        return config
    }
}
