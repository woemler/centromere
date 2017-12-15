package com.blueprint.centromere.cli;

import com.blueprint.centromere.cli.Printer.Level;
import com.blueprint.centromere.cli.commands.BatchCommandExecutor;
import com.blueprint.centromere.cli.commands.CreateCommandExecutor;
import com.blueprint.centromere.cli.commands.DeleteCommandExecutor;
import com.blueprint.centromere.cli.commands.ImportCommandExecutor;
import com.blueprint.centromere.cli.commands.ListCommandExecutor;
import com.blueprint.centromere.cli.commands.UpdateCommandExecutor;
import com.blueprint.centromere.cli.parameters.BatchCommandParameters;
import com.blueprint.centromere.cli.parameters.CreateCommandParameters;
import com.blueprint.centromere.cli.parameters.DeleteCommandParameters;
import com.blueprint.centromere.cli.parameters.ImportCommandParameters;
import com.blueprint.centromere.cli.parameters.ListCommandParameters;
import com.blueprint.centromere.cli.parameters.UpdateCommandParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * @author woemler
 */
@ShellComponent
public class JLineInputExecutor {
  
  private static final Logger logger = LoggerFactory.getLogger(JLineInputExecutor.class);

  private ImportCommandExecutor importCommandExecutor;
  private BatchCommandExecutor batchCommandExecutor;
  private ListCommandExecutor listCommandExecutor;
  private DeleteCommandExecutor deleteCommandExecutor;
  private CreateCommandExecutor createCommandExecutor;
  private UpdateCommandExecutor updateCommandExecutor;

  @ShellMethod(key = ImportCommandParameters.COMMAND, value = ImportCommandParameters.HELP)
  public void importCommand(@ShellOption(optOut = true) ImportCommandParameters parameters) throws Exception {
    Printer.print(String.format("Running import file command with arguments: %s",
        parameters.toString()), logger, Level.INFO);
    try {
      importCommandExecutor.run(parameters);
    } catch (Exception e) {
      throw new CommandLineRunnerException(e);
    }
  }

  @ShellMethod(key = BatchCommandParameters.COMMAND, value = BatchCommandParameters.HELP)
  public void batchCommand(@ShellOption(optOut = true) BatchCommandParameters parameters) throws Exception {
    Printer.print(String.format("Running import batch command with arguments: %s ",
        parameters.toString()), logger, Level.INFO);
    try {
      batchCommandExecutor.run(parameters.getFilePath());
    } catch (Exception e){
      throw new CommandLineRunnerException(e);
    }
  }

  @ShellMethod(key = CreateCommandParameters.COMMAND, value = CreateCommandParameters.HELP)
  public void createCommand(@ShellOption(optOut = true) CreateCommandParameters parameters) throws Exception {
    Printer.print(String.format("Creating new model record with arguments: %s",
        parameters.toString()), logger, Level.INFO);
    try {
      createCommandExecutor.run(parameters);
    } catch (Exception e){
      throw new CommandLineRunnerException(e);
    }
  }

  @ShellMethod(key = UpdateCommandParameters.COMMAND, value = UpdateCommandParameters.HELP)
  public void updateCommand(@ShellOption(optOut = true) UpdateCommandParameters parameters) throws Exception {
    Printer.print(String.format("Updating existing model record with arguments: %s",
        parameters.toString()), logger, Level.INFO);
    try {
      updateCommandExecutor.run(parameters);
    } catch (Exception e){
      throw new CommandLineRunnerException(e);
    }
  }

  @ShellMethod(key = ListCommandParameters.COMMAND, value = ListCommandParameters.HELP)
  public void listCommand(@ShellOption(optOut = true) ListCommandParameters parameters) throws Exception {
    String listable = "";
    if (parameters.getArgs() != null && !parameters.getArgs().isEmpty()) {
      listable = parameters.getArgs().get(0);
    }
    listCommandExecutor.run(listable, parameters.isShowDetails());
  }

  @ShellMethod(key = DeleteCommandParameters.COMMAND, value = DeleteCommandParameters.HELP)
  public void deleteCommand(@ShellOption(optOut = true) DeleteCommandParameters parameters) throws Exception {
    deleteCommandExecutor.run(parameters);
  }

  @Autowired
  public void setImportCommandExecutor(ImportCommandExecutor importCommandExecutor) {
    this.importCommandExecutor = importCommandExecutor;
  }

  @Autowired
  public void setBatchCommandExecutor(BatchCommandExecutor batchCommandExecutor) {
    this.batchCommandExecutor = batchCommandExecutor;
  }

  @Autowired
  public void setListCommandExecutor(ListCommandExecutor listCommandExecutor) {
    this.listCommandExecutor = listCommandExecutor;
  }

  @Autowired
  public void setDeleteCommandExecutor(DeleteCommandExecutor deleteCommandExecutor) {
    this.deleteCommandExecutor = deleteCommandExecutor;
  }

  @Autowired
  public void setCreateCommandExecutor(CreateCommandExecutor createCommandExecutor) {
    this.createCommandExecutor = createCommandExecutor;
  }

  @Autowired
  public void setUpdateCommandExecutor(
      UpdateCommandExecutor updateCommandExecutor) {
    this.updateCommandExecutor = updateCommandExecutor;
  }
  
}
