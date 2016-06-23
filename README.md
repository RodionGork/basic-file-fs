# basic-file-fs

Simple implementation of file-based virtual file system in Java.

This library allows to store files and folders inside a single physical file. It does not provide API fully-compatible with
`java.io`. The storage is automatically resized at every operation. All operations mentioned below are implemented efficiently
(in the sense that they do not take up more time or disk space than you naturally expect).

Simple console tool allowing to exchange files between normal and virtual FS is added.

Features: folders and files creation, deletion, renaming, reading, writing, appending (no random access however).
