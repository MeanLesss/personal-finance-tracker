package com.example.coreapi.console;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.PrintWriter;

public class ConsoleUi {

    public static final class Key {
        public enum Kind { UP, DOWN, LEFT, RIGHT, ENTER, ESC, QUIT, CHAR }

        public final Kind kind;
        public final char ch;

        private Key(Kind kind, char ch) {
            this.kind = kind;
            this.ch = ch;
        }

        public static Key of(Kind kind) {
            return new Key(kind, '\0');
        }

        public static Key charKey(char c) {
            return new Key(Kind.CHAR, c);
        }

        public boolean isEnter() {
            return kind == Kind.ENTER;
        }

        public boolean isBack() {
            return kind == Kind.ESC || kind == Kind.LEFT;
        }

        public boolean isUp() {
            return kind == Kind.UP;
        }

        public boolean isDown() {
            return kind == Kind.DOWN;
        }
    }

    private final boolean color;
    private Terminal terminal;
    private boolean usable;

    public ConsoleUi(boolean color) {
        this.color = color;
    }

    private synchronized Terminal terminal() {
        if (terminal == null && !usable) {
            try {
                terminal = TerminalBuilder.builder()
                        .system(true)
                        .jansi(true)
                        .build();
                usable = terminal != null && !"dumb".equalsIgnoreCase(terminal.getType());
                if (terminal != null && usable) {
                    terminal.enterRawMode();
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        try {
                            terminal.close();
                        } catch (IOException ignored) {
                            // nothing to do
                        }
                    }));
                }
            } catch (Exception | NoClassDefFoundError e) {
                usable = false;
                terminal = null;
            }
        }
        return usable ? terminal : null;
    }

    private PrintWriter out() {
        if (terminal() != null) {
            return terminal().writer();
        }
        return new PrintWriter(System.out, true);
    }

    private int readByte() throws IOException {
        if (terminal() != null) {
            return terminal().reader().read();
        }
        return System.in.read();
    }

    public void clear() {
        if (color) {
            PrintWriter w = out();
            w.print("\033[H\033[J");
            w.flush();
        }
    }

    public void line(String s) {
        PrintWriter w = out();
        w.println(s);
        w.flush();
    }

    public void plain(String s) {
        line(s);
    }

    public void blank() {
        line("");
    }

    public void info(String s) {
        colored("36", "  " + s);
    }

    public void success(String s) {
        colored("32", "  " + s);
    }

    public void warn(String s) {
        colored("33", "  " + s);
    }

    public void error(String s) {
        colored("31", "  " + s);
    }

    public void title(String s) {
        colored("1;36", "  " + s);
    }

    private void colored(String ansi, String s) {
        PrintWriter w = out();
        if (color) {
            w.print("\033[" + ansi + "m");
        }
        w.print(s);
        if (color) {
            w.print("\033[0m");
        }
        w.println();
        w.flush();
    }

    public void renderMenu(String title, String[] labels, int selected, String footer) {
        clear();
        line("");
        title(title);
        line("");
        PrintWriter w = out();
        for (int i = 0; i < labels.length; i++) {
            if (i == selected) {
                if (color) {
                    w.print("\033[7m");
                }
                w.print("   > " + labels[i]);
                if (color) {
                    w.print("\033[0m");
                }
                w.println();
            } else {
                w.println("     " + labels[i]);
            }
        }
        w.flush();
        line("");
        warn(footer);
    }

    public Key readKey() {
        int b;
        try {
            b = readByte();
        } catch (Exception e) {
            return Key.of(Key.Kind.ESC);
        }
        if (b == -1) {
            return Key.of(Key.Kind.QUIT);
        }
        if (b == 27) {
            try {
                int b2 = readByte();
                if (b2 == '[' || b2 == 'O') {
                    int b3 = readByte();
                    if (b3 == 'A') return Key.of(Key.Kind.UP);
                    if (b3 == 'B') return Key.of(Key.Kind.DOWN);
                    if (b3 == 'C') return Key.of(Key.Kind.RIGHT);
                    if (b3 == 'D') return Key.of(Key.Kind.LEFT);
                    if (b3 == '1') {
                        int b4 = readByte();
                        if (b4 == '~') return Key.of(Key.Kind.UP);
                        if (b4 == ';') {
                            readByte();
                            readByte();
                            readByte();
                            return Key.of(Key.Kind.ESC);
                        }
                    }
                    if (b3 == '4') {
                        int b4 = readByte();
                        if (b4 == '~') return Key.of(Key.Kind.DOWN);
                    }
                    if (b3 == 'H') return Key.of(Key.Kind.UP);
                    if (b3 == 'F') return Key.of(Key.Kind.DOWN);
                }
            } catch (Exception ignored) {
                // treat as a plain Esc
            }
            return Key.of(Key.Kind.ESC);
        }
        if (b == '\r' || b == '\n') {
            return Key.of(Key.Kind.ENTER);
        }
        char c = (char) b;
        if (c == 'q' || c == 'Q') {
            return Key.of(Key.Kind.QUIT);
        }
        return Key.charKey(c);
    }

    public void awaitContinue() {
        PrintWriter w = out();
        w.println();
        w.print("  ...press any key to continue ");
        w.flush();
        readKey();
        w.println();
        w.flush();
    }

    public String readLine(String prompt) {
        PrintWriter w = out();
        w.print(prompt);
        w.flush();
        StringBuilder sb = new StringBuilder();
        while (true) {
            int b;
            try {
                b = readByte();
            } catch (Exception e) {
                return sb.toString().trim();
            }
            if (b == -1 || b == '\n' || b == '\r') {
                break;
            }
            if (b == 8 || b == 127) {
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                    w.print("\b \b");
                    w.flush();
                }
            } else {
                sb.append((char) b);
                w.print((char) b);
                w.flush();
            }
        }
        w.println();
        w.flush();
        return sb.toString().trim();
    }
}