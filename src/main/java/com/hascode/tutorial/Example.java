package com.hascode.tutorial;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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

		InputStream istream = Example.class.getResourceAsStream("/book.xml");
		Path xml = data.resolve("book.xml"); // /data/book.xml
		Files.copy(istream, xml, StandardCopyOption.REPLACE_EXISTING);

		Files.list(data).forEach(file -> {
			try {
				System.out.println(String.format("%s (%db)", file, Files.readAllBytes(file).length));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
