package fr.syrdek.jean.claude.plugin.view;

import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import fr.syrdek.jean.claude.plugin.JcController;

public class MarkdownViewer {

  private final HtmlPackager packager = new HtmlPackager();
  private final Browser browser;

  public MarkdownViewer(Composite parent, int style) {
    browser = new Browser(parent, style);
    browser.setJavascriptEnabled(true);
    packager.addFileReplacement(Pattern.compile("plugin_glue.js"), "");
    browser.setText(packager.singleFileHtml("main.htm"), true);

    new BrowserFunction(browser, "plugin_clear") {
      @Override
      public Object function(Object[] arguments) {
        clear();
        return super.function(arguments);
      }
    };
    new BrowserFunction(browser, "plugin_tell") {
      @Override
      public Object function(Object[] arguments) {
        tell(String.valueOf(arguments[0]));
        return super.function(arguments);
      }
    };
    new BrowserFunction(browser, "plugin_copy") {
      @Override
      public Object function(Object[] arguments) {
        copy(String.valueOf(arguments[0]));
        return super.function(arguments);
      }
    };
  }

  public static String jsLiteral(final String str) {
    return String.format("'%s'", StringEscapeUtils.escapeEcmaScript(str));
  }

  public Composite getBrowser() {
    return browser;
  }

  public void setError(String message) {
    browser.evaluate("jc.setError(\"" + message.replace("\"", "\\\"") + "\")", true);
  }

  public void setConversationHistory(final String message) {
    browser.evaluate("jc.setChat(" + message + ")", true);
  }

  private void tell(final String text) {
    System.out.println("Javascript hook : Tell" + text);
    JcController.predict(text);
  }

  private void clear() {
    System.out.println("Javascript hook : Clear");
    JcController.clear();
  }

  protected void copy(String text) {
    System.out.println("Javascript hook : Copy " + text);
    Clipboard clipboard = new Clipboard(Display.getDefault());
    clipboard.setContents(new Object[] { text }, new Transfer[] { TextTransfer.getInstance() });
    clipboard.dispose();
  }
}
