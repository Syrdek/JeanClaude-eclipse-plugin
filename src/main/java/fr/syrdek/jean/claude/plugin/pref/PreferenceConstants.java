package fr.syrdek.jean.claude.plugin.pref;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

  public static final String P_URL = "jeanclaudeUrl";
  public static final String DEFAULT_URL = "http://localhost:7890";

  public static final String P_TYPE = "jeanclaudeType";
  public static final String DEFAULT_TYPE = "ollama"; // ollama/gradio

  public static final String P_EXPLAIN_TPL = "explainTpl";
  public static final String DEFAULT_EXPLAIN_TPL = "Explique le code suivant : ```[SELECTED_TEXT]```";

  public static final String P_COMMENT_TPL = "commentTpl";
  public static final String DEFAULT_COMMENT_TPL = "Réécris le code suivant en y ajoutant des commentaires en français pour le rendre compréhensible : ```[SELECTED_TEXT]```";

  public static final String P_TEST_TPL = "testTpl";
  public static final String DEFAULT_TEST_TPL = "Construit les tests unitaires permettant de vérifier au mieux le fonctionnement de ce code : ```[SELECTED_TEXT]```";

  public static final String P_CHECK_TPL = "checkTpl";
  public static final String DEFAULT_CHECK_TPL = "Vérifie le code suivant afin de détecter les vulnérabilités potentielles : ```[SELECTED_TEXT]```";

}
