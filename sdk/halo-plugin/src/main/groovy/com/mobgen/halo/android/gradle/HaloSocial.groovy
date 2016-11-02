package com.mobgen.halo.android.gradle

import org.gradle.api.GradleException
import org.gradle.api.Project;

/**
 * Social link to the libraries.
 */
public class HaloSocial {

    /**
     * The google client id.
     */
    String googleClient
    /**
     * The facebook client id.
     */
    String facebookClient

    /**
     * Configures the google authentication.
     * @param clientId The token to configure google.
     */
    public void google(String clientId){
        if(clientId && !googleClient){
            this.googleClient = clientId
        }
    }

    /**
     * Configures the facebook credentials.
     * @param clientId
     */
    public void facebook(String clientId){
        if(clientId && !facebookClient){
            this.facebookClient = clientId
        }
    }

    /**
     * Configures the dependencies.
     * @param project The project.
     * @param dependencyMode The dependency mode.
     * @param dependencyVersion The dependency version of the plugin.
     */
    public void configureDependencies(Project project, String dependencyMode, String dependencyVersion) {
        //TODO remove the exception once released
        throw new GradleException("Social sdk is still not released.")
        project.dependencies.add(dependencyMode, "${HaloPlugin.GROUP_NAME}:halo-social:${dependencyVersion}")

        if(facebookClient){
            project.dependencies.add(dependencyMode, "com.facebook.android:facebook-android-sdk:${Version.FACEBOOK_VERSION}")
        }

        if(googleClient){
            project.dependencies.add(dependencyMode, "com.google.android.gms:play-services-auth:${Version.GOOGLE_VERSION}")
        }
    }
}
