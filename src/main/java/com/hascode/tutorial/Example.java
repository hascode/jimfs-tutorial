package com.hascode.tutorial;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class Example {

	public static void main(final String[] args) throws IOException, InterruptedException {
		runExample1();
		runExample2();
		runExample3();
	}

	private static void runExample1() throws IOException {
		System.out.println("Example 1: Creating, copying, reading files and directories");
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

	private static void runExample2() throws IOException {
		System.out.println("Example 2: Handling Symbolic Links");
		FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
		Path data = fs.getPath("/data");
		Files.createDirectory(data);

		Path hello = data.resolve("test.txt"); // /data/test.txt
		Files.write(hello, ImmutableList.of("hello world"), StandardCharsets.UTF_8);

		Path linkToHello = data.resolve("test.txt.link");
		Files.createSymbolicLink(linkToHello, hello);

		Files.list(data).forEach(file -> {
			try {
				System.out.println(String.format("%s (%db)", file, Files.readAllBytes(file).length));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private static void runExample3() throws IOException, InterruptedException {
		System.out.println("Example 3: Watching changes using a WatchService");
		FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
		Path data = fs.getPath("/data");
		Files.createDirectory(data);

		WatchService watcher = data.getFileSystem().newWatchService();
		Thread watcherThread = new Thread(() -> {
			WatchKey key;
			try {
				key = watcher.take();
				while (key != null) {
					for (WatchEvent<?> event : key.pollEvents()) {
						System.out.printf("event of type: %s received for file: %s\n", event.kind(), event.context());
					}
					key.reset();
					key = watcher.take();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, "CustomWatcher");
		watcherThread.start();
		data.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);

		Path hello = data.resolve("test.txt"); // /data/test.txt
		Files.write(hello, ImmutableList.of("hello world"), StandardCharsets.UTF_8);
	}
}
