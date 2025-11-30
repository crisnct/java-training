//open (for module) - Declares an open module (all packages are open for deep reflection).
//opens (inside of module for a dependency) - Allows deep reflection (e.g., for frameworks like Jackson, Hibernate).
//opens … to … - Open only to specific modules.
//requires - Specifies that this module depends on another module.
//requires transitive - Consumers of your module also require the dependency.
//requires static - Dependency needed only at compile time, optional at runtime.
//exports  - Makes a package available to other modules.
//exports … to … - Export only to specific modules.
//uses - Declares that this module consumes a service (Service Loader API).
//provides … with … -Declares service implementation(s). Example:
//provides com.example.spi.PaymentProcessor
//with com.example.impl.PayPalProcessor;
//permits - Not allowed here.(It only appears in sealed classes, not in module descriptors.)
//Summary:
//    module / open module
//    requires [static] [transitive]
//    exports [to]
//    opens [to]
//    uses
//    provides ... with ...

open module app.core {
  requires static app.api;
  requires java.instrument;
  requires jdk.incubator.httpclient;

  //uses only means:“At runtime, I might look for an implementation of this interface.”
  uses com.example.training.api.GreetingService;
  uses com.example.training.api.LisaGreetingService;
}