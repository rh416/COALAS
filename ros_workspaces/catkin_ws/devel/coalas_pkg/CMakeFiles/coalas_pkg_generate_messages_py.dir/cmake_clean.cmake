FILE(REMOVE_RECURSE
  "CMakeFiles/coalas_pkg_generate_messages_py"
  "../devel/lib/python2.7/dist-packages/coalas_pkg/msg/_EncodersOutput.py"
  "../devel/lib/python2.7/dist-packages/coalas_pkg/msg/_JoystickOutput.py"
  "../devel/lib/python2.7/dist-packages/coalas_pkg/msg/_ChairOutput.py"
  "../devel/lib/python2.7/dist-packages/coalas_pkg/msg/_ChairState.py"
  "../devel/lib/python2.7/dist-packages/coalas_pkg/msg/_JoystickInput.py"
  "../devel/lib/python2.7/dist-packages/coalas_pkg/srv/_CollAvoidService.py"
  "../devel/lib/python2.7/dist-packages/coalas_pkg/srv/_ChairStateService.py"
  "../devel/lib/python2.7/dist-packages/coalas_pkg/msg/__init__.py"
  "../devel/lib/python2.7/dist-packages/coalas_pkg/srv/__init__.py"
)

# Per-language clean rules from dependency scanning.
FOREACH(lang)
  INCLUDE(CMakeFiles/coalas_pkg_generate_messages_py.dir/cmake_clean_${lang}.cmake OPTIONAL)
ENDFOREACH(lang)
