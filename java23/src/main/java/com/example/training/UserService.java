package com.example.training;

/// # UserService
///
/// High-level service for user operations.
///
/// This class demonstrates the **Java 23 Markdown Documentation Comments** feature (JEP 467). Markdown comments use the `///` prefix and support:
/// - Headings
/// - Lists
/// - Code blocks
/// - Links
/// - Emphasis
///
/// ## Responsibilities
/// - Create new users
/// - Validate user input
/// - Store users in the repository
///
/// ## Example API usage
/// ```java
/// var service = new UserService(new InMemoryUserRepository());
/// service.createUser("alice@example.com");
/// ```
///
/// ## Related
/// Visit the [official JEP 467](https://openjdk.org/jeps/467) for details.
public class UserService {

  private final UserRepository repository;

  /// ## Constructor
  ///
  /// Creates an instance of `UserService` using the given repository.
  ///
  /// - `repository` must not be `null`
  public UserService(UserRepository repository) {
    if (repository == null) {
      throw new IllegalArgumentException("repository cannot be null");
    }
    this.repository = repository;
  }

  /// ## Create a new user
  ///
  /// Creates a user with the specified email.
  ///
  /// ### Validation rules
  /// - Email must contain `"@"`
  /// - Email must be at least 5 characters long
  ///
  /// ```java
  /// service.createUser("john@example.com");
  /// ```
  ///
  /// @param email The user's email address.
  /// @return The created user.
  public User createUser(String email) {
    if (email == null || email.length() < 5 || !email.contains("@")) {
      throw new IllegalArgumentException("Invalid email: " + email);
    }
    User user = new User(email);
    repository.save(user);
    return user;
  }
}


/// # User entity
///
/// Simple value object representing a system user.
class User {

  private final String email;

  public User(String email) {
    this.email = email;
  }

  /// Returns the user's email.
  public String getEmail() {
    return email;
  }
}


/// # Repository abstraction
///
/// This is deliberately simple, just to have a real example.
interface UserRepository {

  void save(User user);
}


/// # In-memory repository
///
/// A tiny in-memory implementation.
/// ```java
/// var repo = new InMemoryUserRepository();
/// ```
class InMemoryUserRepository implements UserRepository {

  @Override
  /// Saves a user in memory.
  ///
  /// Because this is just an example, it only prints the result.
  public void save(User user) {
    System.out.println("Saved: " + user.getEmail());
  }
}
