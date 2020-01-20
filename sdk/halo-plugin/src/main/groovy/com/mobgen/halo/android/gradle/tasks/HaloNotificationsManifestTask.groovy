package com.mobgen.halo.android.gradle.tasks

import com.android.build.gradle.api.BaseVariant
import groovy.xml.Namespace
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Task to write the halo configuration file to the system.
 */
public class HaloNotificationsManifestTask extends DefaultTask {

    BaseVariant androidVariant

    @TaskAction
    onProcessManifests() {
        String manifestLocation = getManifestLocation()
        def xml = new XmlParser().parse(manifestLocation)
        appendPermissions(xml)
        appendServices(xml)

        //Add the permissions
        XmlNodePrinter printer = new XmlNodePrinter(new PrintWriter(new FileWriter(manifestLocation)))
        printer.setPreserveWhitespace(true)
        printer.print(xml)
    }

    private String getManifestLocation() {
        def manifestLocation = "${getProject().getBuildDir()}/intermediates/manifests/full"
        if (androidVariant.getFlavorName() != null) {
            manifestLocation += "/${androidVariant.getFlavorName()}"
        }
        manifestLocation += "/${androidVariant.getBuildType().getName()}/AndroidManifest.xml"
        return manifestLocation
    }

    static def appendPermissions(Node xml) {
        String internetPermission = "<uses-permission " +
                "xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "android:name=\"android.permission.INTERNET\" />"
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

    static def appendServices(Node xml) {
        if (xml.application && xml.application[0]) {
            String gcmService = "<service\n" +
                    "xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                    "            android:name=\"com.mobgen.halo.android.notifications.services.NotificationService\"\n" +
                    "            android:exported=\"false\" >\n" +
                    "            <intent-filter>\n" +
                    "                <action android:name=\"com.google.firebase.MESSAGING_EVENT\" />\n" +
                    "            </intent-filter>\n" +
                    "        </service>"
            String instanceIdService = "<service\n" +
                    "xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                    "            android:name=\"com.mobgen.halo.android.notifications.services.InstanceIDService\"\n" +
                    "            android:exported=\"false\" >\n" +
                    "            <intent-filter>\n" +
                    "                <action android:name=\"com.google.firebase.INSTANCE_ID_EVENT\" />\n" +
                    "            </intent-filter>\n" +
                    "        </service>"
            xml.application[0].append(new XmlParser().parseText(gcmService))
            xml.application[0].append(new XmlParser().parseText(instanceIdService))
        }
    }
}
