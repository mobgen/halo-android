package com.mobgen.halo.android.gradle

import org.gradle.api.Project

/**
 * Two factor authentication link to the libraries.
 */
public class HaloTwoFactorAuth {

    Boolean pushNotificationAuth
    Boolean smsNotificationAuth

    /**
     * Enable push notification two factor
     * @param push
     */
    public void push(Boolean push){
        pushNotificationAuth = push
    }

    /**
     * Enable sms two factor notification
     * @param sms
     */
    public void sms(Boolean sms){
        smsNotificationAuth = sms
    }

    /**
     * Configures the dependencies.
     * @param project The project.
     * @param dependencyMode The dependency mode.
     * @param dependencyVersion The dependency version of the plugin.
     */
    public void configureDependencies(Project project, String dependencyMode, String dependencyVersion) {
        if(pushNotificationAuth || smsNotificationAuth) {
            project.dependencies.add(dependencyMode, "${HaloPlugin.GROUP_NAME}:halo-twofactor:${dependencyVersion}")
        }
    }
}
