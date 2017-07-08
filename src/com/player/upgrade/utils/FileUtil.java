package com.player.upgrade.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public final class FileUtil {

	private static FileChannel inputChannel;
	private static FileChannel outputChannel = null;
	private static FileInputStream fis = null;
	private static FileOutputStream fos = null;

	/**
	 * 
	 * @param fileName
	 *            文件名
	 * @param split
	 *            分隔符
	 * @param defaultFileName
	 *            默认文件名
	 * @return 后缀
	 */
	public static String getSubFileName(String fileName, char split, String defaultFileName) {
		if (fileName == null || fileName.isEmpty()) {
			return defaultFileName;
		} else if (fileName.lastIndexOf(split) == -1) {
			return fileName;
		}
		return fileName.substring(fileName.lastIndexOf(split) + 1);
	}

	public static void copyFile(Path sourcePath, Path destPath) throws IOException {
		if (Files.exists(sourcePath) && !Files.isDirectory(sourcePath)) {
			Files.createDirectories(destPath.getParent());
			copyFile(sourcePath.toFile(), destPath.toFile());
		}
	}

	/**
	 * copy file
	 * 
	 * @param source
	 * @param dest
	 */
	public static void copyFile(File source, File dest) throws IOException {
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(dest);
			inputChannel = fis.getChannel();
			outputChannel = fos.getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} finally {
			closeSource();
		}
	}

	private static void closeSource() throws IOException {
		if (fis != null) {
			fis.close();
		}
		if (fos != null) {
			fos.close();
		}
		if (inputChannel != null) {
			inputChannel.close();
		}
		if (outputChannel != null) {
			outputChannel.close();
		}
		fis = null;
		fos = null;
		inputChannel = null;
		outputChannel = null;
	}

	/**
	 * copy folder contain DIR name
	 */
	public static void copyDirs(Path source, Path target, CopyOption... options) throws IOException {
		copyDirs(source, target, true, options);
	}

	public static void copyDirs(Path source, Path target, boolean containDirName, CopyOption... options)
			throws IOException {
		if (null == source || !Files.isDirectory(source))
			throw new IllegalArgumentException("source must be directory");

		Path dest;
		if (containDirName) {
			dest = target.resolve(source.getFileName());
		} else {
			dest = target;
		}
		// 如果相同则返回
		if (Files.exists(dest) && Files.isSameFile(source, dest))
			return;
		// 目标文件夹不能是源文件夹的子文件夹
		if (isSub(source, dest))
			throw new IllegalArgumentException("dest must not  be sub directory of source");

		Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				// create targetPath subDir
				Path subDir = 0 == dir.compareTo(source) ? dest
						: dest.resolve(dir.subpath(source.getNameCount(), dir.getNameCount()));
				Files.createDirectories(subDir);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.copy(file, dest.resolve(file.subpath(source.getNameCount(), file.getNameCount())), options);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	public static boolean deleteDir(Path path) {
		boolean de = false;
		if (Files.exists(path)) {
			try {
				Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
						Files.deleteIfExists(filePath);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dirPath, IOException exc) throws IOException {
						Files.deleteIfExists(dirPath);
						return FileVisitResult.CONTINUE;
					}
				});
				de = true;
			} catch (IOException e) {
			}
		}
		return de;
	}

	/**
	 * 判断sub是否与parent相等或在其之下
	 * parent必须存在，且必须是directory,否则抛出{@link IllegalArgumentException}
	 * 
	 * @param parent
	 * @param sub
	 */
	public static boolean sameOrSub(Path parent, Path sub) throws IOException {
		if (null == parent)
			throw new NullPointerException("parent is null");
		if (!Files.exists(parent) || !Files.isDirectory(parent))
			throw new IllegalArgumentException(String.format("the parent not exist or not directory %s", parent));
		while (null != sub) {
			if (Files.exists(sub) && Files.isSameFile(parent, sub))
				return true;
			sub = sub.getParent();
		}
		return false;
	}

	/**
	 * 判断sub是否在parent之下的文件或子文件夹
	 * 
	 * @param parent
	 * @param sub
	 */
	public static boolean isSub(Path parent, Path sub) throws IOException {
		return (null == sub) ? false : sameOrSub(parent, sub.getParent());
	}
}