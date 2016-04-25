rm sample.fs
rm -r src
echo "Creating new filesystem and making 3 copies of src folder there, then deleting first and last:"
java -jar ../target/basic-fs.jar sample.fs < first-run.txt
echo "Let us see the container file:"
ls -l sample.fs
echo "Now fetching the remaining src folder into current directory:"
java -jar ../target/basic-fs.jar sample.fs < second-run.txt
echo "And run comparison tool on ./src and ../src folders:"
diff -ur src ../src
echo "If no diff messages are seen, all is fine!"

