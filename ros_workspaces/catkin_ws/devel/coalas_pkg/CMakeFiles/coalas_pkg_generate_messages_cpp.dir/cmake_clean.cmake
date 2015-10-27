FILE(REMOVE_RECURSE
  "CMakeFiles/coalas_pkg_generate_messages_cpp"
  "../devel/include/coalas_pkg/EncodersOutput.h"
  "../devel/include/coalas_pkg/JoystickOutput.h"
  "../devel/include/coalas_pkg/ChairOutput.h"
  "../devel/include/coalas_pkg/ChairState.h"
  "../devel/include/coalas_pkg/JoystickInput.h"
  "../devel/include/coalas_pkg/CollAvoidService.h"
  "../devel/include/coalas_pkg/ChairStateService.h"
)

# Per-language clean rules from dependency scanning.
FOREACH(lang)
  INCLUDE(CMakeFiles/coalas_pkg_generate_messages_cpp.dir/cmake_clean_${lang}.cmake OPTIONAL)
ENDFOREACH(lang)
