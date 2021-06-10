package com.jimu.hotfix;

import com.google.auto.service.AutoService;
import com.luojilab.router.compiler.processor.RouterProcessor;
import com.luojilab.router.compiler.utils.Constants;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

/**
 * <p><b>Package:</b> apt </p>
 * <p><b>Project:</b> jimu-sample-project </p>
 * <p><b>Classname:</b> RouterProcessor </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2021/6/10.
 */
@AutoService(Processor.class)
@SupportedOptions(Constants.KEY_HOST_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({Constants.ANNOTATION_TYPE_ROUTE_NODE, Constants.ANNOTATION_TYPE_ROUTER})
public class RouterProcessorHotfix extends RouterProcessor {
}
