package fr.syrdek.jean.claude.plugin.view;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

public class MarkdownViewer {

  private final Parser parser = Parser.builder().build();
  private final HtmlRenderer renderer = HtmlRenderer.builder().build();

  private static final String DEFAULT_BG_COLOR = "34393d";
  private static final String DEFAULT_FG_COLOR = "eeeeee";
  private final String background;
  private final String foreground;

  private final Browser browser;
  private final ReadyFunction readyFunction;

  private final String jeanclaudeDiv = "document.getElementById('jeanclaude')";

  private static final String loadLib(final String name) {
    try (final InputStream s = MarkdownViewer.class.getResourceAsStream("/" + name)) {
      return IOUtils.toString(s, Charset.defaultCharset());
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static final String includeScript(final String name) {
    return "\n<script>\n" + loadLib(name) + "\n</script>\n";
  }

  private static final String toHex(final Color color) {
    return Integer.toHexString(color.getRed()) + Integer.toHexString(color.getGreen())
        + Integer.toHexString(color.getBlue());
  }

  public static String jsLiteral(final String str) {
    return String.format("'%s'", StringEscapeUtils.escapeEcmaScript(str));
  }

  public MarkdownViewer(Composite parent, int style) {
    final IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
    final ITheme currentTheme = themeManager.getCurrentTheme();

    final ColorRegistry colorRegistry = currentTheme.getColorRegistry();
//     colorRegistry.getKeySet().stream().sorted().forEach((String s) -> System.out.println(s + ": " +
//         toHex(colorRegistry.get(s))));

    background = getColor(colorRegistry, "CONTENT_ASSIST_BACKGROUND_COLOR", DEFAULT_BG_COLOR);
    foreground = getColor(colorRegistry, "CONTENT_ASSIST_FOREGROUND_COLOR", DEFAULT_FG_COLOR);

    browser = new Browser(parent, style);
    browser.setJavascriptEnabled(true);
    readyFunction = new ReadyFunction(browser, "javaReadyFunction");
    browser.setText(new StringBuilder("<html><head><style>")
        .append("body {background:#")
        .append(background)
        .append("; color:#")
        .append(foreground)
        .append(";}")
        .append("</style></head><body><div id='jeanclaude'></div></body>")
        .append(includeScript("jeanclaude.js"))
        .append("\n<script>\n").append("window.onload = function(){javaReadyFunction();};").append("\n</script>\n")
        .append("</html>")
        .toString(), true);
  }

  public String getColor(final ColorRegistry colorRegistry, final String key, final String defaultVal) {
    return colorRegistry.getKeySet().contains(key) ? toHex(colorRegistry.get(key)) : defaultVal;
  }

  public Object jsCall(String name, String... args) {
    final StringBuilder sb = new StringBuilder(name)
        .append("(")
        .append(String.join(", ", args))
        .append(");");
    return browser.evaluate(sb.toString(), true);
  }

  private String formatMarkdown(final String markdown) {
    return renderer.render(parser.parse(markdown));
  }

  public Composite getBrowser() {
    return browser;
  }

  /**
   * Permet de n'executer le code que quand le browser est prêt.
   */
  public class ReadyFunction extends BrowserFunction {

    private boolean ready = false;
    private Runnable readyAction = () -> {
    };

    public ReadyFunction(Browser browser, String name) {
      super(browser, name);
    }

    public void onReady(Runnable runnable) {
      if (ready) {
        Display display = Display.getCurrent();
        if (display == null) {
          display = Display.getDefault();
        }
        display.asyncExec(runnable);
      } else {
        readyAction = runnable;
      }
    }

    @Override
    public Object function(Object[] arguments) {
      ready = true;
      readyAction.run();
      return null;
    }
  }

  public void setError(String message) {
    System.out.println("setError(" + message + ")");
    readyFunction.onReady(() -> browser.evaluate(
        jeanclaudeDiv + ".innerHTML = " + jsLiteral("<span style='color:#AA0000'>" + message + "</span>"), true));
    // readyFunction.onReady(() -> jsCall("setMarkdown", jeanclaudeDiv, jsLiteral("<span style='color:#AA0000'>" +
    // message + "</span>")));
  }

  public void setMarkdown(final String markdown) {
    readyFunction
        .onReady(() -> browser.evaluate(jeanclaudeDiv + ".innerHTML = " + jsLiteral(formatMarkdown(markdown)), true));
    // readyFunction.onReady(() -> jsCall("setMarkdown", jeanclaudeDiv, jsLiteral(formatMarkdown(markdown))));
  }

  public void setWaiting() {
    System.out.println("setWaiting()");
    readyFunction.onReady(() -> browser.evaluate(
        jeanclaudeDiv + ".innerHTML = " + jsLiteral("<span style='color:#00AA00'>Attente de Jean-Claude...</span>"),
        true));
    // readyFunction.onReady(() -> jsCall("setMarkdown", jeanclaudeDiv, jsLiteral("<span style='color:#00AA00'>Attente
    // de Jean-Claude...</span>")));
  }
}
