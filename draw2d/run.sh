#! /bin/sh

if [ -z "$GRAALVM_HOME" ]; then
	GRAALVM_HOME=$HOME/tools/graalvm
fi

LIBS_3RD=../../third-party
SWT_HOME=$LIBS_3RD/swt
SKIJA_HOME=$LIBS_3RD/skija
RCP_HOME=$LIBS_3RD/rcp
GEF_HOME=$LIBS_3RD/gef
CP=$SWT_HOME/swt.jar:$SKIJA_HOME/skija.jar:\
$RCP_HOME/plugins/org.eclipse.jface_3.22.0.v20201106-0834.jar:\
$RCP_HOME/plugins/org.eclipse.core.commands_3.9.800.v20201021-1339.jar:\
$RCP_HOME/plugins/org.eclipse.equinox.common_3.14.0.v20201102-2053.jar:\
$RCP_HOME/plugins/org.eclipse.ui.workbench_3.122.0.v20201122-1345.jar:\
$GEF_HOME/plugins/org.eclipse.draw2d_3.10.100.201606061308.jar:\
$GEF_HOME/plugins/org.eclipse.gef_3.11.0.201606061308.jar:\
$GEF_HOME/plugins/org.eclipse.zest.core_1.5.300.201606061308.jar:\
$GEF_HOME/plugins/org.eclipse.zest.layouts_1.1.300.201606061308.jar

for arg in "$@"; do
  if [ "$arg" = "-trace" ]; then
    rm -fr $HOME/.swt
    JAVA_OPTS="$JAVA_OPTS -agentlib:native-image-agent=config-output-dir=res/META-INF/native-image"
  elif [ "$arg" = "-debug" ]; then
    JAVA_OPTS="$JAVA_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=8000"
  else
    JAVA_OPTS="$JAVA_OPTS $arg"
  fi
done

$GRAALVM_HOME/bin/java $JAVA_OPTS -Djava.library.path=$SKIJA_HOME -cp bin:res:$CP com.spket.demo.draw2d.Draw2DApp
