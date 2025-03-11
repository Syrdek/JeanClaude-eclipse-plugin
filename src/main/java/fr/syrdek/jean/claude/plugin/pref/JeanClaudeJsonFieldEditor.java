/**
 * @author cedric
 */
package fr.syrdek.jean.claude.plugin.pref;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.gson.Gson;

import fr.syrdek.jean.claude.plugin.config.JeanClaudeConfig;

public class JeanClaudeJsonFieldEditor extends FieldEditor {
  private Gson gson = new Gson();
  private Text textField;
  private boolean wasOk = false;
  private String oldValue = "";

  public JeanClaudeJsonFieldEditor(String name, String labelText, Composite parent) {
    init(name, labelText);
    createControl(parent);
  }

  @Override
  protected void adjustForNumColumns(int numColumns) {
    GridData gd = (GridData) textField.getLayoutData();
    gd.horizontalSpan = numColumns - 1;
    gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
  }

  @Override
  protected void doFillIntoGrid(Composite parent, int numColumns) {
    Label label = getLabelControl(parent);
    GridData gd = new GridData();
    gd.horizontalSpan = numColumns - 1;
    label.setLayoutData(gd);

    textField = getTextControl(parent);
    gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.verticalAlignment = GridData.FILL;
    gd.horizontalSpan = numColumns - 1;
    gd.grabExcessHorizontalSpace = true;
    textField.setLayoutData(gd);
  }

  @Override
  protected void doLoad() {
    if (textField != null) {
      String value = getPreferenceStore().getString(getPreferenceName());
      textField.setText(value);
    }
  }

  @Override
  protected void doLoadDefault() {
    if (textField != null) {
      String value = getPreferenceStore().getDefaultString(getPreferenceName());
      textField.setText(value);
      oldValue = value;
    }
  }

  @Override
  protected void doStore() {
    getPreferenceStore().setValue(getPreferenceName(), textField.getText());
  }

  @Override
  public int getNumberOfControls() {
    return 2;
  }

  protected Text getTextControl(Composite parent) {
    if (textField == null) {
      textField = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
      textField.setFont(parent.getFont());
      textField.addModifyListener(e -> valueChanged(e));
    }
    return textField;
  }

  public boolean isValid() {
    final String text = textField.getText();
    try {
      final JeanClaudeConfig config = gson.fromJson(text, JeanClaudeConfig.class);
      return config != null && config.isActionsValid();
    } catch (Exception e) {
      System.err.println("Configuration is invalid: " + text);
      e.printStackTrace();
      return false;
    }
  }

  private void valueChanged(ModifyEvent e) {
    setPresentsDefaultValue(false);
    refreshValidState();

    final boolean newState = isValid();
    fireStateChanged(IS_VALID, wasOk, newState);
    wasOk = newState;

    final String newValue = textField.getText();
    fireValueChanged(VALUE, oldValue, newValue);
    oldValue = newValue;
  }
}
