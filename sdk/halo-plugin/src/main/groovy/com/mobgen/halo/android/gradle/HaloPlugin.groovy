package com.mobgen.halo.android.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies

/**
 * Halo plugin that is in charge to manage dependencies and all the configuration that must be
 * applied to a project using the Halo framework.
 */
public class HaloPlugin implements Plugin<Project> {

    /**
     * Halo convention object name.
     */
    public static final String PLUGIN_NAME = "halo"
    public static final String GROUP_NAME = "com.mobgen.halo.android"

    private Project project
    private HaloExtensionV2 extensionV2;

    @Override
    public void apply(Project project) {
        this.project = project
        if (!Utils.isAndroidPlugin(project)) {
            throw new ProjectConfigurationException("You cannot apply the Halo plugin in a non android project.", null)
        }

        //Create the configuration
        extensionV2 = project.extensions.create(PLUGIN_NAME, HaloExtensionV2, project, Version.VERSION)

        //Configure the dependencies when we have them ready
        project.getGradle().addListener(new DependencyResolutionListener() {
            @Override
            void beforeResolve(ResolvableDependencies resolvableDependencies) {
                addGlobalDependencies()
                extensionV2.configureDependencies()
            }

            @Override
            void afterResolve(ResolvableDependencies resolvableDependencies) {
                project.getGradle().removeListener(this)
            }
        })

        project.getGradle().addProjectEvaluationListener( new ProjectEvaluationListener() {
            @Override
            void beforeEvaluate(Project project1) {
            }

            @Override
            void afterEvaluate(Project project1, ProjectState projectState) {
                extensionV2.validateConfiguration()
                extensionV2.configureTasks()
                project.getGradle().removeListener(this)
            }
        })
    }

    private void addGlobalDependencies() {
        //Ensure jcenter is available
        project.repositories.add(project.getRepositories().jcenter())

        //Version is injected using gradle
        project.dependencies.add("api", "com.mobgen.halo.android:halo-sdk:${Version.VERSION}")
    }
}