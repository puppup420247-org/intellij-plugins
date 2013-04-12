package com.intellij.javascript.karma.execution;

import com.intellij.javascript.karma.KarmaBundle;
import com.intellij.lang.javascript.JavaScriptFileType;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.PathChooserDialog;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Sergey Simonchik
 */
public class KarmaRunConfigurationEditor extends SettingsEditor<KarmaRunConfiguration> {

  private final JComponent myRootComponent;
  private final TextFieldWithHistoryWithBrowseButton myConfigPathTextFieldWithBrowseButton;

  public KarmaRunConfigurationEditor(@NotNull Project project) {
    myConfigPathTextFieldWithBrowseButton = createConfigurationFileTextField(project);
    JPanel panel = new JPanel(new BorderLayout(0, 4));
    panel.add(new JLabel("Configuration file (usually *.conf.js):"), BorderLayout.NORTH);
    panel.add(myConfigPathTextFieldWithBrowseButton, BorderLayout.CENTER);
    myRootComponent = panel;
  }

  @NotNull
  private static TextFieldWithHistoryWithBrowseButton createConfigurationFileTextField(@NotNull final Project project) {
    TextFieldWithHistoryWithBrowseButton textFieldWithHistoryWithBrowseButton = new TextFieldWithHistoryWithBrowseButton();
    final TextFieldWithHistory textFieldWithHistory = textFieldWithHistoryWithBrowseButton.getChildComponent();
    textFieldWithHistory.addPopupMenuListener(new PopupMenuListener() {
      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        List<VirtualFile> newFiles = listPossibleConfigFilesInProject(project);
        List<String> newFilePaths = ContainerUtil.map(newFiles, new Function<VirtualFile, String>() {
          @Override
          public String fun(VirtualFile file) {
            return FileUtil.toSystemDependentName(file.getPath());
          }
        });
        Collections.sort(newFilePaths);

        Set<String> allPaths = ContainerUtil.newLinkedHashSet();
        allPaths.addAll(textFieldWithHistory.getHistory());
        allPaths.addAll(newFilePaths);
        textFieldWithHistory.setHistory(ContainerUtil.newArrayList(allPaths));

        // one-time initialization
        textFieldWithHistory.removePopupMenuListener(this);
      }

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
      }

      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {
      }
    });

    FileChooserDescriptor fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor();
    fileChooserDescriptor.putUserData(PathChooserDialog.NATIVE_MAC_CHOOSER_SHOW_HIDDEN_FILES, Boolean.TRUE);

    textFieldWithHistoryWithBrowseButton.addBrowseFolderListener(
      project,
      new ComponentWithBrowseButton.BrowseFolderActionListener<TextFieldWithHistory>(
        KarmaBundle.message("runConfiguration.config_file.browse_dialog.title"),
        null,
        textFieldWithHistoryWithBrowseButton,
        project,
        fileChooserDescriptor,
        TextComponentAccessor.TEXT_FIELD_WITH_HISTORY_WHOLE_TEXT
      ),
      true
    );
    FileChooserFactory.getInstance().installFileCompletion(
      textFieldWithHistory.getTextEditor(),
      fileChooserDescriptor,
      true,
      project
    );
    return textFieldWithHistoryWithBrowseButton;
  }

  @NotNull
  private static List<VirtualFile> listPossibleConfigFilesInProject(@NotNull Project project) {
    GlobalSearchScope scope = ProjectScope.getContentScope(project);
    Collection<VirtualFile> files = FileTypeIndex.getFiles(JavaScriptFileType.INSTANCE, scope);
    List<VirtualFile> result = ContainerUtil.newArrayList();
    for (VirtualFile file : files) {
      if (file.getName().endsWith(".conf.js")) {
        result.add(file);
      }
    }
    return result;
  }

  @Override
  protected void resetEditorFrom(@NotNull KarmaRunConfiguration runConfiguration) {
    KarmaRunSettings runSettings = runConfiguration.getRunSetting();
    myConfigPathTextFieldWithBrowseButton.getChildComponent().setText(runSettings.getConfigPath());
  }

  @Override
  protected void applyEditorTo(@NotNull KarmaRunConfiguration runConfiguration) throws ConfigurationException {
    String configPath = myConfigPathTextFieldWithBrowseButton.getChildComponent().getText();
    KarmaRunSettings.Builder builder = new KarmaRunSettings.Builder();
    builder.setConfigPath(configPath);
    runConfiguration.setRunSettings(builder.build());
  }

  @NotNull
  @Override
  protected JComponent createEditor() {
    return myRootComponent;
  }

  @Override
  protected void disposeEditor() {
  }
}
