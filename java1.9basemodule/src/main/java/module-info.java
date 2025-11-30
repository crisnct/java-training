import com.example.training.api.LisaGreetingService;

module app.api {
  exports com.example.training.api;
  provides com.example.training.api.GreetingService with LisaGreetingService;
}