# Gradle

# Gradle Restriction
Only used Marver- or Ivy- compatible repositories and the filesystem.

# Marven
Build tool.

# The core model is based on tasks
Tasks themselves consist of:

Actions — pieces of work that do something, like copy files or compile source

Inputs — values, files and directories that the actions use or operate on

Outputs — files and directories that the actions modify or generate

# Gradle has several fixed build phases
It’s important to understand that Gradle evaluates and executes build scripts in three phases:

Initialization

Sets up the environment for the build and determine which projects will take part in it.

Configuration

Constructs and configures the task graph for the build and then determines which tasks need to run and in which order, based on the task the user wants to run.

Execution

Runs the tasks selected at the end of the configuration phase.

# build.gradle.kts
right click and Import Gradle Project

# Unable to get gradle home directory