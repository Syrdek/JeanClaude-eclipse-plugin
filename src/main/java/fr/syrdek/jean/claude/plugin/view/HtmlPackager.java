package fr.syrdek.jean.claude.plugin.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.swt.graphics.Color;

/**
 * Package HTML with linked scripts and stylesheets resources into a single-page HTML.
 * 
 * @author cedric
 */
public class HtmlPackager {
  private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script\s*[^>]*\s+src\s*=\s*([\"'])([^\"']+)\\1\s*[^>]*>\s*</script>");
  private static final Pattern CSS_PATTERN = Pattern.compile("<link\s*[^>]*\s+href\s*=\s*([\"'])([^\"']+)\\1\s*[^>]*/?>(\s*</link>)?");

  private List<Entry<Pattern, String>> replacements = new ArrayList<>();

  /**
   * Static class.
   */
  public HtmlPackager() {
    super();
  }

  /**
   * Returns the HTML hexa string corresponding to the given color.
   * 
   * @param color The color.
   * @return The HTML hexa string.
   */
  public static final String toHex(final Color color) {
    return Integer.toHexString(color.getRed()) + Integer.toHexString(color.getGreen()) + Integer.toHexString(color.getBlue());
  }

  /**
   * Reads given resource file as a string.
   *
   * @param filePath The path to the resource file.
   * @return The file's contents
   */
  public static String getResourceFileAsString(String filePath) {
    if (filePath == null) {
      return null;
    }

    if (!filePath.startsWith("/")) {
      filePath = "/" + filePath;
    }

    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(
            HtmlPackager.class.getResourceAsStream(filePath)))) {
      return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Replaces the given filename with the content of the corresponding file.
   * 
   * @param filename The name of the file to replace.
   * @param prefix   The prefix to add before the file content.
   * @param suffix   The suffix to add after the file content.
   * @return The content of the file bound to the given path.
   */
  private String performReplacement(String filename, final String prefix, final String suffix) {
    for (final Entry<Pattern, String> e : replacements) {
      if (e.getKey().matcher(filename).matches()) {
        filename = e.getValue();
        if (filename.isEmpty())
          return "";
      }
    }

    return Matcher.quoteReplacement(prefix + getResourceFileAsString(filename) + suffix);
  }

  /**
   * Includes the content of an external script file to the HTML.
   *
   * @param html The HTML content where to replace script links by script content.
   * @return The modified HTML.
   */
  public String wrapExternalScript(final String html) {
    final Matcher matcher = SCRIPT_PATTERN.matcher(html);
    return matcher.replaceAll((MatchResult m) -> {
      return performReplacement(m.group(2), "<script>\n", "\n</script>");
    });
  }

  /**
   * Includes the content of an external script file to the HTML.
   *
   * @param html The HTML content where to replace script links by script content.
   * @return The modified HTML.
   */
  public String wrapExternalCss(final String html) {
    final Matcher matcher = CSS_PATTERN.matcher(html);
    return matcher.replaceAll((MatchResult m) -> {
      return performReplacement(m.group(2), "<style>\n", "\n</style>");
    });
  }

  /**
   * @param filepath The HTML resource path.
   * @return The content of the HTML resource, where all links have been included.
   */
  public String singleFileHtml(final String filepath) {
    return wrapExternalScript(wrapExternalCss(getResourceFileAsString(filepath)));
  }

  /**
   * Tells the packager to substitute files matching the given pattern with another file.
   * 
   * @param filename The pattern to substitute.
   * @param newname  The file to use in replacement of files matching the pattern. If null or empty, ignores the inclusion of files matching the pattern.
   */
  public void addFileReplacement(Pattern filename, String newname) {
    replacements.add(Map.entry(filename, newname == null ? "" : newname));
  }

  public static void main(String[] args) {
    final HtmlPackager tools = new HtmlPackager();
    tools.addFileReplacement(Pattern.compile(".*.min.js"), null);
    System.out.println(tools.singleFileHtml("main.htm"));
  }
}
