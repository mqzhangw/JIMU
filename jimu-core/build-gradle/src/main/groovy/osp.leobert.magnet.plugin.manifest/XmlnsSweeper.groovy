package osp.leobert.magnet.plugin.manifest

import osp.leobert.magnet.Log

class XmlnsSweeper {

    static final String TEMP_NS_PREFIX = "TEMP_NS_PREFIX"
    static final String STANDARD_NS_PREFIX = "xmlns"
//    static final String ANDROID_MANIFEST_NAME = "AndroidManifest.xml"

    static final List<String> xmlnsShouldSweep = ["android=\"http://schemas.android.com/apk/res/android\""]

    XmlnsSweeper() {
    }

    static void sweep(String processManifestOutputFilePath) {

        File manifestFile = new File(processManifestOutputFilePath)
        Log.info("start sweep manifest xmlns at " + processManifestOutputFilePath)
        if (!manifestFile.exists() /*|| manifestFile.name != ANDROID_MANIFEST_NAME*/) {
            return
        }
        String manifestFileContent = manifestFile.text
        Log.info("\r\n\r\nstart sweep manifest xmlns:\r\n" + manifestFileContent)
        StringBuilder builder = new StringBuilder(manifestFileContent)

        xmlnsShouldSweep.each { String xmlnsItem ->

            String fullName = STANDARD_NS_PREFIX + ":" + xmlnsItem

            // maintain first xmlns
            int firstIndex = builder.indexOf(fullName)
            if (firstIndex < 0) {
                return
            }
            builder.replace(firstIndex, firstIndex + STANDARD_NS_PREFIX.length(), TEMP_NS_PREFIX)

            // replace all except first xmlns
            replaceAll(builder, fullName, "")

            // recovery first item
            replaceAll(builder, TEMP_NS_PREFIX, STANDARD_NS_PREFIX)

        }

        manifestFile.text = builder.toString()
    }

    static void replaceAll(StringBuilder sb, String regex, String replacement) {
        String aux = sb.toString()
        aux = aux.replaceAll(regex, replacement)
        sb.setLength(0)
        sb.append(aux)
    }


}
