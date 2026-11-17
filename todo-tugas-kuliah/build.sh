#!/bin/bash
# Compile + buat JAR. Jalankan dari folder root proyek.
set -e
rm -rf out && mkdir -p out
javac -d out $(find src -name "*.java")
printf "Main-Class: todo.Main\n" > out/manifest.txt
jar cfm todo-app.jar out/manifest.txt -C out todo
echo "Berhasil: todo-app.jar  (jalankan: java -jar todo-app.jar)"
