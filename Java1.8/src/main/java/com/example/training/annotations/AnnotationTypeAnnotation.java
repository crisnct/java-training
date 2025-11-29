package com.example.training.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ================= Meta-annotation =================
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@interface Qualifier {

}

// ================= Qualifier annotations =================
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@interface Region {
  String value();
}

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@interface Primary {

}

// ================= SPI =================
interface PaymentGateway {
  String charge(long cents);
}

// ================= Implementations =================
@Region("EU")
class StripeEU implements PaymentGateway {
  @Override
  public String charge(long cents) {
    return "StripeEU charged " + cents;
  }
}

@Region("US")
@Primary
class StripeUS implements PaymentGateway {
  @Override
  public String charge(long cents) {
    return "StripeUS charged " + cents;
  }
}

// ================= Consumer =================
class CheckoutService {
  // Request a gateway for EU explicitly via qualifier on the FIELD:
  @Region("EU")
  private PaymentGateway gateway;

  String pay(long cents) {
    return gateway.charge(cents);
  }
}

// ================= Tiny container with qualifier matching =================
class Container {

  private final Map<Class<?>, List<Object>> beans = new HashMap<>();

  public void register(Object bean) {
    Class<?>[] ifaces = bean.getClass().getInterfaces();
    for (Class<?> itf : ifaces) {
      beans.computeIfAbsent(itf, k -> new ArrayList<>()).add(bean);
    }
  }

  public <T> T wire(Class<T> type) {
    try {
      Constructor<T> ctor = type.getDeclaredConstructor();
      ctor.setAccessible(true);
      T instance = ctor.newInstance();

      Field[] fields = type.getDeclaredFields();
      for (Field f : fields) {
        Class<?> ft = f.getType();
        List<Object> candidates = beans.get(ft);
        if (candidates == null || candidates.isEmpty()) {
          continue;
        }

        // Collect qualifier annotations on the field
        List<Annotation> qualifiers = getQualifierAnnotations(f.getAnnotations());

        Object chosen = chooseByQualifiers(candidates, qualifiers);
        if (chosen == null) {
          throw new IllegalStateException("No bean matches qualifiers on field: " + f.getName());
        }
        f.setAccessible(true);
        f.set(instance, chosen);
      }
      return instance;
    } catch (Exception e) {
      throw new IllegalStateException("Wiring failed: " + e.getMessage(), e);
    }
  }

  private List<Annotation> getQualifierAnnotations(Annotation[] anns) {
    List<Annotation> out = new ArrayList<Annotation>();
    for (Annotation ann : anns) {
      if (isQualifier(ann.annotationType())) {
        out.add(ann);
      }
    }
    return out;
  }

  private boolean isQualifier(Class<? extends Annotation> annType) {
    return annType.isAnnotationPresent(Qualifier.class);
  }

  // Match: all field qualifiers must appear on the candidate's class with same values.
  private Object chooseByQualifiers(List<Object> candidates, List<Annotation> req) throws Exception {
    List<Object> matches = new ArrayList<Object>();
    for (Object bean : candidates) {
      if (matchesAll(bean.getClass(), req)) {
        matches.add(bean);
      }
    }
    if (matches.isEmpty()) {
      return null;
    }
    if (matches.size() == 1) {
      return matches.get(0);
    }

    // Tie-break: prefer @Primary on the candidate class (also a @Qualifier)
    for (Object match : matches) {
      if (match.getClass().isAnnotationPresent(Primary.class)) {
        return match;
      }
    }
    return matches.get(0);
  }

  private boolean matchesAll(Class<?> beanClass, List<Annotation> req) throws Exception {
    for (Annotation needed : req) {
      Class<? extends Annotation> qType = needed.annotationType();
      Annotation onBean = beanClass.getAnnotation(qType);
      if (onBean == null) {
        return false;
      }
      // Compare values if the qualifier has attributes (here only Region has one).
      if (qType == Region.class) {
        String a = ((Region) needed).value();
        String b = ((Region) onBean).value();
        if (!a.equals(b)) {
          return false;
        }
      }
    }
    return true;
  }
}

// ================= Demo =================
public class AnnotationTypeAnnotation {

  public static void main(String[] args) {
    Container c = new Container();
    c.register(new StripeEU());
    c.register(new StripeUS());

    CheckoutService svc = c.wire(CheckoutService.class);
    System.out.println(svc.pay(1999));
    // Output: StripeEU charged 1999
  }
}
