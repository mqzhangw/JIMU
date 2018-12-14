package osp.leobert.android.router;

import com.google.auto.service.AutoService;
import com.luojilab.router.facade.annotation.Autowired;
import com.luojilab.router.facade.annotation.RouteNode;
import com.luojilab.router.facade.enums.NodeType;
import com.luojilab.router.facade.model.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import osp.leobert.android.reportprinter.spi.Model;
import osp.leobert.android.reportprinter.spi.ReporterExtension;
import osp.leobert.android.reportprinter.spi.Result;

@AutoService(ReporterExtension.class)
public class RouterReporterExtensionImpl implements ReporterExtension {
    private ArrayList<Node> routerNodes = new ArrayList<>();

    @Override
    public Set<String> applicableAnnotations() {
        return Collections.singleton(RouteNode.class.getName());
    }

    @Override
    public Result generateReport(Map<String, List<Model>> previousData) {
        if (previousData == null)
            return null;

        List<Model> routeNodeModels = previousData.get(RouteNode.class.getName());
        if (routeNodeModels == null || routeNodeModels.isEmpty())
            return Result.newBuilder().handled(false).build();

        for (Model m : routeNodeModels) {
            if (m == null) continue;
            buildNodeInfo(m.getElement().getAnnotation(RouteNode.class),
                    m.getElement());
        }

        StringBuilder docBuilder = generateRouterTable();


        return Result.newBuilder()
                .handled(true)
                .reportFileNamePrefix("RouterTable")
                .fileExt("txt")
                .reportContent(docBuilder.toString())
                .build();
    }

    private void buildNodeInfo(RouteNode route, Element element) {
        Node node = new Node();
        String path = route.path();

        node.setPath(path);
        node.setDesc(route.desc());
        node.setPriority(route.priority());
        node.setNodeType(NodeType.ACTIVITY);
        node.setRawType(element);

        Map<String, Integer> paramsType = new HashMap<>();
        Map<String, String> paramsDesc = new HashMap<>();
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null) {
                Autowired paramConfig = field.getAnnotation(Autowired.class);
                paramsDesc.put(isEmpty(paramConfig.name())
                        ? field.getSimpleName().toString() : paramConfig.name(), typeDesc(field));
            }
        }
        node.setParamsType(paramsType);
        node.setParamsDesc(paramsDesc);

        if (!routerNodes.contains(node)) {
            routerNodes.add(node);
        }
    }

    private boolean isEmpty(String name) {
        return name == null || name.isEmpty();
    }


    private StringBuilder generateRouterTable() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("auto generated, do not change !!!! \n\n");
        stringBuilder.append("HOST : {参考table文件名}" + "\n\n");

        for (Node node : routerNodes) {
            stringBuilder.append(node.getDesc() + "\n");
            stringBuilder.append(node.getPath() + "\n");
            Map<String, String> paramsType = node.getParamsDesc();

            for (Map.Entry<String, String> types : paramsType.entrySet()) {
                stringBuilder.append(types.getKey() + ":" + types.getValue() + "\n");
            }

            stringBuilder.append("\n");
        }

        return stringBuilder;

    }

    private String typeDesc(Element element) {
        TypeMirror typeMirror = element.asType();

        // Primitive
        if (typeMirror.getKind().isPrimitive()) {
            return element.asType().getKind().name();
        }

        switch (typeMirror.toString()) {
            case Constants.BYTE:
                return "byte";
            case Constants.SHORT:
                return "short";
            case Constants.INTEGER:
                return "int";
            case Constants.LONG:
                return "long";
            case Constants.FLOAT:
                return "byte";
            case Constants.DOUBEL:
                return "double";
            case Constants.BOOLEAN:
                return "boolean";
            case Constants.STRING:
                return "String";
            default:    // Other side, maybe the PARCELABLE or OBJECT.
                if (element instanceof TypeElement) {
                    List<? extends TypeMirror> interfaces = ((TypeElement) element).getInterfaces();
                    for (TypeMirror i : interfaces) {
                        if (Constants.PARCELABLE.equals(i.toString()))
                            return "parcelable";
                    }
                    return typeMirror.toString();
                }
                return "unknown";
        }
    }
}
