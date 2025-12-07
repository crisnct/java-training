//@formatter:off
/**
 * Java 20 training module demo.
 */
//@formatter:on
module com.example.training {
  requires java.management;
  // export the packages you want visible to other modules
  exports com.example.training;
  exports com.example.training.utc;
}
