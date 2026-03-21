/**
 * PROBLEM STATEMENT: Web Server Security Pipeline
 * * * Background:
 * You are building the security middleware for a web server. Before a client's
 * request reaches the actual business logic, it must pass through a series of
 * strict checks: Rate Limiting (to prevent DDoS), Authentication (valid token),
 * and Authorization (admin privileges).
 * * * Functional Requirements:
 * 1. Process incoming HTTP-like requests.
 * 2. Validate IP rate limits, user tokens, and user roles in sequence.
 * 3. Stop processing immediately if any check fails.
 * * * Technical Constraints (Strict):
 * 1. Dynamic Reconfiguration: You must be able to add, remove, or reorder
 * these security checks at runtime without changing the core server code.
 * 2. Decoupling: The client making the request must not be tightly coupled to
 * the specific security filters.
 * 3. Short-Circuiting: If a filter fails, the request must immediately exit
 * the pipeline and not trigger subsequent filters.
 * * --------------------------------------------------------------------------
 * LLD: CHAIN OF RESPONSIBILITY PATTERN
 * * CORE INTENT: Pass requests along a chain of handlers. Upon receiving a request,
 * each handler decides either to process it or to pass it to the next handler.
 * * KEY POINTERS:
 * 1. Base Handler: Contains the boilerplate code for linking to the `next` handler.
 * 2. Concrete Handlers: Contain the actual business logic (e.g., checking tokens).
 */

// --- 1. THE HANDLER INTERFACE / BASE CLASS ---
abstract class Middleware {
    private Middleware next;

    // Builds the chain
    public Middleware linkWith(Middleware next) {
        this.next = next;
        return next; // Returning next allows for method chaining
    }

    // Subclasses will implement this
    public abstract boolean check(String email, String password, String ip);

    // Helper method to pass the request down the chain
    protected boolean checkNext(String email, String password, String ip) {
        if (next == null) {
            return true; // Reached the end of the chain successfully
        }
        return next.check(email, password, ip);
    }
}

// --- 2. CONCRETE HANDLERS ---
class ThrottlingMiddleware extends Middleware {
    private int requestsPerMinute;
    private int requestCount;
    private long currentTime;

    public ThrottlingMiddleware(int requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
        this.currentTime = System.currentTimeMillis();
    }

    @Override
    public boolean check(String email, String password, String ip) {
        if (System.currentTimeMillis() > currentTime + 60_000) {
            requestCount = 0;
            currentTime = System.currentTimeMillis();
        }

        requestCount++;
        if (requestCount > requestsPerMinute) {
            System.out.println("Request limit exceeded for IP: " + ip + "!");
            return false; // Short-circuit the chain
        }

        System.out.println("Throttling check passed.");
        return checkNext(email, password, ip); // Pass to next handler
    }
}

class UserExistsMiddleware extends Middleware {
    @Override
    public boolean check(String email, String password, String ip) {
        if (!email.equals("admin@example.com")) {
            System.out.println("This email is not registered!");
            return false; // Short-circuit
        }
        if (!password.equals("admin_pass")) {
            System.out.println("Wrong password!");
            return false; // Short-circuit
        }

        System.out.println("User Authentication passed.");
        return checkNext(email, password, ip); // Pass to next handler
    }
}

class RoleCheckMiddleware extends Middleware {
    @Override
    public boolean check(String email, String password, String ip) {
        if (email.equals("admin@example.com")) {
            System.out.println("Role check passed: Admin privileges granted.");
            return true; // Final step, returns true
        }
        System.out.println("Role check failed: User is not an admin.");
        return false;
    }
}

// --- 3. THE SERVER (Client configuration) ---
class Server {
    private Middleware middleware;

    public void setMiddleware(Middleware middleware) {
        this.middleware = middleware;
    }

    public boolean logIn(String email, String password, String ip) {
        if (middleware.check(email, password, ip)) {
            System.out.println("Authorization successful! Routing to admin dashboard...\n");
            return true;
        }
        System.out.println("Authorization failed. Request dropped.\n");
        return false;
    }
}

// --- 4. TESTER ---
public class ChainOfResponsibility {
    public static void main(String[] args) {
        Server server = new Server();

        // 1. Build the chain dynamically (Constraint #1 satisfied)
        Middleware middleware = new ThrottlingMiddleware(2);
        middleware.linkWith(new UserExistsMiddleware())
                .linkWith(new RoleCheckMiddleware());

        // 2. Inject the chain into the server
        server.setMiddleware(middleware);

        // 3. Test the chain
        System.out.println("--- Test 1: Wrong Password ---");
        server.logIn("admin@example.com", "wrong_pass", "192.168.1.1");

        System.out.println("--- Test 2: Correct Credentials ---");
        server.logIn("admin@example.com", "admin_pass", "192.168.1.1");

        System.out.println("--- Test 3: Rate Limit Exceeded ---");
        server.logIn("admin@example.com", "admin_pass", "192.168.1.1");
    }
}