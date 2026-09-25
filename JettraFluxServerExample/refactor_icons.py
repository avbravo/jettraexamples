import os
import re

def to_constant_name(icon_name):
    # e.g., fa-address-book -> ADDRESS_BOOK
    # fa-exclamation-triangle -> EXCLAMATION_TRIANGLE
    name = icon_name.replace("fa-", "").upper().replace("-", "_")
    return name

icon_constants = set([
    "CHECK", "REDO", "PLUS", "SEARCH", "BELL", "COMMENT_ALT", "BARS", "CALENDAR",
    "HOME", "TH_LARGE", "COMMENTS", "CUBE", "MOUSE_POINTER", "WINDOW_MAXIMIZE",
    "LAYER_GROUP", "SIGN_OUT_ALT", "SYNC", "FILTER", "USER", "USERS", "COG",
    "ENVELOPE", "CHART_BAR", "TRASH", "EDIT", "SAVE", "TIMES", "INFO_CIRCLE",
    "EXCLAMATION_TRIANGLE", "EXCLAMATION_CIRCLE", "CHECK_CIRCLE", "LIST"
])

def process_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    new_constants = set()
    
    # We look for "fas fa-something" or "fa fa-something"
    def replacer(match):
        full_str = match.group(0)
        fas_class = match.group(1) # e.g. fas fa-cog
        icon_name = fas_class.split(' ')[1] # e.g. fa-cog
        const_name = to_constant_name(icon_name)
        
        # We need to add to Icon.java if not present
        new_constants.add((const_name, fas_class))
        
        return 'Icon.' + const_name
        
    # Pattern to match: "fas fa-xyz"
    pattern = re.compile(r'"(fas\s+fa-[a-zA-Z0-9\-]+)"')
    
    if not pattern.search(content):
        return new_constants

    new_content = pattern.sub(replacer, content)
    
    # Add import if needed
    if 'io.jettra.flux.widgets.Icon' not in new_content and 'Icon.' in new_content:
        # Find package declaration
        pkg_pattern = re.compile(r'^package\s+[a-zA-Z0-9_.]+;$', re.MULTILINE)
        match = pkg_pattern.search(new_content)
        if match:
            insert_pos = match.end() + 1
            new_content = new_content[:insert_pos] + '\nimport io.jettra.flux.widgets.Icon;\n' + new_content[insert_pos:]

    with open(filepath, 'w') as f:
        f.write(new_content)
        
    return new_constants

java_dir = '/home/avbravo/NetBeansProjects/jettrastack_local/JettraWorkspace/FolderExample/JettraFluxExample/src/main/java'
all_new_constants = set()

for root, _, files in os.walk(java_dir):
    for f in files:
        if f.endswith('.java'):
            filepath = os.path.join(root, f)
            constants = process_file(filepath)
            all_new_constants.update(constants)

# Now update Icon.java
icon_java_path = '/home/avbravo/NetBeansProjects/jettrastack_local/JettraWorkspace/FolderServer/JettraFlux/src/main/java/io/jettra/flux/widgets/Icon.java'
with open(icon_java_path, 'r') as f:
    icon_content = f.read()

# Insert new constants before public static class Bootstrap
insert_idx = icon_content.find('public static class Bootstrap')
if insert_idx != -1:
    to_insert = ""
    for const_name, fas_class in all_new_constants:
        if f"String {const_name} =" not in icon_content:
            to_insert += f'    public static final String {const_name} = "{fas_class}";\n'
    
    if to_insert:
        new_icon_content = icon_content[:insert_idx] + to_insert + "\n    " + icon_content[insert_idx:]
        with open(icon_java_path, 'w') as f:
            f.write(new_icon_content)

print("Refactoring complete.")
