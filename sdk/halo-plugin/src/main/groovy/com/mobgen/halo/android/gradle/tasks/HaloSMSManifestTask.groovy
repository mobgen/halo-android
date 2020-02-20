package com.mobgen.halo.android.gradle.tasks

import com.android.build.gradle.api.BaseVariant
import groovy.xml.Namespace
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Task to write the halo configuration file to the system.
 */
public class HaloSMSManifestTask extends DefaultTask {

    BaseVariant androidVariant

    @TaskAction
    public onProcessManifests() {
        String manifestLocation = getManifestLocation()
        def xml = new XmlParser().parse(manifestLocation)
        appendPermissions(xml)

        //Add the permissions
        XmlNodePrinter printer = new XmlNodePrinter(new PrintWriter(new FileWriter(manifestLocation)))
        printer.setPreserveWhitespace(true)
        printer.print(xml)
    }

    private String getManifestLocation() {
        def manifestLocation = "${getProject().getBuildDir()}/intermediates/merged_manifests"
        if (androidVariant.getFlavorName() != null) {
            manifestLocation += "/${androidVariant.getFlavorName()}"
        }
        manifestLocation += "${androidVariant.getBuildType().getName().capitalize()}/AndroidManifest.xml"
        return manifestLocation
    }

    static def appendPermissions(Node xml) {
        String internetPermission = "<uses-permission " +
                "xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "android:name=\"android.permission.RECEIVE_SMS\" />"
        xml.append(new XmlParser().parseText(internetPermission))

        //Remove repeated node items
        def androidNS = new Namespace("http://schemas.android.com/apk/res/android", "android")
        List<String> nodePermissions = new ArrayList<>()
        ((NodeList) xml.get("uses-permission")).each { node ->
            String permission = node.attribute(androidNS.name)
            if (nodePermissions.contains(permission)) {
                xml.remove(node)
            } else {
                nodePermissions.add(permission)
            }
        }
    }
}
