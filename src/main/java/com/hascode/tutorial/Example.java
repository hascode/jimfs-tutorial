package com.hascode.tutorial;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class Example {

	public static void main(final String[] args) throws IOException {
		FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
		Path data = fs.getPath("/data");
		Files.createDirectory(data);

		Path hello = data.resolve("test.txt"); // /data/test.txt
		Files.write(hello, ImmutableList.of("hello world"), StandardCharsets.UTF_8);

		Path csv = data.resolve("data.csv"); // /data/data.csv
		Files.write(csv, ImmutableList.of("test1,test2\ntest3,test4"), StandardCharsets.UTF_8);

		Files.list(data).forEach(System.out::println);
	}
}
