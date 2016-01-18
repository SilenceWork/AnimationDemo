该工程为demo工程。本身无意义。

clean/clean.py脚本清理无用资源文件。

调用方式：
进入clean目录，执行python clean.py

其中white.list为白名单文件。
默认包含colors.xml;strings.xml;dimens.xml;其中的内容没有被引用，不能删除文件。
白名单文件为文本文件，每行可输入一个文件名,脚本会根据文件名判断是否不处理。
