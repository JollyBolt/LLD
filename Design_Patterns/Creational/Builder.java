/**
 * LLD: BUILDER PATTERN
 * * CORE INTENT: Separate the construction of a complex object from its representation.
 * * KEY POINTERS:
 * 1. Immutability: The final object (User) usually has no setters, making it thread-safe.
 * 2. Method Chaining: Returns 'this' from each setter for a fluent API.
 * 3. Validation: You can check if data is valid inside the build() method.
 */

class User {
    // All fields are final (Immutable)
    private final String firstName; // Required
    private final String lastName;  // Required
    private final int age;          // Optional
    private final String phone;     // Optional

    // Private constructor: Only the Builder can call this
    private User(UserBuilder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.phone = builder.phone;
    }

    @Override
    public String toString() {
        return "User: " + firstName + " " + lastName + ", Age: " + age + ", Phone: " + phone;
    }

    // --- STATIC INNER BUILDER CLASS ---
    public static class UserBuilder {
        private final String firstName;
        private final String lastName;
        private int age = 0; // Default values
        private String phone = "";

        public UserBuilder(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public UserBuilder setAge(int age) {
            this.age = age;
            return this; // Return this for chaining
        }

        public UserBuilder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public User build() {
            // Validation logic can go here
            return new User(this);
        }
    }
}

// --- TESTER ---
public class Builder {
    public static void main(String[] args) {
        // Fluent API: Looks clean and readable
        User user1 = new User.UserBuilder("Ishan", "Lastname")
                        .setAge(25)
                        .setPhone("123456789")
                        .build();

        User user2 = new User.UserBuilder("John", "Doe")
                        .build(); // Only required fields used

        System.out.println(user1);
        System.out.println(user2);
    }
}