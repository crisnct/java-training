package com.example.training.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 1) Class-level annotation with runtime visibility
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface ProviderMeta {

  String id();

  boolean sandbox() default false;
}

// 2) SPI for providers
interface PaymentProvider {

  String charge(long cents);
}

// 3) Implementations annotated at CLASS level
@ProviderMeta(id = "stripe", sandbox = true)
class StripeProvider implements PaymentProvider {

  public String charge(long cents) {
    return "Stripe OK: " + cents;
  }
}

@ProviderMeta(id = "paypal")
class PaypalProvider implements PaymentProvider {

  public String charge(long cents) {
    return "PayPal OK: " + cents;
  }
}

// 4) Registry: discover, index by annotation, and select by id
class ProviderRegistry {

  private final Map<String, PaymentProvider> byId;

  ProviderRegistry(Collection<PaymentProvider> providers) {
    this.byId = providers.stream().collect(Collectors.toMap(
        p -> p.getClass().getAnnotation(ProviderMeta.class).id(),
        p -> p
    ));
  }

  PaymentProvider get(String id) {
    PaymentProvider p = byId.get(id);
    if (p == null) {
      throw new IllegalArgumentException("Unknown provider: " + id);
    }
    return p;
  }

  List<String> listSandboxProviders() {
    return byId.values().stream()
        .filter(p -> p.getClass().getAnnotation(ProviderMeta.class).sandbox())
        .map(p -> p.getClass().getAnnotation(ProviderMeta.class).id())
        .collect(Collectors.toList());
  }
}

// 5) Demo
public class TypeAnnotationDemo {

  public static void main(String[] args) {
    // In real apps, use ServiceLoader<PaymentProvider>; here we build manually for brevity.
    List<PaymentProvider> implementations = Arrays.asList(new StripeProvider(), new PaypalProvider());

    // Ensure all are annotated at CLASS level
    for (PaymentProvider p : implementations) {
      if (!p.getClass().isAnnotationPresent(ProviderMeta.class)) {
        throw new IllegalStateException("Provider missing @ProviderMeta: " + p.getClass().getName());
      }
    }

    ProviderRegistry registry = new ProviderRegistry(implementations);
    System.out.println("Sandbox providers: " + registry.listSandboxProviders());
    System.out.println(registry.get("stripe").charge(1299));
    System.out.println(registry.get("paypal").charge(2599));
  }
}
