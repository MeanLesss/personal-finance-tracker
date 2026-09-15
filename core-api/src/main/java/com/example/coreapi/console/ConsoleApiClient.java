package com.example.coreapi.console;

import com.example.coreapi.CoreApiApplication;
import org.springframework.boot.SpringApplication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConsoleApiClient {

    private record MenuItem(String label, boolean menu, boolean close, Runnable action) {

        static MenuItem nav(String label, Runnable action) {
            return new MenuItem(label, false, false, action);
        }

        static MenuItem menu(String label, Runnable action) {
            return new MenuItem(label, true, false, action);
        }

        static MenuItem back() {
            return new MenuItem("Back", false, true, () -> { });
        }
    }

    private static final String FOOTER = "[Up/Down] move   [Enter] select   [Esc] back   [q] quit";

    private final ApiClient api;
    private final ConsoleUi ui;
    private boolean exit = false;

    public static void main(String[] args) {
        String baseUrl = resolveBaseUrl(args);
        boolean color = resolveColor(args);
        boolean noBoot = hasFlag("--no-boot", args);

        ConsoleUi ui = new ConsoleUi(color);
        ui.clear();
        ui.title("Personal Finance Tracker - API Console");
        ui.info("Base URL : " + baseUrl);
        ui.info("Ctrl+C   : hard exit (if the menu gets stuck)");
        try {
            Thread.sleep(600);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        if (!noBoot) {
            ensureServerUp(baseUrl, ui);
        }
        new ConsoleApiClient(baseUrl, ui).start();
    }

    private ConsoleApiClient(String baseUrl, ConsoleUi ui) {
        this.api = new ApiClient(baseUrl);
        this.ui = ui;
    }

    // ---------------------------------------------------------------- startup

    private static void ensureServerUp(String baseUrl, ConsoleUi ui) {
        ApiClient probe = new ApiClient(baseUrl);
        if (probe.isUp()) {
            ui.success("API is already running at " + baseUrl + " - connecting.");
            return;
        }
        ui.warn("API not running at " + baseUrl + " - starting Spring Boot server...");
        Thread boot = new Thread(() -> SpringApplication.run(CoreApiApplication.class, new String[0]), "pft-server");
        boot.setDaemon(true);
        boot.start();

        long deadline = System.currentTimeMillis() + 90_000;
        while (System.currentTimeMillis() < deadline) {
            if (probe.isUp()) {
                ui.success("API is up at " + baseUrl);
                return;
            }
            sleep(1500);
        }
        ui.error("Timed out waiting for the API. Requests will report connection errors - check the server log.");
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ------------------------------------------------------------------ root

    public void start() {
        login();
        runMenu("MAIN MENU", rootItems());
        ui.clear();
        ui.info("Bye.");
    }

    private void login() {
        ui.blank();
        ui.title("Login");
        while (true) {
            String email = ui.readLine("  Email (blank to skip) : ");
            if (email.isBlank()) {
                ui.warn("Skipping login - secured calls will return HTTP 401.");
                return;
            }
            String password = ui.readLine("  Password              : ");
            ApiClient.Response r = api.post("/auth/login", payload("email", email, "password", password));
            if (!r.ok()) {
                ui.error("Login failed (HTTP " + r.status() + ") " + r.body());
                continue;
            }
            api.setToken(api.extract(r.body(), "accessToken"));
            ui.success("Logged in as " + email);
            return;
        }
    }

    private List<MenuItem> rootItems() {
        List<MenuItem> items = new ArrayList<>();
        items.add(MenuItem.menu("Users", this::usersMenu));
        items.add(MenuItem.menu("Categories", this::categoriesMenu));
        items.add(MenuItem.menu("Accounts", this::accountsMenu));
        items.add(MenuItem.menu("Transactions", this::transactionsMenu));
        items.add(MenuItem.menu("Budgets", this::budgetsMenu));
        items.add(MenuItem.menu("Reports", this::reportsMenu));
        items.add(MenuItem.nav("Exit", () -> exit = true));
        return items;
    }

    private void runMenu(String title, List<MenuItem> items) {
        int selected = 0;
        while (!exit) {
            String[] labels = items.stream().map(MenuItem::label).toArray(String[]::new);
            ui.renderMenu(title, labels, selected, FOOTER);
            ConsoleUi.Key key = ui.readKey();
            if (key.isUp()) {
                selected = Math.max(0, selected - 1);
            } else if (key.isDown()) {
                selected = Math.min(items.size() - 1, selected + 1);
            } else if (key.isEnter() || key.kind == ConsoleUi.Key.Kind.RIGHT) {
                MenuItem item = items.get(selected);
                ui.clear();
                if (item.close()) {
                    return;
                }
                item.action().run();
                if (exit) {
                    return;
                }
                if (!item.menu() && !item.close()) {
                    ui.awaitContinue();
                }
            } else if (key.isBack()) {
                return;
            } else if (key.kind == ConsoleUi.Key.Kind.QUIT) {
                exit = true;
                return;
            }
        }
    }

    // -------------------------------------------------------------- sub-menus

    private void usersMenu() {
        runMenu("USERS", List.of(
                MenuItem.nav("List all users", this::userList),
                MenuItem.nav("Find user by ID", this::userFind),
                MenuItem.nav("Create user", this::userCreate),
                MenuItem.nav("Update user", this::userUpdate),
                MenuItem.nav("Delete user", this::userDelete),
                MenuItem.back()
        ));
    }

    private void categoriesMenu() {
        runMenu("CATEGORIES", List.of(
                MenuItem.nav("List all categories", this::categoryList),
                MenuItem.nav("Find category by ID", this::categoryFind),
                MenuItem.nav("Create category", this::categoryCreate),
                MenuItem.nav("Update category", this::categoryUpdate),
                MenuItem.nav("Delete category", this::categoryDelete),
                MenuItem.back()
        ));
    }

    private void accountsMenu() {
        runMenu("ACCOUNTS", List.of(
                MenuItem.nav("List all accounts", this::accountList),
                MenuItem.nav("Find account by ID", this::accountFind),
                MenuItem.nav("Create account", this::accountCreate),
                MenuItem.nav("Update account", this::accountUpdate),
                MenuItem.nav("Delete account", this::accountDelete),
                MenuItem.nav("Account balance", this::accountBalance),
                MenuItem.nav("Account transactions", this::accountTransactions),
                MenuItem.nav("Net worth (by user)", this::accountNetWorth),
                MenuItem.nav("Transfer between accounts", this::accountTransfer),
                MenuItem.back()
        ));
    }

    private void transactionsMenu() {
        runMenu("TRANSACTIONS", List.of(
                MenuItem.nav("List transactions (filterable)", this::transactionList),
                MenuItem.nav("Find transaction by ID", this::transactionFind),
                MenuItem.nav("Create transaction", this::transactionCreate),
                MenuItem.nav("Update transaction", this::transactionUpdate),
                MenuItem.nav("Delete transaction", this::transactionDelete),
                MenuItem.back()
        ));
    }

    private void budgetsMenu() {
        runMenu("BUDGETS", List.of(
                MenuItem.nav("List all budgets", this::budgetList),
                MenuItem.nav("Find budget by ID", this::budgetFind),
                MenuItem.nav("Create budget", this::budgetCreate),
                MenuItem.nav("Update budget", this::budgetUpdate),
                MenuItem.nav("Delete budget", this::budgetDelete),
                MenuItem.nav("Budget status (monthly)", this::budgetStatus),
                MenuItem.back()
        ));
    }

    private void reportsMenu() {
        runMenu("REPORTS", List.of(
                MenuItem.nav("Monthly summary", this::reportSummary),
                MenuItem.nav("Category breakdown", this::reportCategoryBreakdown),
                MenuItem.back()
        ));
    }

    // --------------------------------------------------------------- actions

    private void userList() {
        int page = promptInt("Page", 1);
        int size = promptInt("Size", 15);
        printResponse(api.get("/users", payload("page", page, "size", size)));
    }

    private void userFind() {
        Long id = promptLong("User ID");
        if (id == null) return;
        printResponse(api.get("/users/" + id, null));
    }

    private void userCreate() {
        String name = prompt("Name");
        if (name.isBlank()) return;
        String email = prompt("Email");
        String password = prompt("Password");
        printResponse(api.post("/users", payload("name", name, "email", email, "password", password)));
    }

    private void userUpdate() {
        Long id = promptLong("User ID");
        if (id == null) return;
        String name = prompt("Name");
        String email = prompt("Email");
        String password = prompt("Password");
        printResponse(api.put("/users/" + id, payload("name", name, "email", email, "password", password)));
    }

    private void userDelete() {
        Long id = promptLong("User ID");
        if (id == null) return;
        printResponse(api.delete("/users/" + id));
    }

    private void categoryList() {
        int page = promptInt("Page", 1);
        int size = promptInt("Size", 15);
        printResponse(api.get("/categories", payload("page", page, "size", size)));
    }

    private void categoryFind() {
        Long id = promptLong("Category ID");
        if (id == null) return;
        printResponse(api.get("/categories/" + id, null));
    }

    private void categoryCreate() {
        String name = prompt("Name");
        if (name.isBlank()) return;
        String type = promptEnum("Type", "INCOME", "EXPENSE");
        Long userId = promptLong("User ID (optional)");
        printResponse(api.post("/categories", payload("name", name, "type", type, "userId", userId)));
    }

    private void categoryUpdate() {
        Long id = promptLong("Category ID");
        if (id == null) return;
        String name = prompt("Name");
        String type = promptEnum("Type", "INCOME", "EXPENSE");
        Long userId = promptLong("User ID (optional)");
        printResponse(api.put("/categories/" + id, payload("name", name, "type", type, "userId", userId)));
    }

    private void categoryDelete() {
        Long id = promptLong("Category ID");
        if (id == null) return;
        printResponse(api.delete("/categories/" + id));
    }

    private void accountList() {
        int page = promptInt("Page", 1);
        int size = promptInt("Size", 15);
        printResponse(api.get("/accounts", payload("page", page, "size", size)));
    }

    private void accountFind() {
        Long id = promptLong("Account ID");
        if (id == null) return;
        printResponse(api.get("/accounts/" + id, null));
    }

    private void accountCreate() {
        String name = prompt("Account name");
        if (name.isBlank()) return;
        String type = promptEnum("Type", "CASH", "BANK", "WALLET", "CREDIT");
        BigDecimal balance = promptDecimal("Initial balance (optional)");
        String currency = prompt("Currency");
        if (currency.isBlank()) currency = "USD";
        Long userId = promptLong("User ID");
        printResponse(api.post("/accounts",
                payload("accountName", name, "accountType", type, "balance", balance, "currency", currency, "userId", userId)));
    }

    private void accountUpdate() {
        Long id = promptLong("Account ID");
        if (id == null) return;
        String name = prompt("Account name");
        if (name.isBlank()) return;
        String type = promptEnum("Type", "CASH", "BANK", "WALLET", "CREDIT");
        BigDecimal balance = promptDecimal("Balance (optional)");
        String currency = prompt("Currency");
        if (currency.isBlank()) currency = "USD";
        Long userId = promptLong("User ID");
        printResponse(api.put("/accounts/" + id,
                payload("accountName", name, "accountType", type, "balance", balance, "currency", currency, "userId", userId)));
    }

    private void accountDelete() {
        Long id = promptLong("Account ID");
        if (id == null) return;
        printResponse(api.delete("/accounts/" + id));
    }

    private void accountBalance() {
        Long id = promptLong("Account ID");
        if (id == null) return;
        printResponse(api.get("/accounts/" + id + "/balance", null));
    }

    private void accountTransactions() {
        Long id = promptLong("Account ID");
        if (id == null) return;
        printResponse(api.get("/accounts/" + id + "/transactions", null));
    }

    private void accountNetWorth() {
        Long userId = promptLong("User ID");
        if (userId == null) return;
        printResponse(api.get("/accounts/net-worth", payload("userId", userId)));
    }

    private void accountTransfer() {
        Long from = promptLong("From account ID");
        if (from == null) return;
        Long to = promptLong("To account ID");
        if (to == null) return;
        BigDecimal amount = promptDecimal("Amount");
        if (amount == null) return;
        String description = prompt("Description (optional)");
        printResponse(api.post("/accounts/transfer",
                payload("fromAccountId", from, "toAccountId", to, "amount", amount, "description", description)));
    }

    private void transactionList() {
        ui.warn("Leave a filter blank to skip it.");
        Long accountId = promptLong("Account ID (optional)");
        Long categoryId = promptLong("Category ID (optional)");
        String type = promptEnum("Type (optional)", "INCOME", "EXPENSE", "TRANSFER");
        LocalDate start = promptDate("Start date", null);
        LocalDate end = promptDate("End date", null);
        int page = promptInt("Page", 1);
        int size = promptInt("Size", 15);
        printResponse(api.get("/transactions", payload(
                "accountId", accountId,
                "categoryId", categoryId,
                "type", type,
                "startDate", start,
                "endDate", end,
                "page", page,
                "size", size)));
    }

    private void transactionFind() {
        Long id = promptLong("Transaction ID");
        if (id == null) return;
        printResponse(api.get("/transactions/" + id, null));
    }

    private void transactionCreate() {
        BigDecimal amount = promptDecimal("Amount");
        if (amount == null) return;
        String type = promptEnum("Type", "INCOME", "EXPENSE", "TRANSFER");
        String description = prompt("Description (optional)");
        LocalDate date = promptDate("Date", LocalDate.now());
        Long accountId = promptLong("Account ID");
        Long categoryId = promptLong("Category ID");
        printResponse(api.post("/transactions",
                payload("amount", amount, "type", type, "description", description, "date", date,
                        "accountId", accountId, "categoryId", categoryId)));
    }

    private void transactionUpdate() {
        Long id = promptLong("Transaction ID");
        if (id == null) return;
        BigDecimal amount = promptDecimal("Amount");
        if (amount == null) return;
        String type = promptEnum("Type", "INCOME", "EXPENSE", "TRANSFER");
        String description = prompt("Description (optional)");
        LocalDate date = promptDate("Date", LocalDate.now());
        Long accountId = promptLong("Account ID");
        Long categoryId = promptLong("Category ID");
        printResponse(api.put("/transactions/" + id,
                payload("amount", amount, "type", type, "description", description, "date", date,
                        "accountId", accountId, "categoryId", categoryId)));
    }

    private void transactionDelete() {
        Long id = promptLong("Transaction ID");
        if (id == null) return;
        printResponse(api.delete("/transactions/" + id));
    }

    private void budgetList() {
        int page = promptInt("Page", 1);
        int size = promptInt("Size", 15);
        printResponse(api.get("/budgets", payload("page", page, "size", size)));
    }

    private void budgetFind() {
        Long id = promptLong("Budget ID");
        if (id == null) return;
        printResponse(api.get("/budgets/" + id, null));
    }

    private void budgetCreate() {
        BigDecimal limit = promptDecimal("Monthly limit");
        if (limit == null) return;
        int month = promptInt("Month (1-12)", LocalDate.now().getMonthValue());
        int year = promptInt("Year", LocalDate.now().getYear());
        Long userId = promptLong("User ID");
        Long categoryId = promptLong("Category ID");
        printResponse(api.post("/budgets",
                payload("monthlyLimit", limit, "month", month, "year", year, "userId", userId, "categoryId", categoryId)));
    }

    private void budgetUpdate() {
        Long id = promptLong("Budget ID");
        if (id == null) return;
        BigDecimal limit = promptDecimal("Monthly limit");
        if (limit == null) return;
        int month = promptInt("Month (1-12)", LocalDate.now().getMonthValue());
        int year = promptInt("Year", LocalDate.now().getYear());
        Long userId = promptLong("User ID");
        Long categoryId = promptLong("Category ID");
        printResponse(api.put("/budgets/" + id,
                payload("monthlyLimit", limit, "month", month, "year", year, "userId", userId, "categoryId", categoryId)));
    }

    private void budgetDelete() {
        Long id = promptLong("Budget ID");
        if (id == null) return;
        printResponse(api.delete("/budgets/" + id));
    }

    private void budgetStatus() {
        Long userId = promptLong("User ID");
        if (userId == null) return;
        int month = promptInt("Month (1-12)", LocalDate.now().getMonthValue());
        int year = promptInt("Year", LocalDate.now().getYear());
        printResponse(api.get("/budgets/status", payload("userId", userId, "month", month, "year", year)));
    }

    private void reportSummary() {
        Long userId = promptLong("User ID");
        if (userId == null) return;
        int month = promptInt("Month (1-12)", LocalDate.now().getMonthValue());
        int year = promptInt("Year", LocalDate.now().getYear());
        printResponse(api.get("/reports/summary", payload("userId", userId, "month", month, "year", year)));
    }

    private void reportCategoryBreakdown() {
        Long userId = promptLong("User ID");
        if (userId == null) return;
        int month = promptInt("Month (1-12)", LocalDate.now().getMonthValue());
        int year = promptInt("Year", LocalDate.now().getYear());
        printResponse(api.get("/reports/category-breakdown", payload("userId", userId, "month", month, "year", year)));
    }

    // -------------------------------------------------------------- helpers

    private void printResponse(ApiClient.Response response) {
        if (response.status() < 0) {
            ui.error(response.body());
            return;
        }
        if (response.ok()) {
            ui.success("HTTP " + response.status());
        } else {
            ui.error("HTTP " + response.status());
        }
        if (response.body() != null && !response.body().isBlank()) {
            ui.plain(api.prettyPrint(response.body()));
        }
    }

    static Map<String, Object> payload(Object... kv) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            Object value = kv[i + 1];
            if (value != null) {
                map.put(String.valueOf(kv[i]), value);
            }
        }
        return map;
    }

    private String prompt(String label) {
        return ui.readLine("  " + label + ": ");
    }

    private Long promptLong(String label) {
        while (true) {
            String s = prompt(label);
            if (s.isBlank()) return null;
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                ui.error("Invalid number: " + s);
            }
        }
    }

    private Integer promptInt(String label, int def) {
        while (true) {
            String s = prompt(label + " [" + def + "]");
            if (s.isBlank()) return def;
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                ui.error("Invalid number: " + s);
            }
        }
    }

    private BigDecimal promptDecimal(String label) {
        while (true) {
            String s = prompt(label);
            if (s.isBlank()) return null;
            try {
                return new BigDecimal(s);
            } catch (NumberFormatException e) {
                ui.error("Invalid amount: " + s);
            }
        }
    }

    private LocalDate promptDate(String label, LocalDate def) {
        while (true) {
            String s = prompt(label + (def != null ? " [" + def + "]" : " (optional)"));
            if (s.isBlank()) return def;
            try {
                return LocalDate.parse(s);
            } catch (Exception e) {
                ui.error("Use yyyy-MM-dd format");
            }
        }
    }

    private String promptEnum(String label, String... options) {
        ui.line("  " + label + ":");
        for (int i = 0; i < options.length; i++) {
            ui.line("    " + (i + 1) + ") " + options[i]);
        }
        while (true) {
            String s = prompt(label + " (number or value, blank to skip)");
            if (s.isBlank()) return null;
            String input = s.trim();
            try {
                int idx = Integer.parseInt(input);
                if (idx >= 1 && idx <= options.length) {
                    return options[idx - 1];
                }
                ui.error("Number must be between 1 and " + options.length);
                continue;
            } catch (NumberFormatException ignored) {
                // not a number - fall through to value matching
            }
            for (String option : options) {
                if (option.equalsIgnoreCase(input)) return option;
            }
            ui.error("Must be one of: " + String.join(", ", options));
        }
    }

    private static String resolveBaseUrl(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--api-url=")) {
                return arg.substring("--api-url=".length());
            }
        }
        String env = System.getenv("API_URL");
        if (env != null && !env.isBlank()) {
            return env;
        }
        return "http://localhost:9090";
    }

    private static boolean hasFlag(String flag, String[] args) {
        for (String arg : args) {
            if (arg.equals(flag)) return true;
        }
        return false;
    }

    private static boolean resolveColor(String[] args) {
        if (System.getenv("NO_COLOR") != null) {
            return false;
        }
        return !hasFlag("--no-color", args);
    }
}