import os
import sys

CURRENT_LANG_EXTENSION = {".cpp", ".java", ".py", ".c"}
HELP_OPTIONS = {"-h", "-help", "--help"}


def java(list_of_files: list, dir_name: str, main_file_name: str):
    # import_lines will come before the code
    # import_line contains import part
    # code_list contains code part from each file


    import_lines, code_list = [], []
    print("in java")

    import_keyword = "import"

    for files in list_of_files:
        f = open(os.path.join(dir_name, files))
        lines = f.readlines()
        f.close()

        code_lines = []

        for line in lines:
            _line = line.strip().split(" ")

            # Separating import lines from code lines
            if _line[0] == import_keyword:
                import_lines.append(line.strip())
            else:
                code_lines.append(line)

        code_data = "".join(code_lines).strip()

        code_list.append(code_data)

    final_code = "\n".join(import_lines) + "\n" + "\n".join(code_list)
    final_code = final_code.replace("\\n", "\\\\n").replace("public class", "class") \
        .replace("\\", "\\\\").replace("\"", "\\\"").replace("class " + main_file_name, "public class Main") \
        .replace("\n", "\\n").replace("\t", "\\t")

    return final_code


def cpp(list_of_files: list, dir_name: str):
    print("in cpp")
    # NOTE: Header files should not use code from other user-defined header file
    #       If this case arises, then we need to add the files in order
    #       where the dependent file comes after the independent file

    # If there are .h files, the contents of the should be copied in main code
    # Also #include "file_name.h" should be removed from main file

    # the import statements can come in the middle

    header_files, cpp_files = [], []

    # list of files is a list of file names
    for file in list_of_files:
        header_files.append(file) if file.endswith(".h") else cpp_files.append(file)

    final_code = ""
    for header_names in header_files:
        with open(os.path.join(dir_name, header_names)) as f:
            lines = f.readlines()

        final_code += "".join(lines) + "\n"

    for cpp_names in cpp_files:
        with open(os.path.join(dir_name, cpp_names)) as f:
            lines = f.readlines()

        # Remove user-defined header
        for line in lines:
            for header_names in header_files:
                if header_names in line:
                    try:
                        lines.remove(line)
                    except:
                        print("ERROR while deleting:" + line)

        final_code += "".join(lines) + "\n"

    final_code = final_code.replace("\\n", "\\\\n").replace("\"", "\\\"").replace("\n", "\\n").replace("\t", "\\t")

    return final_code


def py(list_of_files: list, dir_name: str):
    return ""
    pass


def unzip(zip_file_name):
    zip_ref = zipfile.ZipFile(zip_file_name, 'r')
    dir_name = zip_file_name.split(".")[0]
    zip_ref.extractall(dir_name)
    zip_ref.close()

    return dir_name


def main():
    dir_name = sys.argv[1]
    invalid_syntax = False

    if len(sys.argv) != 4:
        print("Invalid syntax")
        invalid_syntax = True

    if dir_name in HELP_OPTIONS or invalid_syntax:
        print("HELP MENU")
        print("Syntax: python3 combine.py [DIR_NAME] [main_file_name] [OUTPUT_TEST_FILE_NAME]")
        print("[DIR_NAME]: The directory of your code that will be combined {only java and cpp files supported yet}")
        print("[main_file_name]: The file where main method of the code exists")
        print("[OUTPUT_TEST_FILE_NAME]: The text file containing the JSON valid string version of the code ")
        return

    main_file_name = sys.argv[2]
    output_file = sys.argv[3]

    list_of_files = os.listdir(dir_name)
    print(dir_name, list_of_files)

    language = ""

    # Determine which language the code is written
    for file in list_of_files:
        file_name, extension = os.path.splitext(file)

        if extension in CURRENT_LANG_EXTENSION:
            language = extension
            break

    if language == ".cpp" or language == ".c":
        final_code = cpp(list_of_files, dir_name)
    elif language == ".java":
        final_code = java(list_of_files, dir_name, main_file_name)
    elif language == ".py":
        final_code = py(list_of_files, dir_name)
    else:
        final_code = ""

    if final_code != "":
        with open(output_file, 'w') as f_output:
            f_output.write(final_code)

    else:
        print("final code not generated, allowed extensions", CURRENT_LANG_EXTENSION)


if __name__ == "__main__":
    main()
