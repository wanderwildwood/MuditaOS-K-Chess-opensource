import os
import shutil
import re

base_dir = os.path.join(".", "src", "main", "res", "values")


def ensure_dir(directory):
    if not os.path.exists(directory):
        os.makedirs(directory)

#
def move_file(file_suffix):
    """
    Moves a strings XML file to the appropriate resource directory based on the given suffix.

    Args:
    file_suffix (str): The language suffix for the strings file. If empty, it's the default strings file.

    This function:
    1. Determines the source filename and destination directory based on the suffix.
    2. Constructs the full source and destination paths.
    3. Ensures the destination directory exists.
    4. If the source file exists, moves it to the destination.

    For example:
    - If suffix is "fr", it moves "strings-fr.xml" to "./src/main/res/values-fr/strings.xml"
    - If suffix is empty, it moves "strings.xml" to the base directory
    """
    if file_suffix:
        src_filename = f"strings-{file_suffix}.xml"
        dest_dir = os.path.join(".", "src", "main", "res", f"values-{file_suffix}")
    else:
        src_filename = "strings.xml"
        dest_dir = base_dir

    src_path = os.path.join(base_dir, src_filename)
    dest_path = os.path.join(dest_dir, "strings.xml")

    ensure_dir(dest_dir)

    if os.path.exists(src_path):
        shutil.move(src_path, dest_path)


suffixes = [""]
for filename in os.listdir(base_dir):
    match = re.match(r"strings-(\w+).xml", filename)
    if match:
        suffixes.append(match.group(1))

for suffix in suffixes:
    move_file(suffix)
