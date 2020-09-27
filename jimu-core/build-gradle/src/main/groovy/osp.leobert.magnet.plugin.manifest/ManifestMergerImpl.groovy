package osp.leobert.magnet.plugin.manifest

import org.slf4j.Logger
import osp.leobert.magnet.Log


class ManifestMergerImpl {

    Node manifestRoot

    Node fromManifestRoot

    boolean dest(String destPath) {
        dest(new File(destPath))
    }

    boolean dest(File destFile) {
        new XmlNodePrinter(new PrintWriter(new FileWriter(destFile))).print(manifestRoot)
    }

    ManifestMergerImpl(File manifestFile, File fromManifestFile) {
        try {
            manifestRoot = new XmlParser().parse(manifestFile.absolutePath)
        } catch (Exception e) {
            e.printStackTrace()
            throw new Exception("ManifestMergerImpl: ${manifestFile.absolutePath} file parsed error!")
        }

        try {
            fromManifestRoot = new XmlParser().parse(fromManifestFile.absolutePath)
        } catch (Exception e) {
            e.printStackTrace()
            throw new Exception("ManifestMergerImpl: ${fromManifestFile.absolutePath} file parsed error!")
        }
    }

    ManifestMergerImpl(String manifestPath, String fromManifestPath) {
        this(new File(manifestPath), new File(fromManifestPath))
    }

    void merge(Logger logger) {
        doMerge(fromManifestRoot, manifestRoot, logger)
    }


    private static void doMerge(Node manifestRoot, Node targetManifestNode, Logger logger) {
        targetManifestNode.attributes().put("xmlns:android","http://schemas.android.com/apk/res/android")
        NodeList targetChildren = targetManifestNode.children()
        Node fromApplicationNode = null
        Node targetApplicationNode = null

        for (Node child : targetChildren) {
            if (child.name().toString() == "application") {
                targetApplicationNode = child
                break
            }
        }

        for (Node child : manifestRoot) {
            if (child.name().toString() == "application") {
                fromApplicationNode = child
                break
            }
        }

        if (targetApplicationNode != null && fromApplicationNode != null) {
            logger.debug("start merge runalone manifest")
            Log.info("start merge runalone manifest")
            fromApplicationNode.children().each { c ->
                logger.debug("find:" + c.toString())
                Log.info("find:" + c.toString())
                Node node = c
                node.attributes().remove("xmlns:android")
                targetApplicationNode.append(node)
            }
            fromApplicationNode.attributes().keySet().each { k ->
                if (k.toString().contains("name"))
                    targetApplicationNode.attributes().put("android:name",
                            fromApplicationNode.attribute(k))
            }

            //冗余命名空间移除
            for (Node child : targetChildren) {
                child.attributes().remove("xmlns:android")
            }

        } else {
            def log = "t is null:" + targetManifestNode == null + " ; from is null:" + manifestRoot == null
            logger.error(log)
            Log.info(log)
        }

//        NodeList targetApplicationNodes = targetManifestNode.application
//        NodeList fromApplicationNodes = targetManifestNode.application
//        targetApplicationNodes.
    }
}