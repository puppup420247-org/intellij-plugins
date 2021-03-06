package org.jetbrains.plugins.cucumber;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class CucumberBundle extends DynamicBundle {
  @NonNls private static final String BUNDLE = "org.jetbrains.plugins.cucumber.CucumberBundle";
  private static final CucumberBundle INSTANCE = new CucumberBundle();

  private CucumberBundle() { super(BUNDLE); }

  @NotNull
  public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, @NotNull Object... params) {
    return INSTANCE.getMessage(key, params);
  }
}
