package com.example.training.annotations.processor.source;

import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

/**
 * See maven-compiler-plugin from pom.xml
 */
@SupportedAnnotationTypes(
    {
        "com.example.training.annotations.processor.source.ValidAgeSource"
    }
)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ValidAgeSourceProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations,
      RoundEnvironment roundEnv) {

    for (TypeElement annotation : annotations) {
      for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {

        if (element.getKind() != ElementKind.FIELD) {
          continue;
        }

        VariableElement field = (VariableElement) element;

        // Check type is int
        String type = field.asType().toString();
        if (!"int".equals(type)) {
          processingEnv.getMessager().printMessage(
              Diagnostic.Kind.ERROR,
              "@ValidAge can only be applied to int fields",
              field
          );
          continue;
        }

        // Try to validate compile-time constant (if any)
        Object constValue = field.getConstantValue();
        if (constValue instanceof Integer) {
          int value = (Integer) constValue;

          ValidAgeSource validAge = field.getAnnotation(ValidAgeSource.class);
          int min = validAge.min();
          int max = validAge.max();

          if (value < min || value > max) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "Constant value " + value +
                    " for field '" + field.getSimpleName() +
                    "' is outside allowed range [" + min + ", " + max + "]",
                field
            );
          }
        }

        // If constValue is null → non-constant field; cannot check the runtime value here.
      }
    }

    return true; // annotation handled
  }
}
